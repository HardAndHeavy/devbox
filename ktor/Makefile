prepare:
	docker build -t ktor:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		-p 80:8080 \
		ktor:dev bash

dev: prepare
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		-p 80:8080 \
		ktor:dev make dev

test: prepare
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		ktor:dev make test

test-prod: test
	docker build -t ktor:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		ktor:prod
