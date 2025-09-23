init-bundle:
	bundle config set path 'bundle'

prepare: init-bundle
	rails new .
	chmod a+rw -R .

install: init-bundle
	bundle install

dev: install
	bin/rails server -b 0.0.0.0 -p 3000

lint: init-bundle
	bundle exec rubocop -a

test: lint

.PHONY: test
