# Epoxy Proxy Application

#### Description

To address the problem, I have implemented two combined and appended endpoints. 
The initial step involves the conversion of a base64 URI into a Java List, 
verifying its validity. Subsequently, I employ a loop to iterate over the endpoints, utilizing 
__CompletableFuture__ for concurrent API calls. Upon receiving a response, 
I examine the content type and proceed with __JSON/XML__ parsing accordingly. 
In the event of a failure in any API response, I assess the error type. 
If it is __fail_any__, I raise an exception to signify a failed response. 
Conversely, if the error type is __replace__, the respective API is marked as 
failed in the response. Additionally, comprehensive JUnit test cases have been 
developed to validate the implementation.

#### Note:
- __I have attached Video PPT as well for Demo. Epoxy Task Video PPT__
- __Collection of postman is also attached in application__

#### Endpoints
- http://localhost:8080/v1/fetch/{apis}/combined?errors=Fail_Any&timeout=300
- http://localhost:8080/v1/fetch/{apis}/appended?errors=Replace

#### Technology Used
- Java 17
- Springboot 3.1
- Maven
- Junit Mockito
- Jackson Api

#### Build and Run
- mvn clean install
- mvn spring-boot:run

#### Assumption
- Any Error type __replace/fail_any__ can be used with any endpoints.
- Timeout will be sent only in combined api.
- Response of Epoxy server will be in json.
- Error type is required field.
- Timeout is optional if not passed 1000 will be default timeout.

#### Resources
- https://jsonformatter.org/json-to-base64 used to convert Json Array into base64 Uri.
- https://jsonplaceholder.typicode.com/users used for json response.
- http://restapi.adequateshop.com/api/Traveler?page=1 used for xml response.