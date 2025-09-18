prepare:
	docker build -t actix-web:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8080 \
		actix-web:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8080 \
		actix-web:dev make dev

test-prod:
	docker build -t actix-web:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		actix-web:prod
