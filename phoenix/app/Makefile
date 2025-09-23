prepare:
	mix phx.new . --no-ecto
	chmod a+rw -R .

install:
	mix deps.get
	mix deps.compile

dev: install
	mix phx.server

lint:
	mix format
	mix credo --strict
	mix dialyzer
	mix sobelow --config

test: lint
	mix test

.PHONY: test
