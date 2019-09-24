# iglu-json-schema-validator
This is a sample project that demonstrates how to store and validate schemas on top of [iglu](https://github.com/snowplow/iglu)

## Details
The project exposes the following end-points:

```
POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`
POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`
```

The schemas are stored and retrieved from [iglu](https://github.com/snowplow/iglu) and validated against json-schema using [json-schema-validator](https://github.com/java-json-tools/json-schema-validator)

## Development Setup
The project is a play 2.X app. It also needs docker for running iglu locally. All the service APIs and the necessary iglu APIs can be found in the [postman collection](https://www.getpostman.com/collections/baaa49f221434942e4d6)

* Setup iglu server on docker by follwing the instructions [here](https://github.com/snowplow/iglu/wiki/Iglu-server-setup) and [here](https://github.com/snowplow/snowplow-docker/tree/master/iglu-server/example/docker-compose)
* Make sure you insert the super API key into the running postgres
* Generate the keys from the keygen API, the API can also be invoked from the [postman collection](https://www.getpostman.com/collections/baaa49f221434942e4d6)
* Take the `write` token and update `iglu.token` in `application.conf`
* Run the app as a play app

## Tests
Right now, the tests need a locally running iglu server. The instructions above can be used here. The tests can then be run as `sbt test`

## TODO
* Making schemas versioned. Iglu supports this, but this application doesn't. Instead, it overwrites the same version on every `POST /schema/SCHEMAID` API call
* Fetch the keys once at application startup and fetch it again if it expires or on authentication errors
* Using testcontainers instead of making it mandatory to run docker containers separately
* Making the tests transactional, either by starting a new iglu server (enables parallel execution) for each test or by cleaning up the uploaded schemas after each test
