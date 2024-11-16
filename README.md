# Invoice API

This project is a Spring Boot based REST API for managing invoices. 
The application supports operations like creating invoices, making payments, and processing overdue invoices.

---

## Table of Contents

- [Run Locally](#run-locally)
    - [Clone the Repository](#1-clone-the-repository)
    - [Run the Application](#2-run-the-application)
        - [Option 1: Run Directly using Maven](#option-1-run-directly-using-maven)
        - [Option 2: Build and Run the JAR](#option-2-build-and-run-the-jar)
        - [Option 3: Run with Docker](#option-3-run-with-docker)
        - [Option 4: Run with Docker Compose](#option-4-run-with-docker-compose)
- [API Documentation](#api-documentation)
- [Postman Collection](#postman-collection)
- [H2 Database Console](#h2-database-console)

---

## Run Locally

Clone the repository and use one of the options to run it. Access the application at http://localhost:8080.

### 1. Clone the Repository
   ```bash
   git clone https://github.com/rai-sandeep/invoice-api.git
   cd invoice-api
   ```
### 2. Run the Application

### Option 1: Run Directly using Maven

To run the application directly using Maven (requires Java 21 or higher, Maven 3.8 or higher):

   ```bash
   mvn spring-boot:run
   ```

### Option 2: Build and Run the JAR

To build and run the application as a standalone JAR (requires Java 21 or higher, Maven 3.8 or higher):

1. Build the JAR:
   ```bash
   mvn clean package
   ```
2. Run the JAR:
   ```bash
   java -jar target/api.jar
   ```

### Option 3: Run with Docker

To run the application using Docker:

1. Build the Docker image:
   ```bash
   docker build -t invoice-api .
   ```
2. Run the Docker container:
   ```bash
   docker run -p 8080:8080 invoice-api
   ```

### Option 4: Run with Docker Compose

To deploy the application using Docker Compose:
   ```bash
   docker-compose up
   ```

## API Documentation

For details about the API endpoints and their usage:

- Swagger UI: http://localhost:8080/swagger-ui.html.
- Alternatively, refer to the api-docs endpoint at http://localhost:8080/v3/api-docs.

## Postman Collection

A Postman collection is included in the repository under the `tests` folder.  
Import the collection in Postman and execute requests to test the API. 
Ensure that the application is running locally.

## H2 Database Console

This application uses an H2 in-memory database for storage, and the H2 Console is enabled for accessing the database.

To access the H2 console:

1. Ensure that the application is running locally.
2. Open the H2 console at http://localhost:8080/h2-console.
3. Database URL and credentials can be found in `src/main/resources/application.properties`.

Note: This is an in-memory database, which means that the data will be cleared every time the application restarts.