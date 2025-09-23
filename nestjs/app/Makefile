prepare:
	nest new .
	chmod a+rw -R .

install:
	npm i

dev: install
	npm run start:dev

lint: install
	npm run format
	npm run lint

test: lint
	npm run test

.PHONY: test
