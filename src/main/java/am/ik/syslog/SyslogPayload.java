package am.ik.syslog;

import java.util.*;

import org.springframework.integration.syslog.RFC5424SyslogParser;
import org.springframework.integration.syslog.SyslogHeaders;

public class SyslogPayload {
	private static final RFC5424SyslogParser parser = new RFC5424SyslogParser();
	final Map<String, ?> payload;

	public SyslogPayload(String line) {
		this.payload = parser.parse(line.endsWith("]") ? line + " " : line, 0, false);
	}

	public final Integer facility() {
		return (Integer) this.payload.get(SyslogHeaders.FACILITY);
	}

	public final Integer severity() {
		return (Integer) this.payload.get(SyslogHeaders.SEVERITY);
	}

	public final String severityText() {
		return (String) this.payload.get(SyslogHeaders.SEVERITY_TEXT);
	}

	public final String timestamp() {
		return (String) this.payload.get(SyslogHeaders.TIMESTAMP);
	}

	public final String host() {
		return (String) this.payload.get(SyslogHeaders.HOST);
	}

	public final String tag() {
		return (String) this.payload.get(SyslogHeaders.TAG);
	}

	public final String message() {
		return Objects.toString(this.payload.get(SyslogHeaders.MESSAGE), "");
	}

	public final String appName() {
		return (String) this.payload.get(SyslogHeaders.APP_NAME);
	}

	public final String procId() {
		return (String) this.payload.get(SyslogHeaders.PROCID);
	}

	public final String msgId() {
		return (String) this.payload.get(SyslogHeaders.MSGID);
	}

	@SuppressWarnings("unchecked")
	public final List<String> structuredData() {
		return (List<String>) this.payload.get(SyslogHeaders.STRUCTURED_DATA);
	}

	public final Integer version() {
		return (Integer) this.payload.get(SyslogHeaders.VERSION);
	}

	public final Optional<String> errors() {
		String errors = (String) this.payload.get(SyslogHeaders.ERRORS);
		return Optional.ofNullable(errors);
	}

	public final String undecoded() {
		return (String) this.payload.get(SyslogHeaders.UNDECODED);

	}

	@Override
	public String toString() {
		Map<String, Object> copy = new LinkedHashMap<>();
		if (this.payload != null) {
			copy.putAll(this.payload);
		}
		copy.remove(SyslogHeaders.UNDECODED);
		copy.remove(SyslogHeaders.MESSAGE);
		return String.valueOf(copy);
	}
}
