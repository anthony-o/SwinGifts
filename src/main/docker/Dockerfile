FROM adoptopenjdk:11-jre-hotspot

RUN mkdir -p /app
WORKDIR /app
COPY ./src/main/docker/app/entrypoint.sh /app/entrypoint.sh
RUN chmod +x /app/entrypoint.sh
COPY ./target/*.jar /app/swingifts.jar

CMD /app/entrypoint.sh
