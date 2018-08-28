FROM hub.ibitcome.com/library/java:alpine
WORKDIR /app
ADD . /app
RUN mv /app/bin/.m2 /root/ && cd /app && mvn clean package -Dmaven.test.skip=true -Dmaven.repo.local=/root/.m2/repository/
VOLUME ["/data"]
CMD ["java","-Xmx1024m","-jar","target/pkm.jar","--spring.config.location=conf/application.properties"]
