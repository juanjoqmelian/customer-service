FROM maven:latest
MAINTAINER Juan Quintana <juan.quintana@luxuriem.com> 
ADD . /build/customer-service
WORKDIR /build/customer-service
RUN mvn clean package -U
WORKDIR ./target
CMD java -jar customer-service-1.0-SNAPSHOT.jar
