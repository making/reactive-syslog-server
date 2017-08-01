package am.ik.syslog;

import java.util.function.BiFunction;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.NettyInbound;
import reactor.ipc.netty.NettyOutbound;

public class SyslogHandler
		implements BiFunction<NettyInbound, NettyOutbound, Publisher<Void>> {
	private static final Logger log = LoggerFactory.getLogger("LOG");

	@Override
	public Publisher<Void> apply(NettyInbound in, NettyOutbound out) {
		Flux<SyslogPayload> input = in.receive().asString() //
				.flatMap(s -> {
					int index = s.indexOf('<');
					if (index == -1) {
						return Mono.empty();
					}
					return Mono.just(s.substring(index));
				}) //
				.map(SyslogPayload::new);

		input.doOnNext(payload -> {
			log.info(
					"ts:{}\tfacility:{}\tseverity:{}\thost:{}\tapp:{}\tprocId:{}\tmsgId:{}\t\tmsg:{}",
					payload.timestamp(), payload.facility(), payload.severityText(),
					payload.host(), payload.appName(), payload.procId(), payload.msgId(),
					payload.message());
		}).subscribe();
		return Flux.never();
	}
}
