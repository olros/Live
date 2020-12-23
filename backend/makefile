fresh:
	make clean
	docker-compose up --build

clean:
	gradlew clean build

run:
	gradlew build
	docker-compose up --build

test:
	docker-compose run ./gradlew test