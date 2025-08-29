init:
	docker build -t nest:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		nest:dev bash

test-prod:
	docker build -t nest:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:3000 \
		nest:prod
