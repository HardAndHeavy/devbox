prepare:
	ktor new

dev:
	find src -type f | entr -r ./gradlew run

prepare-lint:
	./gradlew detektGenerateConfig

lint:
	./gradlew detekt --auto-correct

test: lint
	./gradlew test

.PHONY: test
