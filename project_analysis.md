# JusDrive Project Deep Analysis

## 1. High-Level Architecture
JusDrive is a comprehensive car-rental platform built using a **Microservices Architecture**. It separates concerns across multiple independent services, allowing for scalability, independent deployment, and technology flexibility.

### Architecture Diagram (Conceptual)
`Angular Frontend` $\xrightarrow{\text{REST/JSON}}$ `API Gateway` $\xrightarrow{\text{Routing}}$ `Microservices (Auth, Booking, Car, Enquiry, Notification)` $\xrightarrow{\text{Feign}}$ `Other Microservices` $\rightarrow$ `MySQL Databases`

---

## 2. Backend Analysis (Spring Boot)

### 2.1 Service Breakdown
- **API Gateway**: 
  - **Role**: Central entry point for all client requests.
  - **Implementation**: Uses `Spring Cloud Gateway`.
  - **Key Feature**: `JwtAuthFilter` intercepts requests, validates the JWT token via `JwtUtils`, and propagates the User ID to downstream services via the `X-User-Id` HTTP header. This removes the need for every service to validate the token again.
- **Auth Service**: 
  - **Role**: Identity and Access Management (IAM).
  - **Implementation**: Handles customer and owner registration, login, and password recovery.
  - **Concepts**:
    - **Bcrypt**: Password hashing for security.
    - **JWT (JSON Web Token)**: Stateless authentication.
    - **OTP System**: `OtpService` manages temporary codes for password resets.
- **Booking Service**: 
  - **Role**: Core business logic for car rentals.
  - **Implementation**: Manages the lifecycle of a booking (`PENDING` $\rightarrow$ `CONFIRMED` $\rightarrow$ `CANCELLED`).
  - **Key Logic**: 
    - **Availability Check**: Calls `CarService` via Feign to ensure the car is available before saving a booking.
    - **Distributed Consistency**: Implements a manual rollback mechanism—if updating the car status to `BOOKED` fails, it deletes the created booking to prevent data inconsistency.
    - **Reporting**: Generates analytical data (Daily/Weekly/Monthly) using Java Streams and `LocalDate` API.
- **Car Service**: 
  - **Role**: Inventory management of vehicles.
  - **Implementation**: Handles car details, ownership, and availability status.
- **Enquiry Service**: 
  - **Role**: Communication bridge between customers and car owners.
- **Notification Service**: 
  - **Role**: Generic alerting system.
  - **Implementation**: Sends HTML-formatted emails using Java Mail Sender.
- **Service Registry (Eureka)**: 
  - **Role**: Service Discovery.
  - **Concept**: Allows services to find each other by name (e.g., `notification-service`) instead of hardcoded IP addresses.

### 2.2 Cross-Cutting Concerns
- **Inter-Service Communication**: Uses **Spring Cloud OpenFeign**, a declarative REST client that makes calling other services as simple as calling a local method.
- **Security**: 
  - **Statelessness**: No session state is stored on the server; identity is carried by the JWT.
  - **Header Propagation**: `X-User-Id` allows downstream services to know exactly which user is making the request without re-parsing the token.
- **Data Handling**: 
  - **Spring Data JPA**: Simplifies database interactions.
  - **DTO Pattern**: Uses Data Transfer Objects (e.g., `BookingWithCarDTO`) to avoid exposing internal entity structures to the frontend.
- **AOP (Aspect Oriented Programming)**: Implements `LoggingAspect` to centralize logging across services without polluting business logic.

---

## 3. Frontend Analysis (Angular)

### 3.1 Technical Stack
- **Framework**: Angular 17+
- **Styling**: Bootstrap 5 + PrimeNG (for high-quality UI components like tables, dialogs, and inputs).
- **Visualizations**: Chart.js (used in owner dashboards for reporting).
- **Rendering**: SSR (Server Side Rendering) via `@angular/ssr` for better SEO and initial load performance.

### 3.2 Key Implementation Patterns
- **State Management (Authentication)**:
  - Stores the JWT token in `localStorage` upon successful login.
- **HTTP Interceptor**:
  - Implements a **Functional Interceptor** (`httpInterceptor`) that automatically attaches the `Authorization: Bearer <token>` header to every outgoing request.
  - Includes a performance logger to track API response times.
- **Service-Based Architecture**:
  - Logic is decoupled from components. `BookingService`, `AuthService`, etc., handle all API communication, making components "lean" and focused on the UI.
- **Environment Configuration**:
  - Uses `environments/environments.ts` to manage the `gatewayBaseUrl`, ensuring easy switching between development and production environments.

---

## 4. Full-Flow Example: Booking a Car
1. **UI**: User selects a car and clicks "Book Now".
2. **Angular**: `BookingService.bookCar()` sends a POST request to `/api/bookings/new`.
3. **Interceptor**: Adds the JWT token to the header.
4. **Gateway**: `JwtAuthFilter` validates the token $\rightarrow$ adds `X-User-Id` header $\rightarrow$ routes to `booking-service`.
5. **Booking Service**: 
   - Calls `car-service` (via Feign) to check if the car is available.
   - Saves the booking record to the MySQL DB.
   - Calls `car-service` (via Feign) to mark the car as `BOOKED`.
   - Calls `notification-service` (via Feign) to send a confirmation email.
6. **Response**: Success response travels back through the Gateway to the Angular UI.
