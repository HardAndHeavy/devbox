install:
	npm i

dev: install
	npm run nodemon -- --exec npm run start

lint: install
	npx eslint . --fix

test: lint
	npm test

.PHONY: test
