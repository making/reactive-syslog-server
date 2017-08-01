package am.ik.syslog;

import java.util.function.BiFunction;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;

public class SyslogHandler
		implements BiFunction<NettyInbound, NettyOutbound, Publisher<Void>> {
	private static final Logger log = LoggerFactory.getLogger("LOG");

	@Override
	public Publisher<Void> apply(NettyInbound in, NettyOutbound out) {
		Flux<SyslogPayload> input = in.receive().asString().map(SyslogPayload::new);
		input.doOnNext(payload -> {
			log.info("ts:{}\tseverity:{}\thost:{}\tapp:{}\tproc:{}\tmsg:{}",
					payload.timestamp(), payload.severityText(), payload.host(),
					payload.appName(), payload.procId(), payload.message());
		}).subscribe();
		return Flux.never();
	}
}
