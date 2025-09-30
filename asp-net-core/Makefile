prepare:
	docker build -t asp-net:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8080 \
		asp-net:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8080 \
		asp-net:dev make dev

test: prepare
	docker run -it --rm \
		-v ./app:/app \
		asp-net:dev make test

test-prod: test
	docker build -tasp-net:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		asp-net:prod
