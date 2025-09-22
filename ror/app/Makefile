init-bundle:
	bundle config set path 'bundle'

prepare: init-bundle
	rails new .
	chmod a+rw -R .

install: init-bundle
	bundle install

dev: install
	bin/rails server -b 0.0.0.0 -p 3000

lint:
	echo lint

test: lint

.PHONY: test
