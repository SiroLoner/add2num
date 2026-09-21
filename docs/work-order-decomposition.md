# WO-201 - Work Order Domain Decomposition

## 1. Context and Scope

### Business intent

Operational managers need to create work orders for field service technicians.

### In scope

- Create a work order through a REST API.
- Validate the input at the API boundary.
- Initialize every new work order with status `DRAFT`.
- Document the UI, data and API contracts.

### Out of scope

- Dispatching algorithms.
- Synchronization with third-party vendors.
- Technician assignment.
- Work order update, deletion or search workflows.

## 2. Target Stack and Constraints

- Java 17.
- Maven.
- Spring Boot when the runtime API module is implemented.
- PostgreSQL 16 for persistent storage.
- Do not add external dependencies without architectural review.
- This Lab 1.2 change is documentation/configuration-only and introduces no runtime code.

## 3. UI Validation Matrix

| Field | Type | Required | Rules | Error behavior |
| --- | --- | --- | --- | --- |
| `title` | String | Yes | Minimum 5, maximum 255 characters | Field-level validation error |
| `description` | String | No | Maximum 2,000 characters | Field-level validation error when limit is exceeded |
| `priority` | Enum | Yes | `LOW`, `MED`, `HIGH`, `CRITICAL`; default `MED` | Reject unsupported values |
| `customer_id` | UUID | Yes | Must reference an active customer | Reject malformed or unknown identifier |

Client-side validation improves user feedback but must be repeated at the API boundary.

## 4. Data Contract

### PostgreSQL schema

```sql
CREATE TABLE work_orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    description TEXT,
    priority VARCHAR(20) NOT NULL DEFAULT 'MED',
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    customer_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_work_orders_priority
        CHECK (priority IN ('LOW', 'MED', 'HIGH', 'CRITICAL')),
    CONSTRAINT chk_work_orders_status
        CHECK (status IN ('DRAFT'))
);
```

### Data and PII rules

- `customer_id` is internal PII and must not be written in unsanitized application logs.
- Authorization must be checked before creating a work order for a customer.
- Database credentials and environment values must never appear in this document or in AI prompts.
- The final implementation should define a foreign key to the customer table when that table exists in the owning service.

## 5. REST API Contract

### Endpoint

```text
POST /api/v1/work-orders
```

### Authorization

```text
Authorization: Bearer <token>
Required scope: workorders:write
Content-Type: application/json
```

### Request body

```json
{
  "title": "HVAC Repair Unit 4",
  "description": "System reporting error code E-42",
  "priority": "HIGH",
  "customer_id": "a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11"
}
```

### Success response: `201 Created`

```json
{
  "id": "c9bf9e57-1685-4c89-bafb-ff5af830be8a",
  "status": "DRAFT",
  "created_at": "2026-08-30T10:00:00Z"
}
```

### Error responses

- `400 Bad Request`: malformed JSON or invalid request shape.
- `401 Unauthorized`: missing or invalid bearer token.
- `403 Forbidden`: token lacks `workorders:write` scope.
- `422 Unprocessable Entity`: field-level validation failure, such as a missing title.

Example validation response:

```json
{
  "code": "VALIDATION_ERROR",
  "fields": [
    {
      "field": "title",
      "message": "title field required"
    }
  ]
}
```

## 6. Acceptance Criteria

- Given valid work order input and a token with `workorders:write`, when the client sends the request, then the service persists a work order with status `DRAFT` and returns `201 Created`.
- Given a missing `title`, when the client sends the request, then the service returns `422 Unprocessable Entity` with a field-level `title` error.
- Given an invalid `priority`, when the client sends the request, then the service returns `422 Unprocessable Entity` and does not persist a record.
- Given a token without `workorders:write`, when the client sends the request, then the service returns `403 Forbidden`.
- Given this Lab 1.2 scope, when the Pull Request is reviewed, then no application code under `src/` is changed.

## 7. Human Verification Record

- [x] UI, Data and API boundaries are explicitly separated.
- [x] Scope and non-goals are documented.
- [x] Acceptance criteria are testable.
- [x] PII and security requirements are documented.
- [x] No secrets or customer data are included.
- [x] This specification was reviewed after AI-assisted drafting.
