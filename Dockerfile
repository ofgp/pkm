FROM hub.ibitcome.com/library/java:alpine
WORKDIR /app
ADD . /app
RUN cp ./bin/docker-entrypoint.sh /entrypoint.sh && mv /app/bin/.m2 /root/ && cd /app && mvn clean package -Dmaven.test.skip=true -Dmaven.repo.local=/root/.m2/repository/
VOLUME ["/data"]
ENTRYPOINT ["/entrypoint.sh"]
CMD ["supervisord"]
