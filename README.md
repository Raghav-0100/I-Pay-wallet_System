💸 I-Pay - Production-Grade FinTech Backend Wallet System

📖 About The Project

I-Pay is a comprehensive Financial Technology (FinTech) application designed to replicate the core backend functionalities of payment processors like PayPal. Unlike simple CRUD applications, this project focuses on building a production-grade system capable of handling complex financial logic, distributed transactions, and high-concurrency environments.

The system is architected to ensure data integrity, scalability, and security, using industry-standard patterns for handling money and sensitive user data.

🏗 High-Level Architecture

The project follows a Microservices Architecture, decoupling the domain into independent, scalable services. This ensures that failures in one module (e.g., Rewards) do not impact critical paths (e.g., Core Payments) and mimics large-scale enterprise system design.

UML

⚙️ Key Technical Stack

Backend Framework: Java, Spring Boot

Architecture: Distributed Microservices

Database: PostgresSQL (Relational & ACID-compliant)

Messaging & Async: Apache Kafka (Event-Driven Architecture)

Security: Spring Security & JWT (Stateless Authentication)

Communication: REST APIs (Synchronous) & Kafka (Asynchronous)

Service Discovery & Config: Spring Cloud

🧩 Core Microservices

👤 User Service
Responsibilities: Handles user onboarding, authentication, and profile management.

Key Features:

Secure Auth: Implements JWT (JSON Web Token) for stateless, secure authentication.

Orchestrated Onboarding: Automatically triggers Wallet creation upon user registration.

Rollback Mechanism: Uses compensation transactions to delete the user if wallet creation fails, ensuring data consistency.

💳 Wallet Service (The Core)
Responsibilities: Acts as the "heart of the banking system," managing user balances and ledger entries.

Key Features:

Ledger Management: Tracks Total Amount, Available Amount, and Reserved/Hold Amount.

Hold & Release Mechanism: Funds are "Held" during a transaction and only "Released" upon successful completion, preventing overdrafts and race conditions.

💸 Transaction Service
Responsibilities: Manages peer-to-peer (P2P) money transfers and transaction history.

Key Features:

SAGA Pattern: Uses the SAGA design pattern for distributed transactions, ensuring consistency without tight coupling.

Orchestration: Coordinates with the Wallet Service to trigger the Hold/Release logic.

🔔 Notification Service
Responsibilities: Delivers alerts and updates to users.

Key Features:

Event-Driven: Acts as a Kafka consumer, listening for events from the Transaction Service.

Decoupled: Notification failures (e.g., email server down) do not block or roll back financial transactions.

🎁 Reward Service
Responsibilities: Calculates and issues cashback or rewards for specific transaction types.

Key Features:

Idempotency: Ensures rewards are not issued twice for the same transaction, preventing double-spending.

🌐 API Gateway
Responsibilities: The single entry point for all client traffic.

Key Features:

Routing: Dynamically routes requests to the appropriate microservice.

Security & Traffic Control: Handles load balancing, rate limiting, and initial request validation.

🚀 Getting Started Prerequisites

Java 17+

Docker & Docker Compose (for Kafka, Zookeeper, PostgreSQL)

Maven

Configuration

This project uses a Centralized Configuration Server to manage properties for all microservices.

Configuration Repository: https://github.com/V-SankaraRao/I-Pay-wallet_System.git

Installation Clone the Repository git clone https://github.com/V-SankaraRao/I-Pay-wallet_System.git cd I-Pay-wallet_System

Start Infrastructure (Database & Kafka) docker-compose up -d

Start the Config Server first.

Start the Discovery Server (Eureka).

Start individual microservices: User, Wallet, Transaction, etc.

🛡️ Security & Patterns Used

ACID Compliance: Ensures strict transactional integrity for all database writes.

SAGA Pattern: Choreography-based distributed transaction management.

Rate Limiter: Restricts the number of requests per user to prevent abuse.

🤝 Contributing

Contributions are welcome! Please fork the repository and create a pull request for any feature enhancements or bug fixes.
