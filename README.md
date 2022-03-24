# metadata-api-test

## Bugs 
1. POST 
   1. No validation on the request's payload is existed. In the other word, invalid or empty values are all accepted.
   2. MAJOR: It doesn't create a new metadata at all. The response says it's successful but it doesn't return newly created metadata and the subject created not existed when calling GET request with this subject value.
2. GET 
   1. 