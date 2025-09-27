# Conductor – Event Management System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

Conductor is a **multi-tenant event management system** built with **Spring Boot**, designed to support **scalable, secure, and isolated event workflows** for multiple organizations.
It enables **event creation, ticketing, registration workflows, and ticket application handling** with fine-grained **row-level security (RLS)**, ensuring data separation between tenants.

---

## Features

- **Multi-tenant Architecture**: Supports multiple organizations with isolated event workflows.
- **Row-level Security (RLS)**: Ensures per-organization data access and compliance.
- **Ticketing & Registration**: Create, register, and manage event tickets.
- **Application Workflow**: Ticket application flow with secure approvals.
- **Scalability**: Optimized to handle thousands of concurrent registrations with minimal latency.
- **RESTful API**: Documented, versioned API for easy integration.

---

##  Tech Stack

- **Backend**: Java 21, Spring Boot 3
- **Database**: h2 (temporary for development purpose only)
- **Security**: Spring Security, JWT
- **Build Tool**: Maven
---

##  API Documentation

The full REST API documentation is available here:
👉 [Conductor API Docs](https://bump.sh/conductor/doc/conductor-api/)

---
## Setup

```bash
# Clone the repository
git clone https://github.com/ayush27316/Conductor.git
cd Conductor

#install dependencies
mvn clean install

# Build and Run the project
cd core
./run.sh


