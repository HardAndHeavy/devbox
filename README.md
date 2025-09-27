# Welcome to Framework DevBox!

Tired of spending hours setting up a new web framework just to try it out? **Framework DevBox** is your solution.

This repository is a curated collection of modern web frameworks, pre-packaged into Docker containers. Each framework is configured with strict linters, a full test suite, and hot-reloading for a seamless development experience. It's the ultimate playground for developers, tech writers, and architects who want to explore, learn, and compare technologies without the setup pain.

**Focus on what matters — your code — not the configuration.**

### Core Features

- **Zero-Config Development**: Get a fully-featured development environment with a single command: `make dev`.
- **Production-Ready Blueprints**: Each framework includes separate `Dockerfile.dev` and `Dockerfile.prod` files, demonstrating best practices for development and production builds.
- **Built-in Quality Gates**: Strict linters and pre-configured tests run with `make test-prod`, simulating a CI/CD pipeline and ensuring code quality from the start.
- **Hot-Reloading Enabled**: Write code in your favorite IDE on your local machine; the app inside the container updates automatically.
- **Standardized Structure**: Every framework follows the same intuitive directory structure, making it easy to switch between them.
- **Ready-to-Use Examples**: Each service includes a "Hello World" endpoint and a practical QR code generator to get you started immediately.

### Frameworks Included

- **Go**: [Gin](https://github.com/gin-gonic/gin)
- **Kotlin**: [Ktor](https://github.com/ktorio/ktor)
- **Python**: [FastAPI](https://github.com/fastapi/fastapi), [Django](https://github.com/django/django)
- **Rust**: [Actix Web](https://github.com/actix/actix-web)
- **TypeScript**: [NestJS](https://github.com/nestjs/nest)
- **Java**: [Spring Boot](https://github.com/spring-projects/spring-boot)
- **JavaScript**: [Fastify](https://github.com/fastify/fastify)
- **Ruby**: [RoR](https://github.com/rails/rails)
- **Elixir**: [Phoenix](https://github.com/phoenixframework/phoenix)

### Getting Started

Prerequisites:
- [Docker](https://www.docker.com/get-started)
- [Make](https://www.gnu.org/software/make/)

Using any framework is simple. Just navigate to its directory and use the `Makefile` commands.

**Start the Development Server**

This command builds the development Docker image, installs all dependencies, and starts the server with hot-reloading. Your local `app` directory will be mounted into the container.

```bash
# Example for FastAPI
cd fastapi
make dev
```
The server will be available at `http://localhost`.

**Run a Production-Ready Check**

This command simulates a CI/CD pipeline. It will:
- Run the linter to check for code style issues.
- Run the test suite to ensure everything is working.
- Build a lean, optimized production Docker image.
- Run the production image locally for a final check.

```bash
# Example for FastAPI
cd fastapi
make test-prod
```
This is a great way to verify that your changes are ready for a production deployment.

### Project Structure

Every framework in this repository follows the same standardized structure:

```
.
└── [framework-name]/
    ├── app/                # All your application source code lives here.
    ├── Dockerfile.dev      # Dockerfile for the development environment with hot-reloading.
    ├── Dockerfile.prod     # Multi-stage Dockerfile for a lean production build.
    └── Makefile            # Contains the `dev` and `test-prod` commands.
```

This consistency allows you to explore different technologies without having to learn a new project layout every time.

### Included API Endpoints

Each framework comes with two pre-configured endpoints to get you started:

1.  **Hello World**:
    *   **URL**: `http://localhost/`
    *   **Description**: A simple endpoint that returns a "This is the <framework_name> in DevBox." message.

2.  **QR Code Generator**:
    *   **URL**: `http://localhost/qr?data=<your_text>`
    *   **Description**: A service that generates a QR code image from the text you provide in the `data` query parameter.
    *   **Example**: `http://localhost/qr?data=hello_world`
