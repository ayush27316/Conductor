## API Endpoints

This document describes the REST endpoints exposed by the controllers under `com.conductor.core.controller`, including request/response schemas and common error cases. All responses now use Spring `ResponseEntity` with standard HTTP status codes.

### Auth

- POST `/auth/login`
  - Request (application/json):
    ```json
    {
      "username": "string",
      "password": "string"
    }
    ```
  - Responses:
    - 200 OK
      ```json
      { "jwt": "string" }
      ```
    - 401 Unauthorized: "Username or password invalid"
    - 500 Internal Server Error: "An internal server error occurred"

- POST `/auth/signup`
  - Request:
    ```json
    {
      "username": "string",
      "password": "string",
      "email": "string",
      "first_name": "string",
      "last_name": "string"
    }
    ```
  - Responses:
    - 201 Created (no body)
    - 409 Conflict: "Username already taken."
    - 500 Internal Server Error: "An internal server error occurred"

### Events

- POST `/api/events/register`
  - Request:
    ```json
    {
      "id": "string|null",
      "organizationId": "string",
      "name": "string",
      "format": "ONLINE|OFFLINE|HYBRID",
      "location": "string",
      "begin_time": "YYYY-MM-DDThh:mm:ss",
      "end_time": "YYYY-MM-DDThh:mm:ss",
      "total_tickets_to_be_sold": 0,
      "options": ["..."],
      "access_strategy": "PUBLIC|PRIVATE|INVITE",
      "accessible_from": "YYYY-MM-DDThh:mm:ss",
      "accessible_to": "YYYY-MM-DDThh:mm:ss",
      "description": "string"
    }
    ```
  - Responses:
    - 201 Created (no body)
    - 500 Internal Server Error: string message

- GET `/api/events`
  - Responses:
    - 200 OK: `Array<EventDTO>`
    - 500 Internal Server Error (no body)

`EventDTO` schema:
```json
{
  "id": "string",
  "organizationId": "string",
  "name": "string",
  "format": "string",
  "location": "string",
  "begin_time": "string",
  "end_time": "string",
  "total_tickets_to_be_sold": 0,
  "options": ["string"],
  "access_strategy": "string",
  "accessible_from": "string",
  "accessible_to": "string",
  "description": "string"
}
```

### Tickets

- POST `/api/tickets/book`
  - Request:
    ```json
    { "event_name": "string" }
    ```
  - Responses:
    - 201 Created: `TicketDTO`
    - 404 Not Found: string message

- POST `/api/tickets/buy/{eventExternalId}?tags=tag1,tag2`
  - Responses:
    - 201 Created: `TicketDTO`
    - 400 Bad Request: string message
    - 404 Not Found: string message
    - 500 Internal Server Error: "Purchase failed"

- GET `/api/tickets/{ticketExternalId}/qr`
  - Responses:
    - 200 OK: image/png
    - 404 Not Found (no body)

- POST `/api/tickets/{ticketExternalId}/check-in`
  - Responses:
    - 200 OK: `TicketDTO`
    - 400 Bad Request: string message
    - 500 Internal Server Error: "Check-in failed"

`TicketDTO` schema:
```json
{
  "code": "string",
  "owner_username": "string",
  "event_id": "string"
}
```

### Organization Registration

- POST `/api/organizations/register`
  - Request:
    ```json
    {
      "name": "string",
      "description": "string",
      "email": "string",
      "tags": ["string"],
      "website_url": "string",
      "locations": "string"
    }
    ```
  - Responses:
    - 201 Created: `OrganizationRegistrationResult`
    - 500 Internal Server Error: string message

- GET `/api/organizations/{applicationExternalId}/form`
  - Responses:
    - 200 OK: `FormSchemaResponse`
    - 400 Bad Request (no body)

- POST `/api/organizations/{applicationExternalId}/form`
  - Request:
    ```json
    { "schema_json": "string" }
    ```
  - Responses:
    - 204 No Content
    - 400 Bad Request (no body)

- POST `/api/organizations/{applicationExternalId}/form/submit`
  - Request:
    ```json
    { "result_json": "string" }
    ```
  - Responses:
    - 204 No Content
    - 400 Bad Request (no body)

- POST `/api/organizations/approve`
  - Request:
    ```json
    {
      "application_id": "string",
      "approve": true
    }
    ```
  - Responses:
    - 200 OK (no body)
    - 400 Bad Request: string message

- GET `/api/organizations/pending`
  - Responses:
    - 200 OK: `Array<Application>`
    - 500 Internal Server Error (no body)

`FormSchemaResponse` schema:
```json
{ "schema_json": "string" }
```

### Event Applications

- POST `/api/event-applications/submit`
  - Request:
    ```json
    {
      "event_id": "string",
      "message": "string",
      "additional_notes": "string"
    }
    ```
  - Responses:
    - 201 Created: `EventApplicationResponse`
    - 400 Bad Request: string message
    - 500 Internal Server Error: string message

- GET `/api/event-applications/{applicationExternalId}/form`
  - Responses:
    - 200 OK: `FormSchemaResponse`
    - 400 Bad Request: string message

- POST `/api/event-applications/{applicationExternalId}/form`
  - Request:
    ```json
    { "schema_json": "string" }
    ```
  - Responses:
    - 204 No Content
    - 400 Bad Request (no body)

- POST `/api/event-applications/{applicationExternalId}/form/submit`
  - Request:
    ```json
    { "result_json": "string" }
    ```
  - Responses:
    - 204 No Content
    - 400 Bad Request (no body)

- POST `/api/event-applications/process`
  - Request:
    ```json
    {
      "application_id": "string",
      "action": "approve|reject",
      "reason": "string"
    }
    ```
  - Responses:
    - 200 OK: string (result message)
    - 400 Bad Request: string message
    - 401 Unauthorized: string message
    - 500 Internal Server Error: string message

- GET `/api/event-applications/pending`
  - Responses:
    - 200 OK: `Array<Application>`
    - 500 Internal Server Error (no body)

- GET `/api/event-applications/event/{eventId}`
  - Responses:
    - 200 OK: `Array<Application>`
    - 400 Bad Request: string message
    - 500 Internal Server Error: string message

- GET `/api/event-applications/my-applications`
  - Responses:
    - 200 OK: `Array<Application>`
    - 500 Internal Server Error (no body)

- POST `/api/event-applications/{applicationId}/comments`
  - Request:
    ```json
    { "comment": "string" }
    ```
  - Responses:
    - 204 No Content
    - 400 Bad Request: string message
    - 500 Internal Server Error: string message

- POST `/api/event-applications/{applicationId}/cancel`
  - Request (optional body):
    ```json
    { "reason": "string" }
    ```
  - Responses:
    - 204 No Content
    - 400 Bad Request (no body)
    - 401 Unauthorized (no body)
    - 500 Internal Server Error (no body)

- GET `/api/event-applications/{applicationId}`
  - Responses:
    - 501 Not Implemented: string message
    - 500 Internal Server Error: string message

### Common Errors

- 400 Bad Request: Validation errors, missing fields, invalid state transitions
- 401 Unauthorized: Authentication/authorization failures
- 404 Not Found: Missing resources (tickets, events)
- 409 Conflict: Username already taken (signup)
- 500 Internal Server Error: Unhandled server errors
