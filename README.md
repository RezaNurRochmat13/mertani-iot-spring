# Device and Sensor Management System

A Spring Boot application for managing IoT devices and their associated sensors with full CRUD operations.

## Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- PostgreSQL 13 or higher
- Postman or any API testing tool

## Setup

1. **Database Setup**
   - Create a PostgreSQL database named `iot_management`
   - Update the database credentials in `src/main/resources/application.properties` if needed

2. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

3. **Access the Application**
   - The application will be available at `http://localhost:8080/api`
   - API Documentation (Swagger UI): `http://localhost:8080/api/swagger-ui.html`

## API Endpoints

### Device Endpoints

- `GET /api/devices` - Get all devices
- `GET /api/devices/{id}` - Get a device by ID
- `POST /api/devices` - Create a new device
- `PUT /api/devices/{id}` - Update a device
- `DELETE /api/devices/{id}` - Delete a device

### Sensor Endpoints

- `GET /api/sensors` - Get all sensors
- `GET /api/sensors/{id}` - Get a sensor by ID
- `GET /api/sensors/device/{deviceId}` - Get all sensors for a device
- `POST /api/sensors` - Create a new sensor
- `PUT /api/sensors/{id}` - Update a sensor
- `DELETE /api/sensors/{id}` - Delete a sensor

## Data Models

### Device
```json
{
  "id": 1,
  "name": "Raspberry Pi 4",
  "serialNumber": "RP4-12345",
  "description": "Main controller for living room",
  "location": "Living Room",
  "active": true,
  "createdAt": "2023-10-31T15:30:00",
  "updatedAt": "2023-10-31T15:30:00"
}
```

### Sensor
```json
{
  "id": 1,
  "name": "Temperature Sensor",
  "sensorId": "TEMP-001",
  "type": "DHT22",
  "unit": "°C",
  "minValue": -20.0,
  "maxValue": 60.0,
  "active": true,
  "deviceId": 1,
  "createdAt": "2023-10-31T15:30:00",
  "updatedAt": "2023-10-31T15:30:00"
}
```

## Error Handling

The API provides meaningful error messages with appropriate HTTP status codes:

- `400 Bad Request` - Invalid input data
- `404 Not Found` - Resource not found
- `409 Conflict` - Duplicate serial number or sensor ID
- `500 Internal Server Error` - Server-side error

## Testing

You can use the included Postman collection or the Swagger UI for testing the API endpoints.

## Built With

- Spring Boot 3.1.0
- Spring Data JPA
- PostgreSQL
- Maven
- Lombok
- MapStruct
- SpringDoc OpenAPI

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
