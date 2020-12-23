fresh:
	gradlew clean build
	docker-compose up --build

start:
	gradlew build
	docker-compose up --build

s:
	make start

test:
	docker-compose run ./gradlew test