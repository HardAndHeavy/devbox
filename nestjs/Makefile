prepare:
	docker build -t nest:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		nest:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		nest:dev make dev

test-prod:
	docker build -t nest:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:3000 \
		nest:prod
