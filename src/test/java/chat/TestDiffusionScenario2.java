// CHECKSTYLE:OFF
package chat;

import static chat.common.Log.LOGGER_NAME_TEST;
import static chat.common.Log.LOG_ON;
import static chat.common.Log.TEST;

import java.util.function.Consumer;
import java.util.function.Function;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Test;

import chat.client.Client;
import chat.client.algorithms.chat.ChatMsgContent;
import chat.common.ClientInterceptors;
import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestDiffusionScenario2 extends Scenario {
	@Test
	@Override
	public void constructAndRun() throws Exception {
		Log.configureALogger(LOGGER_NAME_TEST, Level.TRACE);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the servers...");
		}
		Server s1 = instanciateAServer("0");
		sleep(500);
		Server s2 = instanciateAServer("1 localhost 0");
		sleep(500);
		Server s3 = instanciateAServer("2 localhost 0 localhost 1");
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
		ClientInterceptors.setInterceptionEnabled(true);
		Function<ChatMsgContent, Boolean> conditionForInterceptingI1OnC0 = msg -> msg.getSender() == 1;
		Function<ChatMsgContent, Boolean> conditionForExecutingI1OnC0 = msg -> c1.getV().getEntry(0) == 2; 
		Consumer<ChatMsgContent> treatmentI1OnC1 = msg -> chat.client.algorithms.chat.Action.CHAT_MESSAGE
				.execute(c0, new ChatMsgContent(msg.getSender(), msg.getSeqNumber(),
						msg.getContent() + ", intercepted at client c1 by i1", c0.getV()));
		ClientInterceptors.addAnInterceptor("i1", c1, conditionForInterceptingI1OnC0, conditionForExecutingI1OnC0,
				treatmentI1OnC1);
	    
		emulateAnInputLineFromTheConsoleForAClient(c0, "message 0 from c0");
		System.out.println(c1.getV().getEntry(0));
		
		emulateAnInputLineFromTheConsoleForAClient(c1, "message 1 from c0");
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the test of the algorithms...");
		}
		sleep(3000);
		System.out.flush();
		Assert.assertEquals(1, c2.getV().getEntry(c0.getIdentity()));
		Assert.assertEquals(1, c2.getV().getEntry(c1.getIdentity()));
		
		emulateAnInputLineFromTheConsoleForAClient(c0, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAClient(c2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s3, "quit");
		sleep(100);

} }

