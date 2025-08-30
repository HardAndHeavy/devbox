init:
	docker build -t gin:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./app:/app \
		-p 80:8080 \
		gin:dev bash

test-prod:
	docker build -t gin:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		gin:prod
