prepare:
	docker build -t phoenix:dev -f ./Dockerfile.dev .

bash: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:4000 \
		phoenix:dev bash

dev: prepare
	docker run -it --rm \
		-v ./app:/app \
		-p 80:4000 \
		phoenix:dev make dev

test: prepare
	docker run -it --rm \
		-v ./app:/app \
		phoenix:dev make test

test-prod: test
	docker build -t phoenix:prod -f ./Dockerfile.prod .
	docker run -it --rm \
		-e DATABASE_URL=ecto://user:pass@localhost/db \
		-e SECRET_KEY_BASE=4W6Gm9lnsz12blz/Vp0+q+9+G/T9vyVVsqUF4SK3gBr0zXsBZmsH1wHZf1SI9XfWquk/mB38CDBYKlw+ydDUPw== \
	  -e PHX_SERVER=true \
		-p 80:4000 \
		phoenix:prod
