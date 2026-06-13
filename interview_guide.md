# TCS Prime Interview Guide: JusDrive Project

## 1. The "Elevator Pitch" (How to introduce the project)
"I developed **JusDrive**, a full-stack car rental platform built on a **Microservices Architecture**. The goal was to create a scalable system where customers can browse and book cars, and car owners can manage their fleet and track earnings through a dashboard. I used **Spring Boot** for the backend, **Angular 17** for the frontend, and **MySQL** for the database. To handle the complexity of multiple services, I implemented an **API Gateway** for centralized security and a **Service Registry (Eureka)** for discovery."

---

## 2. Key Technical Talking Points (The "How")

### A. Why Microservices? (Architectural Decision)
*   **Interviewer:** "Why did you choose microservices instead of a monolith?"
*   **Your Answer:** "I wanted to ensure **independent scalability**. For example, the `Booking Service` might experience higher load than the `Enquiry Service`. With microservices, I can scale only the booking service without affecting others. Additionally, it allows for **fault isolation**; if the `Notification Service` goes down, users can still book cars, and the system remains partially functional."

### B. Security & Authentication (The JWT Flow)
*   **Interviewer:** "How is security handled in your application?"
*   **Your Answer:** "I implemented a **stateless authentication** mechanism using **JWT (JSON Web Tokens)**. The flow is as follows:
    1. The user logs in via the `Auth Service`, which returns a signed JWT.
    2. The Angular frontend stores this token in `localStorage` and attaches it to every request using an **HTTP Interceptor**.
    3. The **API Gateway** acts as the single point of entry. It validates the token using a `JwtAuthFilter`.
    4. Once validated, the Gateway extracts the User ID and injects it as an `X-User-Id` header. This means downstream services don't need to re-validate the token, reducing latency and redundancy."

### C. Inter-Service Communication (OpenFeign)
*   **Interviewer:** "How do your services talk to each other?"
*   **Your Answer:** "I used **Spring Cloud OpenFeign**. It provides a declarative REST client, meaning I can define an interface and Spring handles the actual HTTP calls. For example, the `Booking Service` calls the `Car Service` to check vehicle availability before confirming a booking. This keeps the code clean and easy to maintain."

### D. Data Consistency (The "Rollback" Scenario)
*   **Interviewer:** "What happens if the booking is saved, but the car status fails to update?"
*   **Your Answer:** "That's a classic distributed transaction problem. In my current implementation, I used a **compensating transaction** approach. If the call to the `Car Service` to mark a car as 'BOOKED' fails, the `Booking Service` catches the exception and immediately deletes the pending booking record. This ensures the system doesn't end up in an inconsistent state where a car is booked but still listed as available."

---

## 3. Challenging Questions & Sample Answers

### Q1: "What was the most difficult part of this project?"
**Suggested Answer:** "Implementing the **cross-service identity propagation**. Initially, I was validating the JWT in every single service, which was repetitive. I solved this by moving the validation logic to the **API Gateway** and using the `X-User-Id` header. This architectural shift significantly cleaned up my service-level code and improved performance."

### Q2: "How did you optimize the frontend performance?"
**Suggested Answer:** "I used **Angular 17's SSR (Server Side Rendering)**. By rendering the initial page on the server, I reduced the 'Time to First Byte' and improved SEO. I also used **PrimeNG** for efficient UI components and a **Functional Interceptor** to log API latency, which helped me identify and optimize slow endpoints."

### Q3: "If you had more time, what would you improve?"
**Suggested Answer:** "I would replace the manual rollback in the Booking service with a **Saga Pattern** using a message broker like **Apache Kafka** or **RabbitMQ**. This would make the system truly eventually consistent and more resilient to temporary service outages."

---

## 4. Quick Reference Tech Stack Table
| Layer | Technology | Purpose |
| :--- | :--- | :--- |
| **Frontend** | Angular 17, PrimeNG, Bootstrap | UI/UX, State Mgmt, SSR |
| **API Layer** | Spring Cloud Gateway | Routing, Security, JWT Filtering |
| **Service Layer**| Spring Boot 3.5 | Business Logic (Microservices) |
| **Communication**| OpenFeign, Eureka | Inter-service calls, Service Discovery |
| **Security** | JWT, BCrypt | Stateless Auth, Password Hashing |
| **Database** | MySQL | Persistent Storage |
| **Deployment** | Docker | Containerization & Orchestration |
