# Smart Campus Sensor & Room Management API

## Overview

This project is a RESTful web service developed for the **5COSC022W Client-Server Architectures coursework**. It simulates a **Smart Campus** environment where rooms, sensors installed in those rooms, and historical sensor readings can be managed through HTTP endpoints.

The API is implemented using **JAX-RS (Jersey)** and deployed on **Apache Tomcat** as a **Maven Web Application**. In line with the coursework requirements, the system uses **in-memory data structures only** (`HashMap` and `ArrayList`) and does not use any database technology.

---

## API Design

The API follows RESTful design principles 

### Base URL

```text
http://localhost:8080/w2120084_coursework/api/v1
```

### Main Resources

#### Discovery Endpoint
```
GET /api/v1
```
Returns API metadata including version, contact information, and top-level resource links.

#### Room Resource
```
GET    /api/v1/rooms
POST   /api/v1/rooms
GET    /api/v1/rooms/{roomId}
DELETE /api/v1/rooms/{roomId}
```

#### Sensor Resource
```
GET    /api/v1/sensors
GET    /api/v1/sensors?type=CO2
POST   /api/v1/sensors
GET    /api/v1/sensors/{sensorId}
```

#### Sensor Reading Sub-Resource
```
GET  /api/v1/sensors/{sensorId}/readings
POST /api/v1/sensors/{sensorId}/readings
```

---

### Resource Models

#### Room

A room contains:

- id
- name
- capacity
- sensorIds

#### Sensor

A sensor contains:

- id
- type
- status
- currentValue
- roomId

#### SensorReading

A sensor reading contains:

- id
- timestamp
- value

---

## Key Features

- Discovery endpoint for API navigation
- Room creation, retrieval, and deletion
- Sensor creation with room validation
- Sensor filtering using query parameters
- Deep nesting through a dedicated sensor reading sub-resource
- Automatic update of a sensor's currentValue when a new reading is added
- Custom exception handling with structured JSON responses
- Request and response logging using JAX-RS filters

---

## Error Handling

The API includes custom exception mappers so that clients receive structured JSON errors instead of raw stack traces.

| Status Code | Scenario |
|---|---|
| 409 Conflict | Attempt to delete a room that still has assigned sensors |
| 422 Unprocessable Entity | Attempt to create a sensor with a non-existing roomId |
| 403 Forbidden | Attempt to post a reading to a sensor in MAINTENANCE mode |
| 500 Internal Server Error | Unexpected server-side error |

---

## Logging

A custom logging filter implements both:

- ContainerRequestFilter
- ContainerResponseFilter

This allows the API to log:

- incoming request method and URI
- outgoing response status code

---

## Technology Stack

- Java 8
- Maven
- Apache Tomcat 9
- JAX-RS / Jersey 2.32
- Jackson JSON support

---

## Project Structure

```
src/main/java/com/mycompany/w2120084_coursework/
├── MyApplication.java
├── model
│   ├── Room.java
│   ├── Sensor.java
│   ├── SensorReading.java
│   └── ErrorMessage.java
├── store
│   └── DataStore.java
├── resource
│   ├── Discovery.java
│   ├── SensorRoom.java
│   ├── SensorResource.java
│   └── SensorReadingResource.java
├── exception
│   ├── RoomNotEmptyException.java
│   ├── LinkedResourceNotFoundException.java
│   └── SensorUnavailableException.java
├── mapper
│   ├── RoomNotEmptyExceptionMapper.java
│   ├── LinkedResourceNotFoundExceptionMapper.java
│   ├── SensorUnavailableExceptionMapper.java
│   └── GlobalExceptionMapper.java
└── filter
    └── LoggingFilter.java
```

---

## How to Build and Run the Project

### Prerequisites

Make sure the following are installed:

- Java 8
- Apache Tomcat 9
- Apache NetBeans
- Maven

### Step 1: Clone the repository
```bash
git clone https://github.com/KalanJayy/w2120084-csa-coursework.git
cd w2120084-csa-coursework
```

### Step 2: Open the project in NetBeans
1. Open Apache NetBeans
2. Select **File → Open Project**
3. Choose the project folder

### Step 3: Configure Tomcat
- In NetBeans, make sure Apache Tomcat 9 is added under **Services → Servers**
- Ensure the project is configured to run on Tomcat

### Step 4: Build the project

In NetBeans:
1. Right-click the project
2. Select **Clean and Build**

Or using Maven:
```bash
mvn clean install
```

### Step 5: Run the project

In NetBeans:
1. Right-click the project
2. Select **Run**

Tomcat will deploy the application automatically.

### Step 6: Access the API
```
http://localhost:8080/w2120084_coursework/api/v1
```

---

## Sample curl Commands

The following commands demonstrate successful interactions with different parts of the API.
They should be executed in order because later requests depend on resources created earlier.

#### 1. Access the Discovery Endpoint
```bash
curl -X GET http://localhost:8080/w2120084_coursework/api/v1
```

#### 2. Create a Room
```bash
curl -X POST http://localhost:8080/w2120084_coursework/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d '{
    "id": "LAB-101",
    "name": "Computer Lab",
    "capacity": 40
  }'
```

#### 3. Retrieve All Rooms
```bash
curl -X GET http://localhost:8080/w2120084_coursework/api/v1/rooms
```

#### 4. Create a Sensor Linked to the Existing Room
```bash
curl -X POST http://localhost:8080/w2120084_coursework/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d '{
    "id": "SEN-001",
    "type": "CO2",
    "status": "ACTIVE",
    "currentValue": 0.0,
    "roomId": "LAB-101"
  }'
```

#### 5. Retrieve Sensors Filtered by Type
```bash
curl -X GET "http://localhost:8080/w2120084_coursework/api/v1/sensors?type=CO2"
```

#### 6. Add a Reading to the Sensor
```bash
curl -X POST http://localhost:8080/w2120084_coursework/api/v1/sensors/SEN-001/readings \
  -H "Content-Type: application/json" \
  -d '{
    "value": 412.6
  }'
```

#### 7. Retrieve Sensor Reading History
```bash
curl -X GET http://localhost:8080/w2120084_coursework/api/v1/sensors/SEN-001/readings
```

---

## Notes

- The API uses in-memory storage only, so all data is reset whenever the server restarts.


## Report Question and Answers

## Part 1 – Service Architecture and Setup

### 1. Project & Application Configuration

#### Question
Explain the default lifecycle of a JAX-RS Resource class. Is a new instance instantiated for every incoming request, or does the runtime treat it as a singleton? Elaborate on how this architectural decision impacts the way you manage and synchronize your in-memory data structures.

#### Answer

In JAX-RS, a new instance of a resource class is created for each incoming request. It is not treated as a singleton by default. This follows the REST principle of statelessness, where each request is handled independently without relying on previously stored state.

Because of this behavior, storing application data inside resource class instance variables is not suitable, as that data would only exist for a single request and then be lost. Therefore, shared application data such as rooms, sensors, and readings are stored in a centralized in-memory storage class (`DataStore`) using structures like `HashMap` and `ArrayList`.

However, even though each request gets a separate resource instance, the shared data structures are accessed concurrently by multiple requests. This introduces the risk of race conditions. For example, one request might try to delete a room while another is adding a sensor to it.

To avoid such issues, validation and controlled updates are used to ensure consistency and prevent data corruption.

---

### 2. The “Discovery” Endpoint

#### Question
Why is Hypermedia (HATEOAS) considered a hallmark of advanced RESTful design? How does it benefit client developers?

#### Answer

HATEOAS is considered an advanced feature of RESTful design because it allows the server to guide clients through the API by including navigation links directly in responses.

Instead of requiring the client to know all endpoints in advance, the server provides links to available actions. In contrast, traditional APIs rely on static documentation, which forces clients to hardcode endpoint URLs.

Hypermedia improves usability by making APIs self-descriptive. For example, when retrieving a room, the response can include links to view its sensors or perform related actions.

This reduces dependency on hardcoded URLs, improves flexibility, and makes applications more robust when APIs evolve.

---

## Part 2 – Room Management

### 1. Room Resource Implementation

#### Question
What are the implications of returning only IDs versus full room objects?

#### Answer

Returning only room IDs reduces the response size, resulting in lower bandwidth usage and faster responses. However, the client must make additional requests to retrieve full details, increasing complexity.

Returning full room objects provides all necessary data in one response, making it easier for the client to process and display information. However, it increases payload size.

Therefore, returning IDs is more efficient, while returning full objects is more convenient for the client.

---

### 2. Room Deletion and Safety Logic

#### Question
Is the DELETE operation idempotent?

#### Answer

Yes, the DELETE operation is idempotent.

An operation is idempotent if repeating it results in the same final server state. When a room is deleted, it is removed from the system. If the same DELETE request is sent again, the server returns a 404 Not Found since the room no longer exists.

Although the response changes, the server state remains unchanged after the first deletion, which satisfies idempotency.

---

## Part 3 – Sensor Operations and Linking

### 1. Sensor Resource and Integrity

#### Question
What happens if a client sends a request in a format different from JSON?

#### Answer

The `@Consumes(MediaType.APPLICATION_JSON)` annotation restricts the method to accept only JSON input.

If a client sends data in a different format such as `text/plain` or `application/xml`, JAX-RS detects a media type mismatch and does not invoke the resource method. Instead, it returns a **415 Unsupported Media Type** response.

This ensures that only valid and expected data formats are processed.

---

### 2. Filtered Retrieval & Search

#### Question
Why is `@QueryParam` better than using path parameters for filtering?

#### Answer

Using `@QueryParam` is more appropriate because filtering does not define a new resource; it simply narrows down a collection.

For example:

/api/v1/sensors?type=CO2


Using path-based filtering such as:

/api/v1/sensors/type/CO2


makes it look like a separate resource.

Query parameters are more flexible and scalable, especially when multiple filters are needed, such as:

/api/v1/sensors?type=CO2&status=ACTIVE


---

## Part 4 – Deep Nesting with Sub-Resources

### 1. Sub-Resource Locator Pattern

#### Question
What are the architectural benefits of the Sub-Resource Locator pattern?

#### Answer

The Sub-Resource Locator pattern helps structure the API according to resource relationships. In this case, sensor readings belong to a specific sensor, so using:


/sensors/{sensorId}/readings


makes the design more intuitive.

It also provides separation of concerns. The main `SensorResource` handles sensor-related logic, while `SensorReadingResource` handles reading-related operations.

Without this approach, all logic would be placed in one large class, making it difficult to manage, test, and maintain. Using separate classes improves readability, maintainability, and scalability.

---

## Part 5 – Advanced Error Handling & Logging

### 1. Dependency Validation (422 Unprocessable Entity)

#### Question
Why is HTTP 422 more appropriate than 404?

#### Answer

HTTP 422 is used when a request is syntactically correct but contains invalid data.

For example, when creating a sensor with a non-existent `roomId`, the endpoint exists and the request is valid, but the data is incorrect.

HTTP 404 is used when the resource in the URL does not exist.

Therefore, 422 is more accurate because it indicates that the server understands the request but cannot process it due to invalid data.

---

### 2. Global Safety Net (500 Errors)

#### Question
What are the risks of exposing stack traces?

#### Answer

Exposing stack traces is a security risk because it reveals internal system details such as:

- Class and package names
- File paths
- Libraries and frameworks
- Application structure

Attackers can use this information to identify vulnerabilities and exploit the system.

Instead, APIs should return generic error messages and hide internal details using global exception handlers.

---

### 3. Logging Filters

#### Question
Why use JAX-RS filters instead of manual logging?

#### Answer

JAX-RS filters allow centralized handling of cross-cutting concerns like logging.

This improves separation of concerns, as logging is handled outside business logic. It also ensures consistency, since all requests and responses are logged in a uniform format.

Filters improve maintainability because changes to logging behavior can be made in one place. They also support scalability, as new endpoints automatically inherit logging behavior without additional code.

---
