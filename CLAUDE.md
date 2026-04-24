# CLAUDE.md — Project Context for Claude Code Sessions

This file is automatically loaded by Claude Code at the start of every session.
It provides all the context needed to work on this project without re-explaining constraints.

---

## Project Overview

**Course:** Arquitectura Centrada en el Negocio (ARCN_M)  
**Author:** Jesús Pinzón (`jesus.pinzon-v@mail.escuelaing.edu.com`)  
**Repository:** `ARCN-laboratory-4-microservices`

Event-driven architecture with two independent Spring Boot microservices communicating
asynchronously through a RabbitMQ message broker. Orchestrated with Docker Compose and
deployed to Killercoda for live testing.

```
Producer Service (8080) ──AMQP──► RabbitMQ ──AMQP──► Consumer Service
```

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.5 |
| Build tool | Maven 3.9+ |
| Messaging | RabbitMQ 3.12 (DirectExchange) |
| Containerisation | Docker + Docker Compose |
| Testing | JUnit 5 + Mockito + JaCoCo |
| Boilerplate reduction | Lombok |
| Validation | Jakarta Validation (spring-boot-starter-validation) |
| Deployment target | Killercoda Ubuntu Playground |

---

## Repository Structure

```
ARCN-laboratory-4-microservices/
├── .claude/settings.json          # Claude Code permissions
├── .devcontainer/devcontainer.json
├── assets/images/                 # Evidence screenshots for README
├── producer-service/              # Microservice 1 — REST API → RabbitMQ publisher
│   ├── src/main/java/com/eci/arcn/producer/
│   │   ├── config/RabbitMQConfig.java
│   │   ├── controller/MessageController.java
│   │   ├── dto/{MessageRequestDto, MessageResponseDto}.java
│   │   └── service/{MessagePublisherService, impl/MessagePublisherServiceImpl}.java
│   ├── pom.xml
│   └── Dockerfile
├── consumer-service/              # Microservice 2 — RabbitMQ listener
│   ├── src/main/java/com/eci/arcn/consumer/
│   │   ├── config/RabbitMQConfig.java
│   │   ├── listener/MessageListener.java
│   │   └── service/{MessageProcessorService, impl/MessageProcessorServiceImpl}.java
│   ├── pom.xml
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Common Commands

```bash
# Build and test a service (run from its directory)
cd producer-service && mvn clean verify
cd consumer-service && mvn clean verify

# Build a Docker image (multi-stage — no pre-build needed)
docker build -t <DOCKER_HUB_USERNAME>/producer-service:latest ./producer-service
docker build -t <DOCKER_HUB_USERNAME>/consumer-service:latest ./consumer-service

# Start the full stack
docker compose up -d

# Check container status
docker compose ps

# Tail consumer logs
docker compose logs -f consumer

# Send a test message (from Killercoda or local)
curl -s -X POST "http://localhost:8080/api/messages/send" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello from Claude Code"}'

# Tear down
docker compose down
```

---

## Language and Formatting Rules

- **All code, comments, variable names, log messages, and test strings must be in English.**
- Follow standard Java naming conventions at all times:
  - `PascalCase` — classes, interfaces, enums, annotations
  - `camelCase` — methods, variables, parameters
  - `UPPER_SNAKE_CASE` — constants
  - `kebab-case` — properties keys in `application.properties`
  - `lowercase` with dots — package names

---

## Javadoc Convention

Every class and interface must have this exact Javadoc header:

```java
/**
 * Brief description of the class or interface.
 *
 * @author Author Name
 * @version 1.0
 * @since YYYY-MM-DD
 */
```

- Public methods that are part of an interface contract get a concise `/** ... */` Javadoc.
- Private/package-private helpers do **not** need Javadoc unless the logic is non-obvious.
- Inline comments are added **only** when the WHY is non-obvious (hidden constraint, subtle
  invariant, workaround). Never explain WHAT the code does — well-named identifiers already do that.

---

## Design Principles (apply to all new code)

| Principle | How it manifests here |
|---|---|
| **SRP** | Each class has one reason to change: config ≠ controller ≠ service ≠ DTO |
| **OCP** | New message types → new DTOs, not modifications to existing ones |
| **DIP** | Controllers and listeners depend on service *interfaces*, never on `*Impl` classes |
| **DRY** | Shared topology constants live in `application.properties`, not hardcoded |
| **KISS** | No abstraction layers beyond what the current requirement justifies |
| **YAGNI** | Do not add features, error handlers, or fallbacks for hypothetical scenarios |

Prefer editing existing files over creating new ones. Do not introduce helper classes,
utility wrappers, or abstractions unless the current code explicitly requires them.

---

## Lombok Usage

Use Lombok on all DTOs, request/response objects, and JPA entities:

```java
@Data           // getter + setter + equals + hashCode + toString
@Builder        // builder pattern
@NoArgsConstructor
@AllArgsConstructor
```

Use `@RequiredArgsConstructor` on Spring `@Service`, `@Component`, and `@RestController`
classes to inject `final` dependencies via constructor (preferred over `@Autowired`).

Use `@Slf4j` on any class that needs logging.

Do **not** use Lombok on configuration classes (`@Configuration`) that declare `@Bean` methods.

---

## Jakarta Validation

Apply Jakarta constraints **only at system entry boundaries** (controller DTOs):

```java
@NotBlank   // required string fields
@Size       // length limits
@NotNull    // non-null object fields
```

Do not add validation annotations on domain objects, service parameters, or internal DTOs.
Validation inside `@Service` classes should use guard clauses, not annotations.

---

## pom.xml Structure

All new `pom.xml` files must follow the structure and comment style of `pom-example.xml`:
- XML comments as section separators (`<!-- Section Name -->`)
- Properties block before dependencies
- JaCoCo plugin always included alongside the Spring Boot Maven Plugin
- Lombok annotation processor path declared inside the compiler plugin configuration
- Lombok excluded from the Spring Boot fat JAR

---

## Testing Rules

### Framework
JUnit 5 + Mockito + JaCoCo. **Minimum coverage target: 80%.**

### File placement
- Unit tests: `src/test/java/...` — same package as the class under test.
- Test class name: `<ClassName>Test` (e.g., `MessageControllerTest`).

### Test naming — use the `Should` / `ShouldNot` pattern
```java
void shouldReturnSentStatusWhenMessageIsPublished()
void shouldReturnBadRequestWhenContentIsBlank()
void shouldNotAllowContentExceedingMaxLength()
```

### Structure — always AAA
```java
@Test
void shouldDoSomethingGiven() {
    // Arrange
    ...
    // Act
    ...
    // Assert
    ...
}
```

### Mock policy
- Mock **only external dependencies**: `RabbitTemplate`, `ConnectionFactory`, HTTP clients,
  database repositories.
- Use **real instances** for domain logic, services under pure unit test, and DTOs.
- Inject mocked dependencies via `@InjectMocks` + `@Mock` (`@ExtendWith(MockitoExtension.class)`).
- For Spring Boot context tests, use `@MockBean(ConnectionFactory.class)` to avoid
  requiring a live RabbitMQ broker.
- Use `@WebMvcTest` for controller slice tests; do **not** load the full context.

### Quality checklist
- Cover happy path **and** edge/boundary cases (null, empty, max length, etc.).
- Each test must be fully independent — no shared mutable state between tests.
- Extract shared setup into `@BeforeEach setUp()` to avoid duplication (DRY).
- All imports at the file header; no inline static imports inside test methods.
- Apply the FIRST principles: Fast, Independent, Repeatable, Self-validating, Timely.
- Do not write tests that only pass because mocks return hardcoded values — test real behaviour.

---

## Docker and docker-compose Rules

- `docker-compose.yml` uses `depends_on: condition: service_healthy` to guarantee RabbitMQ
  is fully ready before services start connecting.
- All Dockerfiles use **multi-stage builds** (build stage with JDK, runtime stage with JRE only).
- Replace `<YOUR_DOCKER_HUB_USERNAME>` in `docker-compose.yml` with the actual username
  before pushing to GitHub and deploying on Killercoda.
- The `SPRING_RABBITMQ_*` environment variables in `docker-compose.yml` override the
  `application.properties` defaults automatically.

---

## What to Avoid

- **Do not** hardcode RabbitMQ host/port/credentials — always use `application.properties`
  with `${ENV_VAR:default}` placeholders.
- **Do not** commit `.env` files, credentials, or personal access tokens.
- **Do not** use `@Autowired` field injection — use constructor injection via `@RequiredArgsConstructor`.
- **Do not** annotate `@Configuration` classes with Lombok annotations.
- **Do not** skip `@Valid` on `@RequestBody` parameters in controllers.
- **Do not** write multi-paragraph Javadoc blocks or comment blocks that explain WHAT the
  code does — only WHY when it is genuinely non-obvious.
- **Do not** push to `main` directly — use feature branches and PRs.
- **Do not** run `docker compose down -v` without confirming — it deletes named volumes.

---

## Adding a New Microservice

1. Create `<service-name>/` at the repository root.
2. Follow the same package structure: `com.eci.arcn.<service-name>`.
3. Copy the `pom.xml` from an existing service and update `artifactId`, `name`, and `description`.
4. Add a multi-stage `Dockerfile` matching the existing pattern.
5. Register the service in `docker-compose.yml` under the `event-network` network.
6. Apply all conventions defined in this file from the start.
