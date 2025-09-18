prepare:
	docker build -t ror:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		ror:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:3000 \
		ror:dev make dev

test-prod:
	docker build \
		--secret id=rails_master_key,src=app/config/test-master.key \
		-t ror:prod \
		-f ./Dockerfile.prod \
		.
	docker run -it --rm \
		-e RAILS_MASTER_KEY=$$(cat app/config/test-master.key) \
		-p 80:3000 \
		ror:prod
