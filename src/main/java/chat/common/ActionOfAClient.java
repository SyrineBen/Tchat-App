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

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import chat.client.Client;

/**
 * This interface defines the interface of the actions of the algorithms of the
 * client. An action has an identifier and is obtained with the method
 * {@link #identifier}. The identifiers are computed in the enumeration
 * {@link chat.client.algorithms.Algorithm}. The second method
 * ({@link #execute}) is called for executing the action. The context of the
 * call are the client and the message that has just been received.
 * 
 * @author Denis Conan
 * 
 */
public interface ActionOfAClient {
	/**
	 * gets the identifier (integer) of the action, which can be attached to a
	 * message dispatcher.
	 * 
	 * @return the identifier of the action.
	 */
	int identifier();

	/**
	 * index of the first message type of the first algorithm of the client.
	 */
	int OFFSET_CLIENT_ALGORITHMS = 1000;

	/**
	 * gets the type of the content/message to be treated.
	 * 
	 * @return the type of the content/message.
	 */
	Class<? extends MsgContent> contentClass();

	/**
	 * gets the lambda expression of the action to execute.
	 * 
	 * @return the lambda expression of the action.
	 */
	BiConsumer<Client, MsgContent> actionFunction();

	/**
	 * executes the algorithmic part corresponding to this action. The
	 * synchronisation is made into the client method that is going to be executed.
	 * 
	 * @param client
	 *            the reference to the client.
	 * @param msg
	 *            the message in treatment.
	 */
	default void execute(Client client, MsgContent msg) {
		Objects.requireNonNull(client, "argument client cannot be null");
		Objects.requireNonNull(msg, "argument content cannot be null");
		if (!contentClass().isInstance(msg)) {
			throw new IllegalArgumentException("msg of type " + msg.getClass().getCanonicalName()
					+ "() is not an instance of " + contentClass().getCanonicalName());
		}
		// synchronisation made into the client method to be executed
		actionFunction().accept(client, msg);
	}

	/**
	 * executes the action due to the receipt of the message {@code msg} or
	 * intercepts the call of the action for instance to eventually re-schedule the
	 * receipt of the message so that some non-determinism is introduced. The
	 * behaviour is controlled by the boolean value
	 * {@link ClientInterceptors#isInterceptionEnabled()}. The synchronisation is
	 * made into the client method that is going to be executed.
	 * 
	 * @param client
	 *            the reference of the client.
	 * @param content
	 *            the message to treat.
	 */
	default void executeOrIntercept(final Client client, final MsgContent content) {
		Objects.requireNonNull(client, "argument client cannot be null");
		Objects.requireNonNull(content, "argument content cannot be null");
		Optional<MsgContent> msg = Optional.of(content);
		if (ClientInterceptors.isInterceptionEnabled()) {
			msg = ClientInterceptors.intercept(client, msg);
		}
		// synchronisation made into the client method going to be executed
		msg.ifPresent(m -> execute(client, m));
	}
}
