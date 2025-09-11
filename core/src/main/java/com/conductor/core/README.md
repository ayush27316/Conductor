## API Documentation

### Authentication

- POST `/auth/login`
    - **Body**: `LoginRequest` { username, password }
    - **Responses**:
        - 200: `{ "jwt": string }`
        - 401: `Error` message when credentials invalid

- POST `/auth/signup`
    - **Body**: `SignupRequest` { username, password, email, first_name, last_name }
    - **Responses**:
        - 201: Created
        - 409: `Error` when username already taken

### Organization Applications
Base path: `/api/organizations`

- POST `/register`
    - **Auth**: ROLE_USER
    - **Body**: `OrganizationRegistrationRequest`
    - **Responses**:
        - 200: `{ "registration_id": string }`

- PUT `/applications/{application-id}/approve`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Responses**: 200 OK

- PUT `/applications/{application-id}/reject`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Query**: `reason` (<=200 chars)
    - **Responses**: 200 OK

- DELETE `/applications/{application-id}`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Responses**: 200 OK

- POST `/applications/{application-id}/comments`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Query**: `comment` (<=1000 chars)
    - **Responses**: 201 Created

- GET `/applications/pending`
    - **Auth**: ROLE_ADMIN
    - **Responses**: 200: `List<Application>`

### Event Applications
Base path: `/api/events`

- POST `/{event-id}/apply`
    - **Auth**: ROLE_USER
    - **Path**: `event-id` (UUID, 36 chars)
    - **Body**: `FormResponse` { form_response }
    - **Responses**: 200 OK

- PUT `/applications/{application-id}/approve`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Responses**: 200 OK

- PUT `/applications/{application-id}/reject`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Query**: `reason` (<=200 chars)
    - **Responses**: 200 OK

- DELETE `/applications/{application-id}`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Responses**: 200 OK

- POST `/applications/{application-id}/comments`
    - **Path**: `application-id` (UUID, 36 chars)
    - **Query**: `comment` (<=1000 chars)
    - **Responses**: 201 Created

- GET `/{event-id}/applications`
    - **Path**: `event-id`
    - **Responses**: 200: `List<ApplicationDTO>`

- GET `/{event-id}/form`
    - **Auth**: ROLE_USER
    - **Path**: `event-id`
    - **Responses**: 200: `{ "form": string }`

### Event Registration & Modification
Base path: `/api/events`

- POST `/register`
    - **Body**: `EventRegistrationRequest`
    - **Responses**: 201 Created

### Tickets
Base path: `/api/tickets`

- POST `/book`
    - **Auth**: ROLE_USER
    - **Body**: `BookTicketRequest` (name)
    - **Responses**:
        - 201: `TicketDTO`
        - 404: Event not found

- POST `/buy/{eventExternalId}`
    - **Auth**: ROLE_USER
    - **Path**: `eventExternalId`
    - **Query**: `tags` (CSV, optional)
    - **Responses**:
        - 201: `TicketDTO`
        - 400/404/500 on error

- GET `/{ticketExternalId}/qr`
    - **Auth**: ROLE_USER
    - **Path**: `ticketExternalId`
    - **Responses**:
        - 200: image/png
        - 404: Not found

- POST `/{ticketExternalId}/check-in`
    - **Auth**: ROLE_OPERATOR
    - **Path**: `ticketExternalId`
    - **Responses**:
        - 200: `TicketDTO`
        - 400/500 on error

### DTO References

- `LoginRequest`: username, password
- `SignupRequest`: username, password, email, first_name, last_name
- `OrganizationRegistrationRequest`: name, description, email, tags, website_url, locations
- `FormResponse`: form_response
- `ApplicationDTO`: organization_id, event_id, submitted_by_user_id, submitted_at, application_status, processed_by_user_id, processed_at, comments[], application_form, application_form_response
- `EventRegistrationRequest`: organization_id, name, format, location, begin_time, end_time, total_tickets_to_be_sold, options[], is_free, ticket_price, currency, access_strategy, accessible_from, accessible_to, description
- `TicketDTO`: code, owner_username, event_id


