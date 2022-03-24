# metadata-api-test
## How to run
1. Local
   1. Install Java ( 11+)
   2. Install Maven
   3. Clone this repo
   4. Run `mvn test`

2. From GH Action

Please see actions tab. the manual workflow trigger is enabled there.
## Bugs 
By running this tests, all failed tests indicate bugs described below.

1. POST (/metadata/query)
   1. No validation on the request's payload is existed. In the other word, invalid or empty values around "subjects" and "properties" are all accepted.
   2. Incorrect status code mappings
      1. When successful, it should be 201 (CREATED)
      2. When unsuccessful with invalid request, it should be 400 (or other appropriate 4xx)
   3. MAJOR: It doesn't create a new metadata at all. The response says it's successful however it doesn't return newly created metadata and the subject created not existed when calling GET request with this subject value.
      1. I see the reason because there is no logic in the API to add requested data to DB or similar.
2. GET (/metatdata/{value})
   1. The response isn't formed in JSON when NOT FOUND
   2. Incorrect status code mapping
      1. When NOT FOUND, still returns 200 which means successful.
3. GET (/metadata/{value1}/properties/{value2})
    1. The response isn't formed in JSON when NOT FOUND
    2. Incorrect status code mapping
       1. When NOT FOUND, still returns 200 which means successful.
    3. When a specific property has a single value, the response forms as text not JSON.