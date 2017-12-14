package am.ik.syslog;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

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
	private static final String LF = "\n";
	private static final String SPLIT_PATTERN = "(?<=" + LF + ")";

	@Override
	public Publisher<Void> apply(NettyInbound in, NettyOutbound out) {
		Flux<String> incoming = in.receive().asString();
		incoming.compose(this::convert) //
				.doOnNext(this::handleMessage) //
				.subscribe();
		return Flux.never();
	}

	Flux<SyslogPayload> convert(Flux<String> incoming) {
		return incoming.flatMapIterable(s -> Arrays.asList(s.split(SPLIT_PATTERN))) //
				.windowUntil(s -> s.endsWith(LF)) //
				.flatMap(f -> f.collect(Collectors.joining())) //
				.map(String::trim) //
				.filter(s -> !s.isEmpty()) //
				.flatMap(this::skipOctetCounting) //
				.onBackpressureDrop(this::onDropped) //
				.map(SyslogPayload::new);
	}

	Publisher<String> skipOctetCounting(String s) {
		// Ignore octet counting in RFC 6587
		int index = s.indexOf('<');
		if (index == -1) {
			err.error("Unexpected format ({})", s);
			return Mono.empty();
		}
		return Mono.just(s.substring(index));
	}

	void onDropped(String s) {
		err.warn("Dropped! {}", s);
	}

	void handleMessage(SyslogPayload payload) {
		Optional<String> errors = payload.errors();
		if (errors.isPresent()) {
			err.error("Decode Error ({}), undecoded={}", errors.get(),
					payload.undecoded());
			return;
		}
		log.info(
				"timestamp:{}\tfacility:{}\tseverity:{}\thost:{}\tapp:{}\tprocId:{}\tmsgId:{}\tstructuredData:{}\tmsg:{}",
				payload.timestamp(), payload.facility(), payload.severityText(),
				payload.host(), payload.appName(), payload.procId(), payload.msgId(),
				payload.structuredData(), payload.message().trim());
	}
}
