# Customer Workbench Backend

Backend application for the Customer Insights Workbench, built with Spring Boot and providing REST APIs for data upload and retrieval.

## Architecture

### Technology Stack
- **Framework**: Spring Boot 3.2.1
- **Language**: Java 17
- **Database**: PostgreSQL 16
- **Build Tool**: Maven
- **ORM**: Hibernate (JPA)
- **Migration**: Flyway

### Project Structure
```
src/
├── main/
│   ├── java/com/customersaas/workbench/
│   │   ├── controller/          # REST API endpoints
│   │   ├── service/             # Business logic
│   │   ├── repository/          # Data access layer
│   │   ├── entity/              # JPA entities
│   │   ├── dto/                 # Data Transfer Objects
│   │   └── CustomerWorkbenchApplication.java
│   └── resources/
│       ├── application.yml      # Configuration
│       └── db/migration/        # Flyway migrations
└── test/
    └── java/com/customersaas/workbench/
        ├── service/             # Unit tests
        └── CustomerInteractionIntegrationTest.java  # Integration tests
```

### Key Components

#### 1. **REST Controllers**
- `UploadController` - Handles file uploads (CSV/JSON)
- `InteractionController` - Handles search and filtering queries

#### 2. **Services**
- `FileUploadService` - Parses CSV/JSON files and imports data
- `CustomerInteractionService` - Search and retrieval operations

#### 3. **Data Models**
- `CustomerInteraction` - Core entity representing customer interactions
- `UploadJob` - Tracks file upload status

#### 4. **Database Schema**
Two main tables:
- `customer_interactions` - Stores parsed customer interaction records
- `upload_jobs` - Tracks upload history and status

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.8+
- Docker and Docker Compose (for running PostgreSQL locally)

### Database Setup

#### Start PostgreSQL with Docker
The application uses PostgreSQL as its database. Start a local PostgreSQL instance using Docker Compose:

```bash
docker-compose up -d
```

This will:
- Start PostgreSQL 16 on port 5432
- Create a database named `customer_workbench`
- Set up a persistent volume for data storage
- Default credentials: `postgres` / `your_password_here`

To stop the database:
```bash
docker-compose down
```

To stop and remove all data:
```bash
docker-compose down -v
```

#### Database Configuration
The application connects to PostgreSQL using the following default settings (configured in `application.properties`):
- **URL**: `jdbc:postgresql://localhost:5432/customer_workbench`
- **Username**: `postgres`
- **Password**: `your_password_here`
- **Schema**: Automatically created via Flyway migrations on startup

To change these settings, update `src/main/resources/application.properties` or set environment variables:
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

### Build
```bash
mvn clean install
```

### Run
```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

## API Endpoints

### 1. Upload Customer Interactions
**POST** `/api/uploads`

Upload a CSV or JSON file containing customer interactions.

**Request**
```
Content-Type: multipart/form-data
Parameter: file (the CSV or JSON file)
```

**Response**
```json
{
  "success": true,
  "message": "File uploaded successfully",
  "uploadJobId": 1,
  "processedRecords": 150,
  "failedRecords": 0
}
```

### 2. Search Customer Interactions
**GET** `/api/interactions/search`

Search and filter stored customer interactions with pagination.

**Query Parameters**
- `customerId` (optional) - Filter by customer ID
- `interactionType` (optional) - Filter by interaction type (email, chat, ticket, feedback)
- `startDate` (optional) - Filter by start date (ISO format: YYYY-MM-DDTHH:MM:SS)
- `endDate` (optional) - Filter by end date (ISO format: YYYY-MM-DDTHH:MM:SS)
- `page` (default: 0) - Page number for pagination
- `size` (default: 10) - Number of records per page

**Example**
```
GET /api/interactions/search?customerId=CUST-001&interactionType=email&page=0&size=10
```

**Response**
```json
{
  "interactions": [
    {
      "id": 1,
      "productId": "PROD-001",
      "customerId": "CUST-001",
      "customerRating": 5,
      "feedback": "Great service!",
      "timestamp": "2025-12-10T10:30:00",
      "responsesFromCustomerSupport": "Thank you!",
      "interactionType": "email",
      "createdAt": "2025-12-10T10:30:00",
      "updatedAt": "2025-12-10T10:30:00"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "currentPage": 0,
  "pageSize": 10
}
```

## File Format Requirements

### CSV Format
```csv
product_id,customer_id,customer_rating,feedback,timestamp,responses_from_customer_support
PROD-001,CUST-001,5,"Great service!",2025-12-10T10:30:00,"Thank you!"
PROD-001,CUST-002,4,"Good support",2025-12-10T11:00:00,"Appreciated!"
```

### JSON Format
```json
[
  {
    "product_id": "PROD-001",
    "customer_id": "CUST-001",
    "customer_rating": 5,
    "feedback": "Great service!",
    "timestamp": "2025-12-10T10:30:00",
    "responses_from_customer_support": "Thank you!"
  }
]
```

## Testing

### Run All Tests
```bash
mvn test
```

### Run Unit Tests Only
```bash
mvn test -Dtest=*ServiceTest
```

### Run Integration Tests
```bash
mvn test -Dtest=*IntegrationTest
```

### Test Coverage
The project includes:
- Unit tests for `CustomerInteractionService`
- Integration tests covering full upload and retrieval workflows
- Tests use Testcontainers to spin up PostgreSQL instances automatically (no manual database setup required)

## Configuration

### Environment Variables
- `SPRING_DATASOURCE_URL` - Database connection URL (default: `jdbc:postgresql://localhost:5432/customer_workbench`)
- `SPRING_DATASOURCE_USERNAME` - Database username (default: `postgres`)
- `SPRING_DATASOURCE_PASSWORD` - Database password (default: `your_password_here`)
- `SERVER_PORT` - Server port (default: 8080)

### CORS Configuration
By default, CORS is enabled for:
- `http://localhost:3000` (React default)
- `http://localhost:5173` (Vite default)

Modify `application.yml` to add additional origins.

## Design Decisions

### 1. **Database Schema**
- Used indexed columns on frequently searched fields (customer_id, product_id, timestamp)
- Implemented soft-delete via created_at/updated_at timestamps
- Used single table with clear relationships

### 2. **File Processing**
- Async processing of large files (saved to UploadJob)
- Support for both CSV and JSON formats
- Graceful error handling with detailed error messages

### 3. **API Design**
- RESTful endpoints following standard conventions
- Pagination support for large datasets
- Flexible filtering parameters

### 4. **Error Handling**
- Detailed error messages in upload response
- Partial success handling (failed records don't prevent successful ones from being saved)
- Comprehensive logging for debugging

## Deployment

### Production Setup
For production deployment:
1. Set up a managed PostgreSQL instance (AWS RDS, Azure Database, etc.)
2. Update environment variables with production database credentials:
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
3. Flyway migrations will run automatically on startup

### Docker Deployment

#### Application Container
Create a `Dockerfile` for the application:
```dockerfile
FROM openjdk:17-slim
COPY target/customer-workbench-backend-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
mvn clean package
docker build -t customer-workbench-backend .
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/customer_workbench \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=your_password_here \
  customer-workbench-backend
```

#### Full Stack with Docker Compose
The included `docker-compose.yml` can be extended to run both the application and database:
```yaml
services:
  postgres:
    # ... existing postgres configuration ...

  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/customer_workbench
      SPRING_DATASOURCE_USERNAME: postgres
      SPRING_DATASOURCE_PASSWORD: your_password_here
    depends_on:
      postgres:
        condition: service_healthy
```

## Future Enhancements
- Batch processing for large files
- Async upload notifications via WebSocket
- Export to CSV/Excel
- Advanced analytics and reporting
- User authentication and authorization
- Rate limiting and throttling
