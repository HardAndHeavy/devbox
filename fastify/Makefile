init:
	docker build -t fastify:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		fastify:dev bash

test-prod:
	docker build -t fastify:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:3000 \
		fastify:prod
