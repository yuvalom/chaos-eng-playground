FROM maven:3.8.2-openjdk-11-slim as deps

# copy the pom files 
COPY app/pom.xml app/pom.xml
COPY infra/pom.xml infra/pom.xml
COPY logic/pom.xml logic/pom.xml
COPY parent/pom.xml parent/pom.xml

COPY pom.xml .

#RUN mvn dependency:go-offline -B --fail-never -P linux

FROM maven:3.8.2-openjdk-11-slim as builder

# copy the src files 
#COPY --from=deps /root/.m2 /root/.m2
COPY --from=deps . .
COPY app/src app/src
COPY infra/src infra/src
COPY logic/src logic/src


RUN mvn clean install -T 1C -DskipTests -P linux && \
  mkdir -p /docker/build && \
  cp infra/target/workspace/* /docker/build/

COPY build/docker/*.sh /docker/build/
FROM ubuntu:20.04

RUN apt-get update && apt-get install -y openjdk-11-jdk

ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV LD_LIBRARY_PATH /opt/web-service-app/lib/
ENV LOG_PATH /var/log/web-service-app/

COPY --from=builder /docker/build /docker/build

RUN tar -xvf /docker/build/rootfs.tar.gz -C /  && \
  mkdir /build

RUN cp /docker/build/docker-entrypoint.sh /build && \
  chmod +x /build/docker-entrypoint.sh

WORKDIR /build

### ENVIRONMENT VARIABLES ###

ENV MIN_HEAP_SIZE=
ENV MAX_HEAP_SIZE=
ENV PRINT_GC=
ENV HORIZON_PREFER_IPV6 true

ENV MANAGEMENT_SERVER_PORT 8081

### DB ENVS ###
ENV DB_HOST localhost
ENV DB_PORT 3306
ENV DB_USER=
ENV DB_PASS=


### END ENVIRONMENT VARIABLES ###

ENTRYPOINT ["/build/docker-entrypoint.sh"]
