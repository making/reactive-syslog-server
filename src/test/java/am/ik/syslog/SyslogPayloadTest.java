package am.ik.syslog;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * https://github.com/mcuadros/go-syslog/blob/master/internal/syslogparser/rfc5424/rfc5424_test.go
 */
public class SyslogPayloadTest {

	@Test
	public void noSTRUCTURED_DATA1() {
		SyslogPayload payload = new SyslogPayload(
				"<34>1 2003-10-11T22:14:15.003Z mymachine.example.com su - ID47 - 'su root' failed for lonvick on /dev/pts/8");

		assertThat(payload.facility()).isEqualTo(4);
		assertThat(payload.severity()).isEqualTo(2);
		assertThat(payload.version()).isEqualTo(1);
		assertThat(payload.timestamp()).isEqualTo("2003-10-11T22:14:15.003Z");
		assertThat(payload.host()).isEqualTo("mymachine.example.com");
		assertThat(payload.appName()).isEqualTo("su");
		assertThat(payload.procId()).isEqualTo("-");
		assertThat(payload.msgId()).isEqualTo("ID47");
		assertThat(payload.structuredData()).isNull();
		assertThat(payload.message())
				.isEqualTo("'su root' failed for lonvick on /dev/pts/8");
	}

	@Test
	public void noSTRUCTURED_DATA2() {
		SyslogPayload payload = new SyslogPayload(
				"<165>1 2003-08-24T05:14:15.000003-07:00 192.0.2.1 myproc 8710 - - %% It's time to make the do-nuts.");

		assertThat(payload.facility()).isEqualTo(20);
		assertThat(payload.severity()).isEqualTo(5);
		assertThat(payload.version()).isEqualTo(1);
		assertThat(payload.timestamp()).isEqualTo("2003-08-24T05:14:15.000003-07:00");
		assertThat(payload.host()).isEqualTo("192.0.2.1");
		assertThat(payload.appName()).isEqualTo("myproc");
		assertThat(payload.procId()).isEqualTo("8710");
		assertThat(payload.msgId()).isEqualTo("-");
		assertThat(payload.structuredData()).isNull();
		assertThat(payload.message()).isEqualTo("%% It's time to make the do-nuts.");
	}

	@Test
	public void withSTRUCTURED_DATA() {
		SyslogPayload payload = new SyslogPayload(
				"<165>1 2003-10-11T22:14:15.003Z mymachine.example.com evntslog - ID47 [exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"] An application event log entry...");

		assertThat(payload.facility()).isEqualTo(20);
		assertThat(payload.severity()).isEqualTo(5);
		assertThat(payload.version()).isEqualTo(1);
		assertThat(payload.timestamp()).isEqualTo("2003-10-11T22:14:15.003Z");
		assertThat(payload.host()).isEqualTo("mymachine.example.com");
		assertThat(payload.appName()).isEqualTo("evntslog");
		assertThat(payload.procId()).isEqualTo("-");
		assertThat(payload.msgId()).isEqualTo("ID47");
		assertThat(payload.structuredData()).containsExactly(
				"[exampleSDID@32473 iut=\"3\" eventSource=\"Application\" eventID=\"1011\"]");
		assertThat(payload.message()).isEqualTo("An application event log entry...");
	}

	@Test
	public void STRUCTURED_DATA_only() {
		SyslogPayload payload = new SyslogPayload(
				"<165>1 2003-10-11T22:14:15.003Z mymachine.example.com evntslog - ID47 [exampleSDID@32473 iut=\"3\" eventSource= \"Application\" eventID=\"1011\"][examplePriority@32473 class=\"high\"]");
		assertThat(payload.facility()).isEqualTo(20);
		assertThat(payload.severity()).isEqualTo(5);
		assertThat(payload.version()).isEqualTo(1);
		assertThat(payload.timestamp()).isEqualTo("2003-10-11T22:14:15.003Z");
		assertThat(payload.host()).isEqualTo("mymachine.example.com");
		assertThat(payload.appName()).isEqualTo("evntslog");
		assertThat(payload.procId()).isEqualTo("-");
		assertThat(payload.msgId()).isEqualTo("ID47");
		assertThat(payload.structuredData()).containsExactly(
				"[exampleSDID@32473 iut=\"3\" eventSource= \"Application\" eventID=\"1011\"]",
				"[examplePriority@32473 class=\"high\"]");
		assertThat(payload.message()).isEqualTo("");
	}
}