# MFA Microservice

## Overview

This project is a microservice for Multi-Factor Authentication (MFA) built with Java and Spring Boot. The service provides APIs to send MFA codes via email and verify them. Redis is used for storing MFA codes, and Docker is used for containerization.

## Features

- **Send MFA Code:** An API endpoint to generate and send a unique, time-sensitive MFA code to a user's email.
- **Verify MFA Code:** An API endpoint to verify the MFA code provided by the user.
- **Security:** MFA codes are unique, time-sensitive, and securely stored in Redis.
- **The rate-limiting**  feature in the MFA service is implemented using Redis to control the frequency of requests for generating and verifying MFA codes. This feature is crucial for preventing abuse, such as excessive requests that could lead to security vulnerabilities or degrade service performance.

## Prerequisites

- Java 17
- Docker and Docker Compose

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/mfa-microservice.git
cd mfa-microservice
```

### 2. Build and Run the Application

You can use Docker Compose to build and run the application, along with Redis and MailHog (if used for local testing).

```bash
docker-compose up --build
```

This will start:

- **MFA Microservice** on `http://localhost:8080`
- **Redis** on `localhost:6379`

### 3. Testing the Endpoints

You can test the API endpoints using tools like Postman or `curl`.

#### a. Send MFA Code

```bash
curl -X POST http://localhost:8080/api/mfa/send \
-H "Content-Type: application/json" \
-d '{"email": "recipient@example.com"}'
```

This will send an MFA code to the specified email.

#### b. Verify MFA Code

```bash
curl -X POST http://localhost:8080/api/mfa/verify \
-H "Content-Type: application/json" \
-d '{"email": "recipient@example.com", "code": "your-mfa-code"}'
```

This will verify the MFA code provided.

## Configuration

### Docker Compose

The `docker-compose.yml` file sets up the following services:

- **mfa-service:** The main microservice
- **redis:** Redis for storing MFA codes

### Running Tests

Unit tests are included to ensure the reliability of the microservice.