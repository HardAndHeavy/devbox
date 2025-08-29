init:
	docker build -t fastapi:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./site-packages:/site-packages \
		-v ./app:/app \
		-p 80:8000 \
		fastapi:dev bash

test-prod:
	docker build -t fastapi:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8000 \
		fastapi:prod
