# Application External Context Service

This is a Spring Boot service for storing application-specific variables and steps in MongoDB with TTL (time-to-live) support. Designed for use as a dependency in other projects or for testing purposes.

## Technology Stack

- Java 17 - LTS version with modern language features
- Spring Boot 3.5.6 - Latest Spring Boot framework
- Maven 3 - Dependency management and build automation
- JUnit 5 - Modern testing framework
- JaCoCo 0.8.12 - Code coverage analysis with 80% minimum threshold
- MongoDB 8.2 - Document database with TTL index support
- Docker - Alpine-based containerization with optimized JRE image

## Architecture

- Service layer handles `vars` and `steps` CRUD operations.
- MongoDB stores documents with an `expireAt` field.
- TTL Index automatically removes documents after their expiration time.
- Controller layer exposes REST endpoints for external access.

#### Quick installation in Docker

1. Build the service in terminal
    ```shell
    mvn package
    ```
2. Create registry-network(for windows should use WSL terminal):
    ```shell
    docker network create registry-network
    ```
3. Copy the application jar file to root project directory
   ```shell
   mkdir -p target && cp application-external-context/target/application-external-context-1.0.0-SNAPSHOT.jar target/
   ```
4. Run Docker-compose
    ```shell
    docker compose up -d
    ```
5. Access container logs:
   ```shell
    docker logs -f app-context-mongo
    docker logs -f application-context-service
   ```
6. In case if you need to rebuild the service you also need to remove service docker image:
   ```shell
   docker rmi application-context-service
   ```

#### Installation in Minikube

##### Prerequisites

1. Installed [Java OpenJDK 17](https://openjdk.org/install/), [Maven](https://maven.apache.org/)
   and [Minikube](https://minikube.sigs.k8s.io/docs/).
2. Maven is configured to use Nexus repository with all needed dependencies.
3. On the running minikube cluster installed:
   1. [MongoDB](#mongodb-installation)

##### Configuring

* Configuration can be changed in application-external-context config-map that is
  described [here](deploy-templates/templates/configmap.yaml) or in
  runtime in minikube dashboard with pod restarting.
* Any jvm attributes can be added to application-external-context deployment in JAVA_OPTS env
  variable [here](minikube-local/values.yaml) or in runtime in minikube dashboard.

##### MongoDB installation
```shell
kubectl apply -f ./minikube-local/mongodb.yml
```

##### Application-external-context installing

1. Build the service
    ```shell
    mvn package
    ```
2. Copy the application jar file to root project directory
   ```shell
   mkdir -p target && cp application-external-context/target/application-external-context-1.0.0-SNAPSHOT.jar target/
   ```
3. Build Docker image:
   ```shell
   minikube image build -t application-external-context:latest .
   ```
   You can check if image is built and exists in Minikube by:
   ```shell
   minikube image ls --format=table
   ```
4. Install application-external-context using Helm:
   ```shell
   helm install application-external-context-local deploy-templates -f ./minikube-local/values.yaml
   ```
   Or if application-external-context-local already installed:
   ```shell
   helm upgrade application-external-context-local deploy-templates -f ./minikube-local/values.yaml
   ```
   In order to uninstall application-external-context-local use:
   ```shell
   helm uninstall application-external-context-local
   ```
   If application-external-context-local is installed and application-external-context image is rebuilt just
   delete application-external-context pod.
5. Deployment health and service logs can be found in Minikube Dashboard:
   ```shell
   minikube dashboard
   ```
   Managing cluster can also be performed here.
6. After service deploying run next:
   ```shell
   kubectl port-forward svc/application-external-context 8086:8080
   ```
   Go to [Swagger](http://localhost:8086/swagger-ui/index.html)

## License

Apache License 2.0
