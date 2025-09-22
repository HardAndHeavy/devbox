prepare:
	docker build -t fastify:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		fastify:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		fastify:dev make dev

test: prepare
	docker run -it --rm \
		-v ./app:/app \
		fastify:dev make test

test-prod: test
	docker build -t fastify:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:3000 \
		fastify:prod
