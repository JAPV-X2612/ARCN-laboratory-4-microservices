# Laboratory 4 — Event-Driven Microservices with Spring Boot and RabbitMQ

**Course:** Arquitectura Centrada en el Negocio (ARCN_M)  
**Author:** Jesús Pinzón  
**Date:** 2026-04-23

---

## Overview

This laboratory demonstrates an event-driven architecture using two independent Spring Boot
microservices that communicate asynchronously through a RabbitMQ message broker. The entire
system is orchestrated with Docker Compose and deployed on Killercoda.

```
┌─────────────────────┐        AMQP         ┌──────────────────────┐
│   Producer Service  │ ──────────────────► │  Consumer Service    │
│   (Port 8080)       │   messages.exchange  │  (No HTTP port)      │
│                     │   messages.queue     │                      │
│  POST /api/messages │                      │  @RabbitListener     │
│       /send         │                      │  processes messages  │
└─────────────────────┘                      └──────────────────────┘
           │                                            │
           └────────────────┬───────────────────────────┘
                            │
                  ┌─────────▼────────┐
                  │    RabbitMQ      │
                  │  AMQP: 5672      │
                  │  UI:   15672     │
                  └──────────────────┘
```

---

## Project Structure

```
ARCN-laboratory-4-microservices/
├── .devcontainer/
│   └── devcontainer.json
├── assets/images/              ← evidence screenshots
├── producer-service/
│   ├── src/main/java/com/eci/arcn/producer/
│   │   ├── ProducerApplication.java
│   │   ├── config/RabbitMQConfig.java
│   │   ├── controller/MessageController.java
│   │   ├── dto/MessageRequestDto.java
│   │   ├── dto/MessageResponseDto.java
│   │   └── service/
│   │       ├── MessagePublisherService.java
│   │       └── impl/MessagePublisherServiceImpl.java
│   ├── src/main/resources/application.properties
│   ├── src/test/...
│   ├── pom.xml
│   └── Dockerfile
├── consumer-service/
│   ├── src/main/java/com/eci/arcn/consumer/
│   │   ├── ConsumerApplication.java
│   │   ├── config/RabbitMQConfig.java
│   │   ├── listener/MessageListener.java
│   │   └── service/
│   │       ├── MessageProcessorService.java
│   │       └── impl/MessageProcessorServiceImpl.java
│   ├── src/main/resources/application.properties
│   ├── src/test/...
│   ├── pom.xml
│   └── Dockerfile
└── docker-compose.yml
```

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker Desktop
- Docker Hub account
- Git

---

## Phase 1 — Build and Publish Docker Images (Local Machine)

### Step 1 — Build and test the Producer service

```bash
cd producer-service
mvn clean verify
```

> **Screenshot:** `1-producer-tests-passing.png`  
> Capture the terminal showing all tests passing and the JaCoCo coverage summary.

### Step 2 — Build and test the Consumer service

```bash
cd ../consumer-service
mvn clean verify
```

> **Screenshot:** `2-consumer-tests-passing.png`  
> Capture the terminal showing all tests passing and the JaCoCo coverage summary.

### Step 3 — Log in to Docker Hub

```bash
docker login -u <YOUR_DOCKER_HUB_USERNAME>
```

Enter your Docker Hub Personal Access Token when prompted (generate one at
**Docker Hub → Account Settings → Personal Access Tokens**).

> **Screenshot:** `3-docker-hub-login.png`  
> Capture the terminal showing `Login Succeeded`.

### Step 4 — Build and push the Producer Docker image

```bash
cd ../producer-service
docker build -t <YOUR_DOCKER_HUB_USERNAME>/producer-service:latest .
docker push <YOUR_DOCKER_HUB_USERNAME>/producer-service:latest
```

> **Screenshot:** `4-producer-image-pushed.png`  
> Capture the terminal showing all layers pushed and the image digest.

### Step 5 — Build and push the Consumer Docker image

```bash
cd ../consumer-service
docker build -t <YOUR_DOCKER_HUB_USERNAME>/consumer-service:latest .
docker push <YOUR_DOCKER_HUB_USERNAME>/consumer-service:latest
```

> **Screenshot:** `5-consumer-image-pushed.png`  
> Capture the terminal showing all layers pushed and the image digest.

### Step 6 — Verify images on Docker Hub

Open [https://hub.docker.com](https://hub.docker.com) and confirm that both
`producer-service` and `consumer-service` repositories appear with the `latest` tag.

> **Screenshot:** `6-docker-hub-repositories.png`  
> Capture the Docker Hub dashboard showing both repositories.

### Step 7 — Update docker-compose.yml and push to GitHub

Replace `<YOUR_DOCKER_HUB_USERNAME>` in `docker-compose.yml` with your actual username,
then commit and push the repository:

```bash
cd ..
git add docker-compose.yml
git commit -m "chore: set Docker Hub username in docker-compose"
git push origin main
```

---

## Phase 2 — Deploy and Test on Killercoda

### Step 8 — Access Killercoda Ubuntu Playground

Go to [https://killercoda.com/playgrounds/scenario/ubuntu](https://killercoda.com/playgrounds/scenario/ubuntu)
and sign in. You will see a Linux terminal ready to use.

> **Screenshot:** `7-killercoda-playground-ready.png`  
> Capture the Killercoda browser terminal after it finishes loading.

### Step 9 — Clone the repository

```bash
git clone https://github.com/<YOUR_GITHUB_USERNAME>/ARCN-laboratory-4-microservices.git
cd ARCN-laboratory-4-microservices
```

> **Screenshot:** `8-git-clone.png`  
> Capture the terminal showing the clone output and the `cd` into the project folder.

### Step 10 — Start all services with Docker Compose

```bash
docker compose up -d
```

If Docker Compose V1 is installed instead, use:

```bash
docker-compose up -d
```

> **Screenshot:** `9-docker-compose-up.png`  
> Capture the terminal showing all three containers being created and started.

### Step 11 — Verify all containers are running

```bash
docker compose ps
```

All three services (`rabbitmq`, `producer-service`, `consumer-service`) must show
status **Up** or **running**.

> **Screenshot:** `10-containers-running.png`  
> Capture the `docker compose ps` output with all services in a healthy state.

### Step 12 — Send a message to the Producer API

```bash
curl -s -X POST "http://localhost:8080/api/messages/send" \
  -H "Content-Type: application/json" \
  -d '{"content":"Hello from Killercoda"}' | cat
```

Expected response:

```json
{
  "message": "Hello from Killercoda",
  "status": "SENT",
  "timestamp": "2026-04-23T10:00:00"
}
```

> **Screenshot:** `11-send-message-curl.png`  
> Capture the curl command and the JSON response showing `"status": "SENT"`.

### Step 13 — Verify the Consumer received the message

```bash
docker compose logs consumer
```

You should see log lines containing:

```
Received message from queue: 'Hello from Killercoda'
Processing message: 'Hello from Killercoda'
```

> **Screenshot:** `12-consumer-logs.png`  
> Capture the consumer logs showing the received and processed message.

### Step 14 — Access the RabbitMQ Management UI

1. Click **Traffic / Port** in the top-right corner of the Killercoda terminal.
2. Enter port **15672** and confirm.
3. Log in with credentials: `guest` / `guest`.

> **Screenshot:** `13-rabbitmq-management-login.png`  
> Capture the RabbitMQ Management login page accessed via the Killercoda public URL.

### Step 15 — Inspect the queue in RabbitMQ UI

Navigate to **Queues → messages.queue**. After all messages are consumed the queue
should show **0 ready** messages and an active consumer connection.

> **Screenshot:** `14-rabbitmq-queue-status.png`  
> Capture the `messages.queue` detail page showing queue metrics and bound consumers.

### Step 16 — Send multiple messages to verify throughput

```bash
for msg in "First" "Second" "Third"; do
  curl -s -X POST "http://localhost:8080/api/messages/send" \
    -H "Content-Type: application/json" \
    -d "{\"content\":\"$msg message\"}" | cat
  echo
done
```

Then check the consumer logs again:

```bash
docker compose logs consumer --tail 20
```

> **Screenshot:** `15-multiple-messages-processed.png`  
> Capture the consumer logs showing all three messages processed in order.

---

## Evidence Images

| # | File | Description |
|---|------|-------------|
| 1 | `1-producer-tests-passing.png` | Producer `mvn clean verify` — all tests green + JaCoCo report |
| 2 | `2-consumer-tests-passing.png` | Consumer `mvn clean verify` — all tests green + JaCoCo report |
| 3 | `3-docker-hub-login.png` | `docker login` terminal showing `Login Succeeded` |
| 4 | `4-producer-image-pushed.png` | Producer image pushed to Docker Hub |
| 5 | `5-consumer-image-pushed.png` | Consumer image pushed to Docker Hub |
| 6 | `6-docker-hub-repositories.png` | Docker Hub dashboard with both repositories |
| 7 | `7-killercoda-playground-ready.png` | Killercoda Ubuntu Playground terminal loaded |
| 8 | `8-git-clone.png` | `git clone` output in Killercoda |
| 9 | `9-docker-compose-up.png` | `docker compose up -d` output |
| 10 | `10-containers-running.png` | `docker compose ps` — all services Up |
| 11 | `11-send-message-curl.png` | curl POST and JSON response with `"status": "SENT"` |
| 12 | `12-consumer-logs.png` | Consumer logs showing received and processed message |
| 13 | `13-rabbitmq-management-login.png` | RabbitMQ Management UI login page |
| 14 | `14-rabbitmq-queue-status.png` | `messages.queue` detail with 0 ready messages |
| 15 | `15-multiple-messages-processed.png` | Consumer logs with three messages processed |

---

## Key Design Decisions

| Decision | Principle |
|---|---|
| `MessagePublisherService` / `MessageProcessorService` interfaces | DIP (SOLID) — controllers and listeners depend on abstractions |
| `MessageRequestDto` + `MessageResponseDto` | SRP — HTTP contract is decoupled from messaging domain |
| `@Value` for topology properties via `application.properties` | Externalised configuration; overridden by Docker Compose env vars |
| `depends_on: condition: service_healthy` in Docker Compose | Guarantees RabbitMQ is fully ready before services connect |
| Multi-stage Dockerfiles | Smaller runtime images; JDK not shipped to production |
| `CopyOnWriteArrayList` in `MessageProcessorServiceImpl` | Thread-safe: multiple AMQP listener threads may deliver concurrently |
| `@MockBean(ConnectionFactory.class)` in application tests | Isolates Spring context tests from infrastructure dependencies |
