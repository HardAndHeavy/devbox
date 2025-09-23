install:
	pip install --no-cache-dir --target=/site-packages -r requirements.txt

dev: install
	fastapi dev main.py --host 0.0.0.0 --port 8000

lint: install
	ruff format . && ruff check --fix .

test: lint
	pytest

.PHONY: test
