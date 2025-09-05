init:
	docker build -t sprig-boot:dev -f ./Dockerfile.dev .

bash:
	docker run -it --rm \
		-v ./.gradle:/root/.gradle \
		-v ./app:/app \
		-p 80:8080 \
		sprig-boot:dev bash

test-prod:
	docker build -t sprig-boot:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-p 80:8080 \
		sprig-boot:prod
