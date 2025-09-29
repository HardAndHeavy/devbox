prepare:
	laravel new app
	chmod a+rw -R app

install:
	if [ ! -f ".env" ]; then \
		cp .env.example .env; fi
	if [ ! -f "database/database.sqlite" ]; then \
		touch database/database.sqlite && \
		php artisan migrate; fi
	npm install && npm run build
	composer install

dev: install
	composer run dev

lint: install
	composer lint-fix

test: lint
	php artisan test

.PHONY: test
