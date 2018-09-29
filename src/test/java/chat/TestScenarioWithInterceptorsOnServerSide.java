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

import chat.common.Log;
import chat.common.Scenario;
import chat.common.ServerInterceptors;
import chat.server.Server;
import chat.server.State;
import chat.server.algorithms.election.ElectionTokenContent;

public class TestScenarioWithInterceptorsOnServerSide extends Scenario {
	@Test
	@Override
	public void constructAndRun() throws Exception {
		Log.configureALogger(LOGGER_NAME_TEST, Level.TRACE);
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the servers...");
		}
		Server s1 = instanciateAServer("1");
		sleep(500);
		Server s2 = instanciateAServer("2 localhost 1");
		sleep(500);
		Server s5 = instanciateAServer("5 localhost 1 localhost 2");
		sleep(500);

		emulateAnInputLineFromTheConsoleForAServer(s1, "election");
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info(s1.getState());
		}
		emulateAnInputLineFromTheConsoleForAServer(s5, "election");
		ServerInterceptors.setInterceptionEnabled(true);
		Function<ElectionTokenContent, Boolean> conditionForInterceptingI1OnS2 = msg -> msg.getSender() == 1;
		Function<ElectionTokenContent, Boolean> conditionForExecutingI1OnS2 = msg -> true;
		Consumer<ElectionTokenContent> treatmentI1OnS2 = msg -> chat.server.algorithms.election.Action.TOKEN_MESSAGE
				.execute(s2, msg);
		ServerInterceptors.addAnInterceptor("i1", s2, conditionForInterceptingI1OnS2, conditionForExecutingI1OnS2,
				treatmentI1OnS2);

		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info("starting the test of the algorithms...");
		}
		sleep(3000);
		System.out.flush();
		Assert.assertEquals(State.leader, s1.getState());
		Assert.assertEquals(State.nonleader, s2.getState());
		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s5, "quit");
		sleep(100);
	}
}
