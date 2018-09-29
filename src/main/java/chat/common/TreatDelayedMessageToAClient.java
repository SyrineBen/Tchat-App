/**
This file is part of the CSC4509 teaching unit.

Copyright (C) 2012-2018 Télécom SudParis

This is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This software platform is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with the CSC4509 teaching unit. If not, see <http://www.gnu.org/licenses/>.

Initial developer(s): Denis Conan
Contributor(s):
 */
package chat.common;

import static chat.common.Log.LOG_ON;
import static chat.common.Log.GEN;
import static chat.common.Log.TEST;

import java.util.Objects;

import chat.client.Client;

/**
 * This class is a runnable for the treatment of a message that has been delayed
 * by a client interceptor.
 * 
 * @author Denis Conan
 *
 * @param <C>
 *            the type of the content of the delayed message.
 */
public class TreatDelayedMessageToAClient<C extends MsgContent> implements Runnable {
	/**
	 * the reference to the interceptor in which the treatment must be searched for.
	 */
	private final InterceptorOfAClient<C> interceptor;
	/**
	 * the entity that has to receive this delayed message.
	 */
	private final Client client;
	/**
	 * the content of the message.
	 */
	private final C content;
	/**
	 * the delay.
	 */
	private static final long DELAY = 100;

	/**
	 * the constructor.
	 * 
	 * @param interceptor
	 *            the reference to the interceptor.
	 * @param client
	 *            the reference to the client.
	 * @param content
	 *            the content of the delayed message.
	 */
	public TreatDelayedMessageToAClient(final InterceptorOfAClient<C> interceptor, final Client client,
			final C content) {
		Objects.requireNonNull(interceptor, "argument interceptor cannot be null");
		Objects.requireNonNull(client, "argument client cannot be null");
		Objects.requireNonNull(content, "argument content cannot be null");
		this.interceptor = interceptor;
		this.client = client;
		this.content = content;
	}

	@Override
	public void run() {
		boolean quit = false;
		do {
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {
				GEN.error(e.getLocalizedMessage());
			    // Restore interrupted state...
			    Thread.currentThread().interrupt();
				return;
			}
			synchronized (client) {
				if (LOG_ON && TEST.isTraceEnabled()) {
					TEST.trace("delayed message: " + content);
				}
				quit = interceptor.doTreatDelayedMessage(content);
			}
		} while (!quit);
	}
}
