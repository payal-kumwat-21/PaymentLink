# Payment Link API (Razorpay-style)

A Spring Boot REST API that replicates Razorpay's Payment Links API — supporting both standard and UPI payment links, with the same request/response structure, business validation rules, and error format as Razorpay's actual API.

## Overview

This project models the core lifecycle of a Razorpay Payment Link: creation (standard or UPI), retrieval, listing/filtering, updating, and cancellation. It enforces the same business rules Razorpay applies in production — e.g., UPI links must be in INR, have a minimum amount of ₹100, and cannot accept partial payments — and returns structured error responses in Razorpay's `BAD_REQUEST_ERROR` format.

Built during a Java backend internship to practice designing production-style REST APIs with proper validation, layered architecture, and relational data modeling.

## Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Data Layer:** Spring Data JPA / Hibernate
- **Database:** MySQL
- **Build Tool:** Maven
- **Libraries:** Lombok (boilerplate reduction)
- **Testing:** Postman (manual API testing)

## Features

- Create **standard** or **UPI** payment links (routed dynamically based on an `upi_link` flag in the request)
- Razorpay-style business validation:
  - UPI links restricted to INR currency
  - UPI links require a minimum amount of ₹100
  - UPI links cannot accept partial payments
  - Reference ID capped at 40 characters
  - Updates only allowed while a link is in `created` or `partially_paid` status
- Fetch a single payment link by ID (with `plink_` prefix validation)
- List and filter payment links by `reference_id` and `payment_id`
- Update payment link details (reference ID, expiry, reminders, notes)
- Cancel a payment link, with guards against cancelling links that are already paid, expired, or cancelled
- Structured, Razorpay-style JSON error responses (`BAD_REQUEST_ERROR` with `code` and `description`)
- Normalized relational schema — Customer, Notify (notification settings), Reminders, and Notes are stored in separate tables linked to the parent payment link

## API Endpoints

Base path: `/api/v1/payment-links`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST   | `/` | Create a new payment link (standard or UPI, based on `upi_link` in the request body) |
| GET    | `/` | List all UPI payment links, optionally filtered by `reference_id` and `payment_id` query params |
| GET    | `/{id}` | Fetch a payment link by ID |
| PATCH  | `/{id}` | Update a payment link (auto-detects standard vs. UPI and applies the matching rules) |
| PATCH  | `/stdUpdate/{id}` | Update a standard (non-UPI) payment link directly |
| POST   | `/{id}/cancel` | Cancel an active payment link |

## Sample Request/Response

**Create UPI Payment Link — `POST /api/v1/payment-links/`**

Request body:
```json
{
  "amount": 500,
  "currency": "INR",
  "description": "Order #1234 payment",
  "reference_id": "TS1989",
  "upi_link": true,
  "customer": {
    "name": "John Doe",
    "email": "john@example.com",
    "contact": "+919999999999"
  },
  "notify": {
    "sms": true,
    "email": true
  },
 "created_at": 1751362800,
  "updated_at": 1751362800
}
```

Response (`201 Created`):
```json
{
  "id": "plink_3f9a2b1c4d5e6a",
  "amount": 500,
  "amount_paid": 0,
  "currency": "INR",
  "accept_partial": false,
  "description": "Order #1234 payment",
  "reference_id": "TS1989",
  "short_url": "https://rzp.io/i/aB3xY9k",
  "status": "created",
  "upi_link": true,
  "customer": {
    "name": "John Doe",
    "email": "john@example.com",
    "contact": "+919999999999"
  },
  "created_at": 1751362800,
  "updated_at": 1751362800
}
```

**Error Response — invalid UPI amount**
```json
{
  "error": {
    "code": "BAD_REQUEST_ERROR",
    "description": "amount: amount should be minimum 100 for INR."
  }
}
```

## Data Model

The `payment_links` table is the core entity, with related data normalized into separate tables:

| Table | Purpose |
|-------|---------|
| `payment_links` | Core link data — amount, currency, status, timestamps, UPI flag, etc. |
| `customer` | Customer details (name, email, contact) linked by `payment_link_id` |
| `notify` | Notification preferences (SMS/email) linked by `payment_link_id` |
| `reminders` | Reminder status linked by `payment_link_id` |
| `payment_link_notes` | Key/value notes attached to a link |

IDs are generated in Razorpay's format (e.g., `plink_...`, `cust_...`, `notif_...`, `rem_...`) to mirror their API responses.

## How to Run Locally

1. Clone the repository
   ```bash
   git clone https://github.com/payal-kumwat-21/PaymentLink.git
   cd PaymentLink
   ```

2. Configure your MySQL connection in `src/main/resources/application.properties`
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/payment_link_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```

3. Build and run
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. The API will be available at `http://localhost:8080/api/v1/payment-links`

5. Test endpoints using Postman — import the sample requests above or build your own collection against the routes listed.

## What I Learned / Built

- Designed a multi-endpoint REST API replicating a real-world payment gateway's contract and business rules
- Modeled a normalized relational schema across five related tables (PaymentLink, Customer, Notify, Reminders, Notes)
- Implemented conditional business logic (standard vs. UPI flows) with guard clauses matching Razorpay's actual constraints
- Built structured, consistent error handling matching an external API's error format
- Used Spring Data JPA repositories, `@Transactional` boundaries, and DTO-based request/response separation
- Tested all endpoints and edge cases (invalid IDs, invalid amounts, invalid status transitions) using Postman

## Author

**Payal Kumawat**
[LinkedIn](https://www.linkedin.com/in/payal-kumawat-aa0b062b2) · [GitHub](https://github.com/payal-kumwat-21)
