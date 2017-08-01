# reactive-syslog-server

Simple TCP Server that parses and print [RFC 5424](https://tools.ietf.org/html/rfc5424) log

```
./mvnw clean package -DskipTests=true
java -jar target/reactive-syslog-server-0.0.1-SNAPSHOT.jar
```

If you want this to listen on 514 port: 

```
PORT=514 sudo -E java -jar target/reactive-syslog-server-0.0.1-SNAPSHOT.jar
```
