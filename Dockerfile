FROM docker.url/base-image-jre-11

ARG APP_VERSION
ENV VERSION=${APP_VERSION}

LABEL source="https://github.com/allanaidalgo17/spring-rest-api" \
    maintainer="Allana Idalgo"

COPY ./target/classes/* ./app/lib/
COPY ./target/service-order-api-${VERSION}.jar ./app

EXPOSE 8080

ENTRYPOINT exec java -jar /app/service-order-api-${VERSION}.jar