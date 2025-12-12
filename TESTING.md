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
