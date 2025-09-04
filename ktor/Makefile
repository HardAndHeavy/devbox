init:
	docker build -t ktor:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		-p 80:8080 \
		ktor:dev bash

test-prod:
	docker build -t ktor:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		ktor:prod
