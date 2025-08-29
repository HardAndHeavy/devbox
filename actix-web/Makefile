init:
	docker build -t actix-web:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8080 \
		actix-web:dev bash

test-prod:
	docker build -t actix-web:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		actix-web:prod
