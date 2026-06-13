# JusDrive: Conceptual Deep Dive

This document explains the "Why" and "How" of JusDrive by pairing core software engineering concepts with their actual implementation in the codebase.

---

## 1. The Perimeter: API Gateway & Security Filter
**Concept: Centralized Cross-Cutting Concerns**
Instead of each microservice implementing its own security logic (which leads to code duplication and maintenance nightmares), the **API Gateway** acts as a "bouncer." It intercepts all incoming requests, validates the user's identity, and only then forwards the request to the internal services.

### Implementation
`JusDriveSpringBoot/api-gateway/src/main/java/com/jusdrive/api_gateway/filter/JwtAuthFilter.java`

```java
@Override
public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
    ServerHttpRequest request = exchange.getRequest();

    // 1. White-listing: Allow public endpoints (Login/Register) to pass through
    if (EXCLUDED_URLS.stream().anyMatch(url -> request.getURI().getPath().contains(url))) {
        return chain.filter(exchange);
    }

    // 2. Token Extraction: Get the Bearer token from Authorization header
    String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION).replace("Bearer ", "");
    String userId = jwtUtils.extractUserId(token);

    if (userId == null) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    // 3. Identity Propagation: Inject User ID into the request header for downstream services
    ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
        .header("X-User-Id", userId)
        .build();

    return chain.filter(exchange.mutate().request(mutatedRequest).build());
}
```

**How it works conceptually:**
- **Filter Pattern**: It uses the `GatewayFilter` to intercept the request pipeline.
- **Header Mutation**: By adding the `X-User-Id` header, the gateway transforms a "security token" (which is hard to parse) into a "simple identity" (which is easy for internal services to use).

---

## 2. The Glue: Service Discovery & Declarative Clients
**Concept: Location Transparency**
In a dynamic cloud environment, services don't have fixed IP addresses (they change when they restart or scale). **Service Discovery (Eureka)** allows services to find each other by name. **OpenFeign** then provides a way to call these services using a simple Java interface instead of manual HTTP clients.

### Implementation
`JusDriveSpringBoot/auth-service/src/main/java/com/jusdrive/auth_service/mailClient/NotificationClient.java`

```java
@FeignClient(name = "notification-service") // <--- Discovery name
public interface NotificationClient {
    @PostMapping("/alert/send")
    void sendNotification(@RequestBody NotificationRequest request);
}
```

**How it works conceptually:**
- **Declarative Programming**: You don't write the code to create an `HttpClient`, set timeouts, or handle JSON serialization. You simply describe *what* the endpoint is, and Spring Cloud implements the *how* at runtime.
- **Dynamic Routing**: When `notification-service` is called, Feign asks Eureka: *"Where is the notification-service currently running?"* and routes the request to a healthy instance.

---

## 3. The Vault: Stateless Authentication
**Concept: JWT (JSON Web Tokens)**
Traditional sessions require a database to store user state on the server. JWTs move that state to the client. The server signs the token with a secret key; as long as the signature is valid, the server trusts the data inside the token without needing to look it up in a database.

### Implementation
`JusDriveSpringBoot/api-gateway/src/main/java/com/jusdrive/api_gateway/util/JwtUtils.java`

```java
public String generateToken(String userId) {
    return Jwts.builder()
            .setSubject(userId) // The payload: who is this user?
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000))
            .signWith(SECRET_KEY, SignatureAlgorithm.HS256) // The seal of authenticity
            .compact();
}
```

**How it works conceptually:**
- **Digital Signature**: The `signWith` method ensures that if a user tries to change their `userId` in the token, the signature will no longer match, and the Gateway will reject it.
- **Statelessness**: This allows the system to scale horizontally; any instance of the API Gateway can validate the token because they all share the same `SECRET_KEY`.

---

## 4. The Business Core: Distributed Consistency
**Concept: Compensating Transactions (Saga-Lite)**
In a microservices world, you cannot use a single `@Transactional` block across different databases. If you save a booking in `BookingService` but the `CarService` fails to update the car status, you have "ghost data." Since you can't "undo" a database commit in another service, you must perform a **compensating action** (a manual rollback).

### Implementation
`JusDriveSpringBoot/booking-service/src/main/java/com/jusdrive/booking_service/service/BookingServiceImpl.java`

```java
public Booking createBooking(Booking booking) {
    // 1. Validation call to another service
    CarAvailabilityResponse availability = carServiceClient.checkCarAvailability(booking.getCarId());
    if (!availability.isAvailable()) throw new RuntimeException("...");

    // 2. Local commit
    Booking savedBooking = bookingRepository.save(booking);

    try {
        // 3. Remote commit
        carServiceClient.updateCarStatus(savedBooking.getCarId(), "BOOKED");
    } catch (Exception e) {
        // 4. COMPENSATING ACTION: Manual rollback of step 2
        bookingRepository.deleteById(savedBooking.getBookingId());
        throw new RuntimeException("Booking failed... Rolled back.");
    }
    // ... notification logic
}
```

**How it works conceptually:**
- **Atomic-like Behavior**: It mimics atomicity. If the entire chain of events doesn't succeed, it reverts the local changes to ensure the system returns to its original valid state.

---

## 5. The Frontend Bridge: Request Interception
**Concept: The Observer/Interceptor Pattern**
Frontend applications shouldn't manually add tokens to every single API call. This is error-prone. An **Interceptor** acts as a middleware that "listens" to every outgoing request and modifies it automatically.

### Implementation
`JusDriveAngular/src/app/http.interceptor.ts`

```typescript
export const httpInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
    const token = localStorage.getItem('jwtToken');

    // Clone the request and inject the Authorization header
    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });

    // Pass the modified request to the next handler in the chain
    return next(cloned).pipe(
      tap((event) => {
          if (event instanceof HttpResponse) {
              // Conceptual addition: Telemetry/Logging
              console.log(`Request for ${req.urlWithParams} took ${Date.now() - started} ms.`);
          }
      })
    );
};
```

**How it works conceptually:**
- **Separation of Concerns**: The components (like `BookingComponent`) only care about *what* data to send. The Interceptor cares about *how* that data is authenticated and transported.
- **Immutability**: In Angular, requests are immutable. We use `.clone()` to create a new version of the request with the added header, preserving the original request's integrity.
