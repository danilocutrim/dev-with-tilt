FROM openjdk:11.0.11-slim AS TEMP_BUILD_IMAGE
ENV APP_HOME=/usr/app/
WORKDIR $APP_HOME
COPY build.gradle settings.gradle gradlew $APP_HOME
COPY gradle $APP_HOME/gradle
COPY . .
RUN ./gradlew build

FROM openjdk:11.0.11-slim
ENV ARTIFACT_NAME=case-monitoring-twitter-api-1.0.0.jar
ENV APP_HOME=/usr/app
WORKDIR $APP_HOME
COPY --from=TEMP_BUILD_IMAGE $APP_HOME/build/libs/$ARTIFACT_NAME .
EXPOSE 9081
CMD ["java","-jar","case-monitoring-twitter-api-1.0.0.jar"]
