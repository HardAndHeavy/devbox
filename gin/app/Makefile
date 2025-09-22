prepare:
	go mod init app
	chmod a+rw -R .

install:
	go mod tidy

dev:
	air

lint:
	golangci-lint run --fix

test: lint
	go test -v

.PHONY: test
