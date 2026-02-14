# Financial Ledger Service

## Brief description of your solution

This project is a RESTful API for maintaining a financial ledger (accounting). It allows creating accounts, registering transactions (operations), and viewing account balances and transaction history. It implements basic principles of accounting: double-entry (debit/credit), balanced transactions, and account-type balance calculation.

## Architecture and design decisions you made

*   **Architecture:** Clean Architecture was used with a clear separation into layers: Controller → Service → Repository → Entity.
*   **ORM:** JPA/Hibernate is used for interacting with the database.
*   **Migration Management:** Liquibase is used to manage the database structure for deterministic database state.
*   **Validation:** Validation of incoming data (`@Valid`, `@Positive`, `@NotBlank`) is implemented both at the DTO level (on the controller) and in the business logic of the services.
*   **Use of Lombok:** Lombok was used to reduce boilerplate code (getters, setters, constructors).

## How to build and run the application

1.  Ensure that Java 21 and Maven are installed.
2.  Run PostgreSQL in Docker:
    ```bash
    docker run --name ledger-postgres -e POSTGRES_DB=ledger_db -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -p 5432:5432 -d postgres
    ```
3.  Clone the repository and navigate to the project folder.
4.  Run the application:
    ```bash
    mvn spring-boot:run
    ```
    The application will be available at `http://localhost:8080`.

## How to run the tests

*   **All tests (unit and integration)** can be run with the following command:
    ```bash
    mvn test
    ```

## API Documentation (Postman)

For convenient API testing, a Postman collection is provided. 

See the file: [`FinancialLedgerAPI.postman_collection.json`](./FinancialLedgerAPI.postman_collection.json).

## What you would improve with more time

*   **Add pagination.** Implement pagination for lists (accounts, transactions) using `Pageable` and `Page`.
*   **Add authentication and authorization.** Implement a user system and role-based access to resources (e.g., through Spring Security).
*   **Improve error handling.** Create a richer error scheme (e.g., `ErrorResponse` DTO) with error codes and detailed messages.
*   **Add integration tests with a real database.** Use `@SpringBootTest` with `Testcontainers` to test interaction with all application layers and a real database.
*   **Implement MapStruct.** Use MapStruct for automatic mapping between entities and DTOs, improving performance and type safety.
*   **Add logging.** Integrate SLF4J with Logback for structured event and error logging.
*   **Add API documentation.** Use Swagger/OpenAPI for automatically generating API documentation.

---
