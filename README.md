# buzzstats

Monitors web pages for changes.



## How to start the buzzstats application

1. Run `mvn clean install` to build your application
1. Start application with
   `java -jar target/buzzstats-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

## Health Check

To see your applications health enter url `http://localhost:8081/healthcheck`

## Db migrations

Build the application with `mvn package`.

- Check the migration status with
  `java -jar target/buzzstats-1.0-SNAPSHOT.jar db status config.yml`
- Apply migrations with
  `java -jar target/buzzstats-1.0-SNAPSHOT.jar db migrate config.yml`
