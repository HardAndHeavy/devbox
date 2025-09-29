prepare:
	laravel new app
	chmod a+rw -R app

install:
	npm install && npm run build
	composer install

dev: install
	composer run dev

lint: install
	composer lint-fix

test: lint
	php artisan test

.PHONY: test
