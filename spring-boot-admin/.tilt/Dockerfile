FROM openjdk:11.0.11-slim

WORKDIR /app
ADD BOOT-INF/lib /app/lib
ADD META-INF /app/META-INF
ADD BOOT-INF/classes /app

ENTRYPOINT java -cp .:./lib/* br.com.sample.sba.Application