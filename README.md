# reactive-syslog-server

Simple TCP Server that parses and print [RFC 5424](https://tools.ietf.org/html/rfc5424) log

```
./mvnw clean package -DskipTests=true
java -jar target/reactive-syslog-server-0.0.1-SNAPSHOT.jar
```
This server listens on 10514 port by default.

```
echo "<6>1 2017-12-14T05:14:15.000003-07:00 192.0.2.1 myproc 8710 - - Hello World" | nc localhost 10514
```

If you want this to listen on 514 port: 

```
PORT=514 sudo -E java -jar target/reactive-syslog-server-0.0.1-SNAPSHOT.jar
```