# Three-Module Spring Boot (REST + GraphQL + Webhook) with Central Swagger (UAT-only)

## Modules
- `documents-service` (8081): REST bill **upload** (multipart)
- `invoicing-service` (8082): REST **invoice PDF** generation + **email**
- `integrations-service` (8083): **GraphQL**, **Webhook**, **centralized Swagger UI**

## Build
```bash
mvn -q -DskipTests clean package
```

## Run (UAT profile => Swagger UI enabled)
```bash
# Terminal 1
mvn -q -pl documents-service spring-boot:run -Dspring-boot.run.profiles=uat
# Terminal 2
mvn -q -pl invoicing-service spring-boot:run -Dspring-boot.run.profiles=uat
# Terminal 3
mvn -q -pl integrations-service spring-boot:run -Dspring-boot.run.profiles=uat
```

- Swagger UI: `http://localhost:8083/swagger-ui.html` (aggregates all services; APIs require Bearer JWT)
- Get JWT: `POST http://localhost:8083/auth/login?user=prashant`

## Production (Swagger disabled)
Run each service with `--spring.profiles.active=prod`.
