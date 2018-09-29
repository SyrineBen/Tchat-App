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

import chat.server.Server;

/**
 * This interface defines the interface of the actions of the algorithms of the
 * server. An action has an identifier and is obtained with the method
 * {@link #identifier}. The identifiers are computed in the enumeration
 * {@link chat.server.algorithms.Algorithm}. The second method
 * ({@link #execute}) is called for executing the action. The context of the
 * call are the server and the message that has just been received.
 * 
 * @author Denis Conan
 * 
 */
public interface ActionOfAServer {
	/**
	 * gets the identifier (integer) of the action, which can be attached to a
	 * message dispatcher.
	 * 
	 * @return the identifier of the action.
	 */
	int identifier();

	/**
	 * index of the first message type of the first algorithm of the server.
	 */
	int OFFSET_SERVER_ALGORITHMS = 0;

	/**
	 * gets the type of the server.
	 * 
	 * @return the type of the server.
	 */
	Class<? extends MsgContent> contentClass();

	/**
	 * gets the lambda expression of the action to execute.
	 * 
	 * @return the lambda expression of the action.
	 */
	BiConsumer<Server, MsgContent> actionFunction();

	/**
	 * executes the algorithmic part corresponding to this action. The
	 * synchronisation is made into the server method that is going to be executed.
	 * 
	 * @param server
	 *            the reference to the server.
	 * @param msg
	 *            the message in treatment.
	 */
	default void execute(Server server, MsgContent msg) {
		Objects.requireNonNull(server, "argument client cannot be null");
		Objects.requireNonNull(msg, "argument content cannot be null");
		if (!contentClass().isInstance(msg)) {
			throw new IllegalArgumentException("msg of type " + msg.getClass().getCanonicalName()
					+ "() is not an instance of " + contentClass().getCanonicalName());
		}
		actionFunction().accept(server, msg);
	}

	/**
	 * executes the action due to the receipt of the message {@code msg} or
	 * intercepts the call of the action for instance to eventually re-schedule the
	 * receipt of the message so that some non-determinism is introduced. The
	 * behaviour is controlled by the boolean value
	 * {@link ServerInterceptors#isInterceptionEnabled()}. The synchronisation is
	 * made into the server method that is going to be executed.
	 * 
	 * @param server
	 *            the reference of the server.
	 * @param content
	 *            the message to treat.
	 */
	default void executeOrIntercept(final Server server, final MsgContent content) {
		Objects.requireNonNull(server, "argument server cannot be null");
		Objects.requireNonNull(content, "argument content cannot be null");
		Optional<MsgContent> msg = Optional.of(content);
		if (ServerInterceptors.isInterceptionEnabled()) {
			msg = ServerInterceptors.intercept(server, msg);
		}
		msg.ifPresent(m -> execute(server, m));
	}
}
