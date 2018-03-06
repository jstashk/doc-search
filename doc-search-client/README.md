DocSearch service. Client part.
=================================

DocSearch service is responsible for 
- Storing and retrieving document content by ist's key
- Searching for documents by it's content

## Installation and running
1. Clone the repository.
2. Open terminal, go to root of cloned repository -> doc-search-client.
3. Build application: ./gradlew build
4. Run application: java -jar build/libs/doc-search-client-0.1.0.jar
5. Our service will be running on localhost at port 8080.
6. Using your browser check localhost:8080/info to see running version.
    And localhost:8080/health for status.


## REST endpoints

### GET documents by keys
- uri : /documents
- parameters: keys - request parameter, comma separated keys of requested documents.
- HTTP method : GET
- response : json object.

### PUT documents by keys
- uri : /documents
- body: Array of json object which corresponds to following schema:
        { 
           "key" : "string value",
           "content" : "string value"
        }
- HTTP method : PUT
- response : json object.

### GET (find) documents by query string
- uri : /documents/search
- parameters: query - request parameter, string containing search input.
- HTTP method : GET
- response : json object.


