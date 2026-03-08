# Property Management System: Marketplace Property Onboarding Flow

## 1. FEATURE OVERVIEW
The Marketplace Property Onboarding flow enables a landlord to seamlessly set up and publish a property to the public marketplace. Because marketplace listings can range from whole standalone properties (houses/apartments) to composite properties (lodges or apartments rented by the room), this feature consolidates the entire creation process into a single transaction.

In one request, a landlord can submit the property details, attach global property media, specify child rooms (if applicable), attach room-specific media, and either save the progress as a `DRAFT` or validate and push it live (`PUBLISHED`).

## 2. CURRENT API SUMMARY
**Endpoint:** `POST /api/v1/landlord/properties/setup`  
**Method:** `POST`  
**Content-Type:** `multipart/form-data`  

**Purpose:** 
A composite endpoint designed to accept structured JSON metadata (property details, room details, and media metadata) alongside raw file uploads in the same request. By tying the uploaded files to specific keys defined in the metadata, the system elegantly handles multi-level image attachments (Property vs. Room level) in a single pass.

## 3. REQUEST STRUCTURE
The current implementation utilizes Spring's `MultipartHttpServletRequest` to accept the mixed payload.

### Payload Fields
The payload requires:
1.  **`data` part**: A strictly formatted JSON string mapped to the `PropertySetupRequest` DTO.
2.  **File parts**: Multiple binary file attachments where the `form-data` key corresponds exactly to the `fileKey` referenced in the `data` JSON.

### Structure of `data` JSON:
*   **`property`**: Contains base details (`propertyCode`, `title`, `description`, `price`, `type`, `rentalMode`, `allowVisitRequests`, etc.). Includes IDs for `landlordId` and `locationId`.
*   **`propertyImagesMeta`**: A flat array of metadata defining the global property images. Crucially, each entry holds a `fileKey` (e.g., `"property_cover"`) to associate with the uploaded binary.
*   **`rooms`**: An array of `RoomCreatePart` objects. This is optional if the `rentalMode` is `WHOLE_PROPERTY`, but mandatory if it is `BY_ROOMS`.
    *   **`imagesMeta` (inside `rooms`)**: A nested array of image metadata specific to that room, again linked via a `fileKey` (e.g., `"room_1_img_1"`).

### Uploaded Files Behavior
When the service iterates through `propertyImagesMeta` or a room's `imagesMeta`, it extracts the `fileKey`. It then retrieves the corresponding `MultipartFile` directly from the `MultipartHttpServletRequest`.

## 4. DOMAIN MODEL DOCUMENTATION
The onboarding flow interacts with the following core JPA entities:

*   **`Property` (Aggregate Root):** Stores the base listing data. Controls the `ListingStatus` (`DRAFT`, `PUBLISHED`), the `RentalMode` (`WHOLE_PROPERTY` vs `BY_ROOMS`), and setup readiness metrics (`completedSetup`, `requiresRoomSetup`).
    *   *Relationship:* `@ManyToOne` to `Landlord` and `Location`.
    *   *Relationship:* `@OneToMany` to `PropertyImage` and `Room`.
*   **`Room` (Child Entity):** Exists if the property operates under the `BY_ROOMS` rental mode. Stores room-specific pricing, dimensions, and occupant limits.
    *   *Relationship:* `@ManyToOne` to `Property`.
    *   *Relationship:* `@OneToMany` to `RoomImage`.
*   **`PropertyImage` & `RoomImage` (Media Entities):** Isolate media records from the base entities to prevent database bloat and allow adaptive media (e.g., filtering images by `category` or `coverImage` flag).

## 5. BUSINESS RULES AND VALIDATIONS
The `PropertySetupService` currently enforces the following rules within the `@Transactional` boundary:

*   **Required Fields:** `propertyCode` and `title` are strictly validated before processing.
*   **File Matching:** Each image metadata block must include a `fileKey` that matches an attached `MultipartFile`. If the file is missing or empty, the logic skips the upload and appends a warning directly to the response payload.
*   **Minimum Readiness (Publish Conditions):**
    *   At least **1 property image** must be successfully uploaded.
    *   If `rentalMode = BY_ROOMS`, the payload must define at least **1 room**, and those rooms must collectively have at least **1 room image**.
*   **Publishing Fallback:** If the landlord requests `listingStatus: PUBLISHED` but the payload fails the readiness checks, the service automatically downgrades the status to `DRAFT` and flags `published: false`. It successfully saves the data, preventing total data loss, and returns the descriptive validation warnings to the caller.

## 6. FLOW EXPLANATION
1.  **Request Received:** The `PropertySetupController` catches the `multipart/form-data` request, mapping the JSON to `PropertySetupRequest` and capturing the files via `MultipartHttpServletRequest`.
2.  **Payload Parsed & Foreign Keys Fetched:** The service looks up the applicable `Landlord` and `Location` entities.
3.  **Base Property Creation:** Base metadata is mapped to a new `Property` entity and an initial `.save()` is called to generate the primary key (`id`) required for child references.
4.  **Property Media Upload & Link:** Iterates over `propertyImagesMeta`. Looks up the file via `fileKey`. Invokes `FileStorageService`. Saves the resulting `PropertyImage`.
5.  **Room Creation (If Applicable):** Iterates over `rooms`. Maps the base `Room` details and calls `.save()`.
6.  **Room Media Upload & Link:** For each saved room, iterates over its nested `imagesMeta`. Looks up the file via `fileKey`. Invokes `FileStorageService`. Saves the resulting `RoomImage`.
7.  **Readiness Evaluation:** Checks the aggregation counts against the `rentalMode` rules.
8.  **Final Save:** Upgrades or downgrades the `ListingStatus` based on readiness. Updates the `completedSetup` boolean. Calls the final `.save()` on the `Property`.
9.  **Response Generation:** Generates a lightweight `PropertySetupResponse` tallying the saved counts, the final status, and any warnings. If warnings exist, the controller returns an HTTP `202 ACCEPTED` instead of `200 OK` to signal a partial setup.

## 7. STORAGE DOCUMENTATION
**Current Implementation Status:** Mocked Abstraction.

Currently, the `FileStorageService` accepts the `MultipartFile` references but uses a mock generator to simulate the upload process.
*   **Process:** Extracts the `fileName`, `contentType`, and `category`/`title` from the request.
*   **URL Generation:** Generates a simulated URL string dynamically (e.g., `https://propertymgt.example.com/uploads/{timestamp}_{fileName}`).
*   **Future Ready:** The method signatures (`uploadPropertyImage`, `uploadRoomImage`) are fully isolated. They require no refactoring of the parent `PropertySetupService` to swap the mock with an AWS S3, Google Cloud Storage, or **Supabase S3** client. *(Note: S3 Supabase credentials have been provisioned and are ready for integration inside this class).*

## 8. ERROR HANDLING
*   **Missing Core Data:** Hard failure. A runtime `IllegalArgumentException` is thrown if `propertyCode` or `title` is missing, resulting in a `500` (or `400` once the global exception handler maps it) and a complete transaction rollback.
*   **Missing Foreign Keys:** Throws a `RuntimeException` if the `landlordId` is not found, rolling back the transaction.
*   **Missing Media Files:** Soft failure. If a `fileKey` does not yield a valid file in the multipart request, a warning is logged into the `warnings` array returned to the user, and the specific image record is simply skipped. The transaction continues.
*   **Failed Publish Readiness:** Soft failure. Generates warnings, downgrades the state to `DRAFT`, and completes the transaction normally but yields HTTP `202`.

## 9. SAMPLE API DOCUMENTATION

### Set up a Marketplace Property
Creates a property, its subset of rooms, and handles all media uploads in a single transaction.

**Endpoint:** `/api/v1/landlord/properties/setup`  
**Method:** `POST`  
**Content-Type:** `multipart/form-data`  

**Example Request (cURL):**
```bash
curl --location 'http://localhost:8080/api/v1/landlord/properties/setup' \
--form 'data="{
  \"property\": {
    \"landlordId\": 1,
    \"propertyCode\": \"PROP-001\",
    \"title\": \"Luxury Lodge\",
    \"rentalMode\": \"BY_ROOMS\",
    \"listingStatus\": \"PUBLISHED\"
  },
  \"propertyImagesMeta\": [
    { \"fileKey\": \"cover_img\", \"coverImage\": true, \"category\": \"EXTERIOR\" }
  ],
  \"rooms\": [
    {
      \"roomCode\": \"RM-01\",
      \"roomName\": \"Master Suite\",
      \"price\": 150000,
      \"imagesMeta\": [
        { \"fileKey\": \"rm_img_1\", \"coverImage\": true }
      ]
    }
  ]
}";type=application/json' \
--form 'cover_img=@"/path/to/local/cover.jpg"' \
--form 'rm_img_1=@"/path/to/local/room.jpg"'
```

**Example Response:**
```json
{
    "propertyId": 1,
    "propertyCode": "PROP-001",
    "title": "Luxury Lodge",
    "status": "AVAILABLE",
    "listingStatus": "PUBLISHED",
    "propertyImagesCount": 1,
    "roomsCount": 1,
    "roomImagesCount": 1,
    "completedSetup": true,
    "published": true,
    "warnings": [],
    "message": "Property setup processed successfully."
}
```

## 10. STRENGTHS OF CURRENT IMPLEMENTATION
*   **Single-Pass UX:** Avoids forcing the client to choreograph 5+ consecutive HTTP requests just to draft a single property.
*   **Graceful Degradation:** The decision to downgrade a failed `PUBLISHED` attempt to a `DRAFT` (HTTP 202) instead of rolling back the entire transaction prevents the landlord from losing massive image uploads just because they missed a validation rule.
*   **Abstracted Storage:** The `FileStorageService` acts as a clean facade, separating the complex business logic from the network storage logic.
*   **Extremely Scalable JSON Mapping:** Using detached `fileKeys` to link media metadata to the binary uploads completely decouples the depth of the JSON tree from the flat structure of a multipart request.

## 11. IMPROVEMENT OPPORTUNITIES
While the current implementation is highly functional, the following improvements are recommended:
1.  **Storage Implementation:** Swap the mock logic in `FileStorageService` with an actual S3 client (e.g., the Supabase bucket) to handle real binary networking and cleanup on failures.
2.  **Storage Cleanup Strategy:** If the `@Transactional` method fails late in the process (e.g., during the final property save), any images already pushed to S3 earlier in the method will become orphaned. A rollback event listener or a cleanup routine should be implemented to delete orphaned objects.
3.  **Strict Validation Annotations:** Replace manual `if (title == null)` checks with standard JSR-303 (Hibernate Validator) annotations (`@NotNull`, `@NotBlank`) inside the `PropertySetupRequest` DTOs, paired with `@Valid` in the controller.
4.  **Security Context:** The `landlordId` is currently provided in the payload. Once the Phase 3 JWT Security filter is established, this should be stripped from the JSON and extracted securely from the `@AuthenticationPrincipal`.
5.  **Room Code Uniqueness:** The logic currently doesn't check if `roomCode` exists system-wide before saving, leaving it to the database to throw a `DataIntegrityViolationException`. Explicit duplicate checks would yield cleaner user feedback.
