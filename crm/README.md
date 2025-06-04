# LAB3
Web Applications 2 course - Group 20

## Documentation

> The documentation is provided by the Swagger UI.
>
> - [Swagger UI](http://localhost:8080/swagger-ui/index.html#/)
> - [API Documentation](http://localhost:8080/v3/api-docs)


## How to run the docker image
Create the image with the following command
``` bash
./gradlew bootBuildImage
```
Then execute this command on the `docker-compose.yml` file
``` bash
docker compose up --build
```