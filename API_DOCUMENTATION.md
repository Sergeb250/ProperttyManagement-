# Property Management System - Complete API Documentation

This document serves as the master API reference for the Property Management System. It outlines the core architecture, workflows, and provides the cURL/JSON payloads required to interact with every endpoint currently implemented in the system.

---

## 🏗 System Architecture Overview

The system is a multi-actor platform (Airbnb model + Long-term Rental model) with Role-Based context boundaries:
1.  **Public Marketplace (`/api/v1/marketplace`)**: Open indexing of `PUBLISHED` properties.
2.  **Landlord Dashboard (`/api/v1/landlord`)**: Secured property setup, media management, applicant reviews, and manual lease assignment.
3.  **Tenant Portal (`/api/v1/tenant`)**: Initiating rent requests, reviewing agreements, and executing payments.
4.  **Financials & Webhooks (`/api/v1/webhooks`, `/api/v1/receipts`)**: Handling immutable financial state transitions and async callbacks from payment providers (e.g., MTN MoMo, Stripe).

All media uploads are backed by an integrated **Supabase S3 Client**.

---

## 1. Master Data Endpoints

### 1.1 Create Location Hierarchy
Creates a location (e.g., Province, District, Sector). Used to enforce standard geography.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/locations`
*   **Body (JSON):**
```json
{
  "name": "Kigali Province",
  "code": "KIG",
  "type": "PROVINCE",
  "active": true
}
```

---

## 2. Landlord Management APIs

### 2.1 Property Setup & Onboarding (Composite)
Creates the Property, its constituent Rooms, and physically uploads all S3 media in a single atomic transaction.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/landlord/properties/setup`
*   **Content-Type:** `multipart/form-data`
*   **Payload (`data` JSON part):**
```json
{
  "property": {
    "landlordId": 1,
    "locationId": 1,
    "propertyCode": "PROP-099",
    "title": "Kigali Heights Apart-Hotel",
    "price": 300000,
    "currency": "RWF",
    "type": "LODGE",
    "rentalMode": "BY_ROOMS",
    "listingStatus": "PUBLISHED"
  },
  "propertyImagesMeta": [
    { "fileKey": "cover_image", "category": "EXTERIOR", "coverImage": true }
  ],
  "propertyVideosMeta": [
    { "fileKey": "tour_video_1", "title": "Main Property Walkthrough", "platform": "S3_UPLOAD" },
    { "title": "Drone Footage", "platform": "YOUTUBE", "externalEmbedId": "dQw4w9WgXcQ" }
  ],
  "rooms": [
    {
      "roomCode": "RM-01",
      "roomName": "Deluxe Suite",
      "price": 80000,
      "imagesMeta": [
        { "fileKey": "room_12_img", "coverImage": true }
      ]
    }
  ]
}
```
*   **File Parts:** Attach files with keys matching `cover_image` and `room_12_img`.

### 2.2 Upload Single Property Image
Standard endpoint to upload secondary images after initial setup.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/landlord/properties/{id}/images`
*   **Content-Type:** `multipart/form-data`
*   **Form Data:**
    *   `file`: (Binary Image File)
    *   `category`: `INTERIOR`
    *   `isCover`: `false`

### 2.3 Review Rent Request
Allows a landlord to either approve or reject a tenant's application. Approving automatically kicks off the Lease Agreement and Payment pipeline.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/landlord/rent-requests/{id}/review?approve=true&notes=Welcome!`
*   **Response:** Updates state to `APPROVED` and generates `Agreement` context.

### 2.4 Create Manual Tenancy (Bypass Marketplace)
For when a landlord sources a tenant offline and needs to bypass the application queue directly into an active lease.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/landlord/manual-tenancy`
*   **Content-Type:** `application/x-www-form-urlencoded`
*   **Form Data:**
    *   `landlordId`: 1
    *   `tenantId`: 1
    *   `propertyId`: 1
    *   `roomId`: 1 (Optional)
    *   `moveInDate`: `2026-05-01`

---

## 3. Public Marketplace APIs

### 3.1 Fetch Available Properties
Fetches paginated, sanitized DTOs of properties marked as `PUBLISHED` and `AVAILABLE`.
*   **Method:** `GET`
*   **Endpoint:** `/api/v1/marketplace/properties`

### 3.2 Fetch Property Details
Retrieve the deep nested representation (Amenities, Public Rooms, Media) of a specific listing.
*   **Method:** `GET`
*   **Endpoint:** `/api/v1/marketplace/properties/{id}`

---

## 4. Tenant Portal APIs

### 4.1 Submit Rent Request (Application)
Allows a tenant to declare intent to rent a property or specific room.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/tenant/rent-request`
*   **Body (JSON):**
```json
{
  "tenantId": 1,
  "propertyId": 1,
  "roomId": 1,
  "requestedMoveInDate": "2026-06-01",
  "proposedRentAmount": 80000,
  "message": "I would like to rent the Deluxe Suite.",
  "requestSource": "WEB"
}
```

### 4.2 Initiate Payment
After the landlord approves and an agreement is generated, the Tenant initiates payment.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/tenant/pay`
*   **Body (JSON):**
```json
{
  "tenantId": 1,
  "paymentRequestId": 1,
  "method": "MOMO",
  "provider": "MTN",
  "returnUrl": "https://myapp.com/success"
}
```

---

## 5. Webhooks & Financials

### 5.1 Payment Provider Callback (Webhook)
Internal/System endpoint invoked by MTN/Stripe when the funds settle. Triggers the creation of the immutable receipt.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/webhooks/payments/callback`
*   **Content-Type:** `application/x-www-form-urlencoded`
*   **Form Data:**
    *   `externalTransactionId`: `TXN-559982`
    *   `status`: `SUCCESS`

### 5.2 Generate Immutable Receipt
Generates the cryptographically/soft-locked receipt indicating financial finality. No `PUT`/`PATCH` endpoints exist for Receipts by design.
*   **Method:** `POST`
*   **Endpoint:** `/api/v1/receipts`
*   **Body (JSON):**
```json
{
  "receiptNumber": "RCPT-10045",
  "amount": 80000,
  "issuedBy": "System",
  "voided": false
}
```

---

## 🚀 Execution Workflows

### Flow A: Tenant Marketplace Lifecycle
1.  Tenant browses `/api/v1/marketplace/properties`
2.  Tenant issues `/api/v1/tenant/rent-request`
3.  Landlord sees inbox, hits `/api/v1/landlord/rent-requests/{id}/review?approve=true`
4.  Tenant is notified, hits `/api/v1/tenant/pay`
5.  MoMo server posts `/api/v1/webhooks/payments/callback`
6.  System sets Room to `OCCUPIED`, generates `Tenancy` and immutable `Receipt`.

### Flow B: Landlord Fast-Track Onboarding
1.  Landlord hits `/api/v1/landlord/properties/setup` with Multipart JSON + Images. System uploads to **Supabase S3**.
2.  Landlord has external walk-in tenant, hits `/api/v1/landlord/manual-tenancy`.
3.  System immediately generates active lease (`Tenancy`), marks room `OCCUPIED`.
