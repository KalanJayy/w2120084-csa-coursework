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
