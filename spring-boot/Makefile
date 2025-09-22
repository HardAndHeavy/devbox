prepare:
	docker build -t sprig-boot:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		-p 80:8080 \
		sprig-boot:dev bash

dev: prepare
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		-p 80:8080 \
		sprig-boot:dev make dev

test: prepare
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		sprig-boot:dev make test

test-prod: test
	docker build -t sprig-boot:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		sprig-boot:prod
