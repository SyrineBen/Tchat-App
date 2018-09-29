// CHECKSTYLE:OFF
package chat;

import static chat.common.Log.LOGGER_NAME_CHAT;
import static chat.common.Log.LOGGER_NAME_COMM;
import static chat.common.Log.LOGGER_NAME_ELECTION;
import static chat.common.Log.LOGGER_NAME_GEN;
import static chat.common.Log.LOGGER_NAME_TEST;
import static chat.common.Log.LOG_ON;
import static chat.common.Log.TEST;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import chat.client.Client;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestScenarioOssatureDeDepart extends Scenario {

	@Test
	@Override
	public void constructAndRun() throws Exception {
		Log.configureALogger(LOGGER_NAME_CHAT, Level.WARN);
		Log.configureALogger(LOGGER_NAME_COMM, Level.WARN);
		Log.configureALogger(LOGGER_NAME_ELECTION, Level.WARN);
		Log.configureALogger(LOGGER_NAME_GEN, Level.WARN);
		Log.configureALogger(LOGGER_NAME_TEST, Level.WARN);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the servers...");
		}
		Server s0 = instanciateAServer("0");
		sleep(500);
		Server s1 = instanciateAServer("1 localhost 0");
		sleep(500);
		Server s2 = instanciateAServer("2 localhost 0 localhost 1");
		sleep(500);
		Server s3 = instanciateAServer("3 localhost 2");
		sleep(500);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the clients...");
		}
		// start the clients
		Client c0 = instanciateAClient(2050);
		sleep(500);
		Client c1 = instanciateAClient(2050);
		sleep(500);
		Client c2 = instanciateAClient(2052);
		sleep(500);
		Client c3 = instanciateAClient(2053);
		sleep(500);
		Client c4 = instanciateAClient(2053);
		sleep(1000);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the test of the algorithms...");
		}
		emulateAnInputLineFromTheConsoleForAClient(c0, "message 0 from c0");
		emulateAnInputLineFromTheConsoleForAClient(c1, "message 1 from c1");
		emulateAnInputLineFromTheConsoleForAClient(c2, "message 2 from c2");
		emulateAnInputLineFromTheConsoleForAClient(c3, "message 3 from c3");
		emulateAnInputLineFromTheConsoleForAClient(c4, "message 4 from c4");
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("end of the scenario.");
		}
		// wait and then flush stdout (necessary for IDEs such as Eclipse)
		// without flush, no output in Eclipse for instance
		// without sleep, not all outputs
		sleep(3000);
		System.out.flush();
		// the scenario has ended, then get state content without synchronized
		Assert.assertEquals(1, c0.getNbChatMsgContentSent());
		Assert.assertEquals(1, c1.getNbChatMsgContentSent());
		Assert.assertEquals(1, c2.getNbChatMsgContentSent());
		Assert.assertEquals(1, c3.getNbChatMsgContentSent());
		Assert.assertEquals(1, c4.getNbChatMsgContentSent());
		Assert.assertEquals(4, c0.getNbChatMsgContentReceived());
		Assert.assertEquals(4, c1.getNbChatMsgContentReceived());
		Assert.assertEquals(4, c2.getNbChatMsgContentReceived());
		Assert.assertEquals(4, c3.getNbChatMsgContentReceived());
		Assert.assertEquals(4, c4.getNbChatMsgContentReceived());
		// finish properly
		emulateAnInputLineFromTheConsoleForAClient(c0, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c3, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c4, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s0, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s3, "quit");
		sleep(100);
	}
}
