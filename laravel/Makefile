prepare:
	docker build -t laravel:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8000 \
		laravel:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8000 \
		laravel:dev make dev

test: prepare
	docker run -it --rm \
		-v ./app:/app \
		laravel:dev make test

test-prod: test
	docker build \
		-t laravel:prod \
		-f ./Dockerfile.prod \
		.
	docker run -it --rm \
		-e SERVER_NAME=http://localhost \
		-p 80:80 \
		-p 443:443 \
		laravel:prod
