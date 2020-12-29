fresh:
	gradlew clean build
	docker-compose up --build

start:
	gradlew build
	docker-compose up --build

s:
	make start

db:
	docker-compose up live_db phpmyadmin
	docker-compose build

test:
	docker-compose run ./gradlew test