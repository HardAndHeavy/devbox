prepare:
	mix phx.new .
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

.PHONY: test
