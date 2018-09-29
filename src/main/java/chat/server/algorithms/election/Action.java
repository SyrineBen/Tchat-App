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
package chat.server.algorithms.election;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import chat.common.ActionOfAServer;
import chat.common.MsgContent;
import chat.server.Server;

/**
 * This Enumeration type declares the actions of the election algorithm of the
 * server. Only two types of message contents are used/can be received.
 * 
 * @author Denis Conan
 * 
 */
public enum Action implements ActionOfAServer {
	/**
	 * the enumerator for the action of the token message of the election algorithm.
	 */
	TOKEN_MESSAGE(ElectionTokenContent.class,
			(Server server, MsgContent content) -> {
				try {
					server.receiveTokenContent((ElectionTokenContent) content);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}),
	/**
	 * the enumerator for the action of the leader message of the election
	 * algorithm.
	 */
	LEADER_MESSAGE(ElectionLeaderContent.class,
			(Server server, MsgContent content) -> {
				try {
					server.receiveLeaderContent((ElectionLeaderContent) content);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

	/**
	 * collection of the actions of this algorithm enumerator of the server. The
	 * collection is built at class loading by adding all the enumerators, which are
	 * actions of the algorithms (subtypes of {@link chat.common.ActionOfAServer});
	 * it is thus {@code static}. The collection is unmodifiable and the attribute
	 * is {@code final} so that no other collection can be substituted after being
	 * statically assigned. Since it is immutable, the attribute can be
	 * {@code public}.
	 */
	public static final Map<Integer, ActionOfAServer> ACTIONS;
	/**
	 * index of the action of this message type.
	 */
	private final int actionIndex;

	/**
	 * the type of the content.
	 */
	private final Class<? extends MsgContent> contentClass;

	/**
	 * the lambda expression of the action.
	 */
	private final BiConsumer<Server, MsgContent> actionFunction;

	/**
	 * static block to build collections of actions.
	 */
	static {
		ACTIONS = Collections.unmodifiableMap(Arrays.asList(Action.values()).stream()
				.collect(Collectors.toMap(action -> action.identifier(), action -> action)));
	}

	/**
	 * is the constructor of message type object.
	 * 
	 * @param contentClass
	 *            the type of the content.
	 * @param actionFunction
	 *            the lambda expression of the action.
	 */
	Action(final Class<? extends MsgContent> contentClass, final BiConsumer<Server, MsgContent> actionFunction) {
		this.actionIndex = chat.common.ActionOfAServer.OFFSET_SERVER_ALGORITHMS
				+ chat.server.algorithms.Algorithm.OFFSET_ELECTION_ALGORITHM + ordinal();
		this.contentClass = contentClass;
		this.actionFunction = actionFunction;
	}

	/**
	 * obtains the index of this message type.
	 * 
	 * @return the identifier of the action as an {@code int}.
	 */
	public int identifier() {
		return actionIndex;
	}

	/**
	 * gets the type of the content.
	 * 
	 * @return the type of the content.
	 */
	public Class<? extends MsgContent> contentClass() {
		return contentClass;
	}

	/**
	 * gets the lambda expression of the action.
	 * 
	 * @return the lambda expression.
	 */
	public BiConsumer<Server, MsgContent> actionFunction() {
		return actionFunction;
	}

	@Override
	public String toString() {
		return String.valueOf(actionIndex);
	}
}
