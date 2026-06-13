# JusDrive: Comprehensive Project Knowledge Base

This document serves as the definitive reference for every technical concept, design pattern, and architectural decision implemented in the JusDrive project.

---

## 📦 Part 1: Architectural Foundation

### 1.1 Microservices Architecture
**The Concept**: Decomposing a large application into a collection of small, autonomous services that communicate over a network.
- **Why it was used**: To achieve **Independent Scalability** (scale only the booking service during peak hours), **Fault Isolation** (a crash in the Notification service doesn't stop users from browsing cars), and **Technology Agility**.
- **Trade-off**: Increased complexity in deployment and inter-service communication (solved by Gateway and Eureka).

### 1.2 API Gateway Pattern
**The Concept**: A single entry point for all clients. It handles request routing, composition, and protocol translation.
- **Implementation**: `Spring Cloud Gateway`.
- **Key Responsibilities**:
    - **Routing**: Directs `/api/bookings/**` $\rightarrow$ `booking-service`.
    - **Security**: Validates JWT tokens before requests reach internal services.
    - **Cross-Cutting Concerns**: Handles CORS, rate limiting, and logging in one place.

### 1.3 Service Discovery (The Registry)
**The Concept**: A dynamic directory where services register their location (IP/Port) so other services can find them without hardcoded configurations.
- **Implementation**: `Netflix Eureka Server`.
- **Mechanism**: 
    - **Registration**: At startup, each service tells Eureka: *"I am the 'car-service' and I'm at 192.168.1.10:8081"*.
    - **Heartbeat**: Services send a periodic signal to Eureka to prove they are still alive.
    - **Discovery**: When `BookingService` needs `CarService`, it asks Eureka for the current address.

### 1.4 RESTful API Design
**The Concept**: Using HTTP methods (`GET`, `POST`, `PUT`, `DELETE`) and URIs to manage resources.
- **Patterns Used**:
    - **Resource-based URIs**: `/api/bookings/{id}/status` instead of `/updateStatus`.
    - **Statelessness**: Each request contains all information needed for processing (JWT in header).
    - **Standard Response Codes**: `200 OK` (Success), `401 Unauthorized` (Token missing/invalid), `404 Not Found` (Resource missing).

---

## 🛡️ Part 2: Security & Identity Management

### 2.1 JSON Web Tokens (JWT)
**The Concept**: A compact, URL-safe means of representing claims to be transferred between two parties.
- **Structure**: `Header.Payload.Signature`.
- **Why JWT over Sessions?**: 
    - **Statelessness**: The server doesn't need to store session IDs in a DB.
    - **Scalability**: Any instance of the API Gateway can validate the token using the secret key.
- **Implementation**: `jjwt` library used for signing (HS256 algorithm).

### 2.2 Password Hashing (BCrypt)
**The Concept**: Converting a plain-text password into a fixed-length hash that is computationally expensive to reverse.
- **Implementation**: `BCryptPasswordEncoder`.
- **Key Feature**: **Salting**. BCrypt adds a random salt to every password, ensuring that two users with the same password have different hashes, preventing "Rainbow Table" attacks.

### 2.3 Identity Propagation Pattern
**The Concept**: The process of passing the authenticated user's identity across microservice boundaries.
- **The Problem**: Every service would need to parse the JWT, leading to redundancy.
- **The Solution**: The API Gateway parses the JWT once $\rightarrow$ Extracts `userId` $\rightarrow$ Injects it into the `X-User-Id` HTTP header $\rightarrow$ Downstream services trust this header.

---

## ⚙️ Part 3: Backend Engineering (Spring Boot)

### 3.1 Data Access & Persistence
- **Spring Data JPA**: An abstraction over Hibernate that allows defining repositories as interfaces.
- **Repository Pattern**: Decouples the business logic from the data access layer (e.g., `CustomerRepository`).
- **Entity Mapping**: Use of `@Entity` and `@Table` to map Java classes to MySQL tables.

### 3.2 Declarative Communication (OpenFeign)
**The Concept**: Defining an interface for an external API, and letting the framework generate the implementation.
- **Benefit**: Reduces boilerplate code for HTTP requests.
- **Integration**: Works seamlessly with Eureka for load-balanced routing.

### 3.3 Distributed Consistency (Saga-Lite)
**The Concept**: Managing data integrity across multiple databases without a global transaction manager.
- **Compensating Transactions**: When a distributed action fails at step $N$, the system explicitly executes "undo" actions for steps $1$ to $N-1$.
- **Example**: If `updateCarStatus` fails $\rightarrow$ execute `deleteBooking`.

### 3.4 Aspect-Oriented Programming (AOP)
**The Concept**: Separating "cross-cutting concerns" (like logging, auditing, or transaction management) from the actual business logic.
- **Implementation**: `@Aspect` and `@Around` advice in `LoggingAspect`.
- **Result**: Business methods remain clean; logging is handled automatically in the background.

### 3.5 Java 21 & Modern Tooling
- **Streams API**: Used for complex data transformations (e.g., generating daily/weekly reports by grouping bookings).
- **Lombok**: Reduces boilerplate code (`@Data`, `@AllArgsConstructor`) using annotation processing.
- **Validation API**: Using `@Valid` and `@NotNull` to ensure data integrity at the Controller entry point.

---

## 🎨 Part 4: Frontend Engineering (Angular)

### 4.1 Core Framework Concepts
- **Component-Based Architecture**: Breaking the UI into reusable pieces (e.g., `SidebarComponent`, `HeaderComponent`).
- **Dependency Injection (DI)**: Injecting services (like `BookingService`) into components to decouple UI from data fetching.
- **Server-Side Rendering (SSR)**: Rendering the app on the server first to improve SEO and initial page load speed.

### 4.2 Reactive Programming (RxJS)
**The Concept**: Handling asynchronous data streams using Observables.
- **Observables**: `HttpClient` returns observables that emit data when the server responds.
- **Piping & Operators**: Using `.pipe()` and `tap()` in the Interceptor to modify the data flow.

### 4.3 State Management & Interception
- **HTTP Interceptors**: Functional middleware that clones every request to attach the `Authorization: Bearer <token>` header.
- **LocalStorage**: Used for persisting the JWT token across browser refreshes.
- **Environment Files**: Decoupling the API Gateway URL from the code to allow easy switching between `dev` and `prod`.

### 4.4 UI/UX Implementation
- **PrimeNG**: Used for enterprise-grade components (DataTables, Dialogs) to ensure a professional look and feel.
- **Chart.js**: Used in the Owner Dashboard to visualize booking trends (Daily/Weekly/Monthly).
- **Bootstrap 5**: Used for a responsive, mobile-first grid layout.

---

## 🚢 Part 5: Infrastructure & DevOps

### 5.1 Containerization (Docker)
**The Concept**: Packaging the application and its dependencies (JDK, Node, etc.) into an image that runs identically on any machine.
- **Dockerfile**: Defines the build steps (Multi-stage builds for Angular and Spring Boot).
- **Docker Compose**: Orchastrates multiple containers (MySQL, Eureka, Gateway, Services) to start in the correct order.

### 5.2 Database Management (MySQL)
- **Relational Model**: Using primary and foreign keys to maintain relationships between Customers, Owners, Cars, and Bookings.
- **Persistence**: Using Docker volumes to ensure database data survives container restarts.

### 5.3 Maven Build Lifecycle
- **Dependency Management**: Using `pom.xml` to manage versions and ensure consistent builds across the team.
- **Parent POM**: Using `spring-boot-starter-parent` to inherit default configurations and dependency versions.
