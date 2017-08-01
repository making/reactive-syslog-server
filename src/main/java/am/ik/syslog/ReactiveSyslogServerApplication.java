package am.ik.syslog;

import java.util.Optional;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.ipc.netty.tcp.BlockingNettyContext;
import reactor.ipc.netty.tcp.TcpServer;

public class ReactiveSyslogServerApplication {
	private static final Logger log = LoggerFactory
			.getLogger(ReactiveSyslogServerApplication.class);

	public static void main(String[] args) {
		TcpServer tcpServer = TcpServer.create("0.0.0.0", port());
		Consumer<BlockingNettyContext> onStart = context -> {
			log.info("Started. (host={},port={})", context.getHost(), context.getPort());
		};
		tcpServer.startAndAwait(new SyslogHandler(), onStart);
	}

	static int port() {
		return Optional.ofNullable(System.getenv("PORT")).map(Integer::valueOf)
				.orElse(10514);
	}
}
