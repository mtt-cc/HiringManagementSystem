# API Endpoints LAB1 
Web Applications 2 course

> [Swagger API Documentation](http://localhost:8081/swagger-ui.html)

## API Endpoints

### Get all documents:
This GET request retrieves a list of documents from the API.

**URL:** `localhost:8080/documents/`

### Get document details:
This GET request retrieves metadata of a specific document using its ID.

**URL:** `localhost:8080/documents/3`

### Get document content:
This GET request retrieves the document using its ID.

**URL:** `localhost:8080/documents/4/data`

### Add document:
This POST request creates a new document using JSON data in the request body.

**URL:** `localhost:8080/documents/`

### Update document:
This PUT request updates an existing document identified by its ID in the URL. Data is provided in the JSON request body.

**URL:** `localhost:8080/documents/5`

### Delete document:
This DELETE request removes a document identified by its ID in the URL.

**URL:** `localhost:8080/documents/6`

## Tests of the endpoints
Tests of all the possible scenarios of the endpoints can be found into the "Test.postman_collection.json".
