dev:
	watchexec -w src -r cargo run

lint:
	cargo check
	cargo fmt
	cargo clippy -- -D warnings

test: lint
	cargo test

.PHONY: test
