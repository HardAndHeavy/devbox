prepare:
	docker build -t gin:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./go-pkg:/go/pkg \
		-v ./app:/app \
		-p 80:8080 \
		gin:dev bash

dev: prepare
	docker run -it --rm \
		-v ./go-pkg:/go/pkg \
		-v ./app:/app \
		-p 80:8080 \
		gin:dev make dev

test-prod:
	docker build -t gin:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		gin:prod
