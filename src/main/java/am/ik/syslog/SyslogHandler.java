package am.ik.syslog;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

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
	private static final Logger err = LoggerFactory.getLogger(SyslogHandler.class);

	@Override
	public Publisher<Void> apply(NettyInbound in, NettyOutbound out) {
		in.receive() //
				.asString() //
				.flatMapIterable(s -> Arrays.asList(s.split(Pattern.quote("\n")))) //
				.flatMap(s -> {
					int index = s.indexOf('<');
					if (index == -1) {
						err.error("invalid format string={}", s);
						return Mono.empty();
					}
					return Mono.just(s.substring(index));
				}) //
				.map(SyslogPayload::new) //
				.doOnNext(this::handleMessage) //
				.subscribe();
		return Flux.never();
	}

	void handleMessage(SyslogPayload payload) {
		Optional<String> errors = payload.errors();
		if (errors.isPresent()) {
			err.error("error={}, undecoded={}", errors.get(), payload.undecoded());
			return;
		}
		log.info(
				"timestamp:{}\tfacility:{}\tseverity:{}\thost:{}\tapp:{}\tprocId:{}\tmsgId:{}\tstructuredData:{}\tmsg:{}",
				payload.timestamp(), payload.facility(), payload.severityText(),
				payload.host(), payload.appName(), payload.procId(), payload.msgId(),
				payload.structuredData(), payload.message().trim());
	}
}
