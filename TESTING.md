# Backend testing (short)

What is tested
- Unit tests: service-layer unit tests (e.g. `CustomerInteractionServiceTest`) that verify business logic and repository interactions using mocks.
- Integration tests: end-to-end-style tests (e.g. `UploadIntegrationTest`) that POST a CSV/JSON upload to the controller and then verify data is retrievable via the search endpoint. These use Testcontainers to provide an ephemeral PostgreSQL database.

How to run
1. Run all tests locally (requires a working JDK & Maven):

```bash
mvn -U test
```

2. Run unit tests only:

```bash
mvn -Dtest=*ServiceTest test
```

3. Run integration tests only (Docker required for Testcontainers):

```bash
mvn -Dtest=*IntegrationTest test
```

4. If your local JDK/toolchain conflicts with Lombok/annotation processors, run tests inside a Maven+JDK Docker image (example):

```bash
docker run --rm -v "$PWD":/workspace -w /workspace maven:3.9.3-eclipse-temurin-17 mvn -U test
```

What the tests do
- Unit tests: mock repositories and validate service-layer mapping, validation and search logic.
- Integration tests: start the Spring test context, use Testcontainers PostgreSQL, POST a sample CSV/JSON to `/api/uploads`, then call `/api/interactions/search` to confirm the uploaded records were persisted and are returned by the API.

Testing technologies
- JUnit 5 (JUnit Jupiter)
- Mockito for mocking unit tests
- Spring Boot Test + MockMvc for controller/integration wiring
- Testcontainers (PostgreSQL) for reproducible integration environment
- REST-assured (if present) for request assertions in integration tests

Notes
- Integration tests require Docker to be running (Testcontainers will pull images). If your local toolchain has javac/Lombok issues, prefer the Docker-based Maven run or CI where the environment is controlled.
# Backend Testing (Customer Workbench Backend)

Prerequisites
- Java 17 (as specified in `pom.xml`)
- Maven 3.8+
- Docker (only required for integration tests that use Testcontainers)

Run unit tests only

From the backend project root:

```bash
cd /Users/harshapothireddy/eclipse-workspace/customer-workbench-backend
# run only the unit tests (example uses the service test)
mvn -DskipTests=false -Dtest=CustomerInteractionServiceTest test
```

Run integration test (requires Docker)

```bash
cd /Users/harshapothireddy/eclipse-workspace/customer-workbench-backend
# runs the UploadIntegrationTest which uses Testcontainers/Postgres
mvn -DskipTests=false -Dtest=UploadIntegrationTest test
```

Run all tests

```bash
mvn test
```

Troubleshooting
- If you encounter compile-time errors related to Lombok / annotation processing (e.g., ExceptionInInitializerError referencing TypeTag), ensure your local JDK is Java 17 and Lombok version in `pom.xml` is compatible. Consider cleaning your local Maven repository and re-running `mvn -U test`.

Script
- There is a convenience script `run-tests.sh` in the project root to run unit/integration tests.
