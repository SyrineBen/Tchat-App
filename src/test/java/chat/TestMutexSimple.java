//CHECKSTYLE:OFF
package chat;

import static chat.common.Log.LOGGER_NAME_TEST;
import static chat.common.Log.LOG_ON;
import static chat.common.Log.TEST;

import org.apache.log4j.Level;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;


import chat.common.Log;
import chat.common.Scenario;
import chat.server.Server;

public class TestMutexSimple extends Scenario {
	@Ignore
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
		Server s3 = instanciateAServer("3 localhost 1 localhost 2");
		sleep(500);
		
		emulateAnInputLineFromTheConsoleForAServer(s1, "election");
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info(s1.getState());
		}
		sleep(3000);
		System.out.flush();
		Assert.assertEquals(true, s1.getCritical());
		emulateAnInputLineFromTheConsoleForAServer(s2, "mutex");
		if (LOG_ON && TEST.isInfoEnabled()) {
			TEST.info(s2.getState());
		}
		
		sleep(3000);
		System.out.flush();
		Assert.assertEquals(false, s1.getCritical());
		Assert.assertEquals(true, s2.getCritical());
		
		emulateAnInputLineFromTheConsoleForAServer(s1, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s2, "quit");
		sleep(100);
		emulateAnInputLineFromTheConsoleForAServer(s3, "quit");
		sleep(100);
	}

}
