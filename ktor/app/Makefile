prepare:
	ktor new

dev:
	find src -type f | entr -r ./gradlew run

prepare-lint:
	./gradlew detektGenerateConfig

lint:
	./gradlew detekt --auto-correct

test: lint

.PHONY: test
