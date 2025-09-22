prepare:
	docker build -t django:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./site-packages:/site-packages \
		-v ./app:/app \
		-p 80:8000 \
		django:dev bash

dev: prepare
	docker run -it --rm \
		-v ./site-packages:/site-packages \
		-v ./app:/app \
		-p 80:8000 \
		django:dev make dev

test: prepare
	docker run -it --rm \
		-v ./site-packages:/site-packages \
		-v ./app:/app \
		django:dev make test

test-prod: test
	docker build -t django:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8000 \
		django:prod
