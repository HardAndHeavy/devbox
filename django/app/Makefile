prepare:
	django-admin startproject app .
	chmod a+rw -R .

install:
	pip install --no-cache-dir --target=/site-packages -r requirements.txt

dev: install
	python manage.py runserver 0.0.0.0:8000

lint: install
	ruff check --fix . && ruff format .

test: lint
	pytest

.PHONY: test
