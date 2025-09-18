init:
	docker build -t django:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./site-packages:/site-packages \
		-v ./app:/app \
		-p 80:8000 \
		django:dev bash

test-prod:
	docker build -t django:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8000 \
		django:prod
