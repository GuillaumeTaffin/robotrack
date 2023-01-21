![example workflow](https://github.com/techexcellenceio/airline-tracking/actions/workflows/ci.yml/badge.svg)

# AIRLINE - TRACKING

To run the full CI pipeline use the command.

```
./gradlew ciPipeline
```

You can also run the project locally and perform requests against the API. You need docker on your machine
Run the following to start the local database:

```
docker compose up
```

And then start the application:

```
./gradlew bootRun
```

You can then use any client to interact with the API, or open the SWAGGER UI in your
browser http://localhost:8080/swagger-ui.html

The /notifications/move URI corresponds to Server Sent Events, used to publish a simple notification whenever an airplane
moves.

## Contributors

- [Guillaume Taffin](https://www.linkedin.com/in/guillaume-taffin-31343b129/) ([GuillaumeTaffin](https://github.com/GuillaumeTaffin))
