# Skincare Service Application

This is a Spring Boot application for managing a skincare service, using H2 in-memory database.

## Prerequisites

- Java 17 or higher
- Maven

## Running the Application

### Option 1: Using the run script

1. Double-click on the `run.bat` file (Windows)

### Option 2: Using Maven directly

1. Open a terminal or command prompt
2. Navigate to the project directory
3. Run the following command:
   ```
   mvn clean spring-boot:run
   ```

## Accessing the Application

- Main application: http://localhost:8080/
- H2 Console: http://localhost:8080/h2-console

## H2 Database Connection Details

- JDBC URL: `jdbc:h2:mem:skincare_db`
- Username: `sa`
- Password: (leave empty)

## Default Users

| Username  | Password   | Role      |
|-----------|------------|-----------|
| admin     | password   | ADMIN     |
| therapist | password   | THERAPIST |
| customer  | password   | CUSTOMER  |

## API Documentation

The REST API endpoints are available at:
- http://localhost:8080/api/

## Features

- User authentication and authorization
- Customer management
- Appointment scheduling
- Service management
- Therapist assignment 