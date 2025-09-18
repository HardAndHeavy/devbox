prepare:
	spring init --dependencies=web app
	chmod a+rw -R app

dev:
	find src -type f | entr -r ./gradlew bootRun
