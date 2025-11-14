FROM amazoncorretto:11

ENV TZ=Asia/Seoul

COPY lifebookshelf-0.0.1-SNAPSHOT.jar lifebookshelf.jar

CMD java -jar -Dspring.profiles.active=prod lifebookshelf.jar
