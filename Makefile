.PHONY: build

build:
	cd app && ./gradlew clean build

install:
	cd app && ./gradlew clean install
