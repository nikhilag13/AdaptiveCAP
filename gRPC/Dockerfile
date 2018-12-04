#Docker File for java -server

FROM openjdk:8-jdk

#RUN apt-get update && apt-get install inetutils-ping

RUN mkdir -p /app

COPY target/grpctest-1.0-SNAPSHOT-jar-with-dependencies.jar /app/
COPY run.sh /app/

ENTRYPOINT ["bash", "/app/run.sh"]
