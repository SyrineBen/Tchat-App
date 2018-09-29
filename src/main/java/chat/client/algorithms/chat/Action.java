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
package chat.client.algorithms.chat;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

import chat.client.Client;
import chat.common.ActionOfAClient;
import chat.common.MsgContent;

/**
 * This Enumeration type declares the actions of the chat algorithm of the
 * client. Only one message content type can be received.
 * 
 * @author Denis Conan
 * 
 */
public enum Action implements ActionOfAClient {
	/**
	 * the enumerator for the action of the chat message of the chat algorithm. The
	 * synchronisation is made into the client method that is going to be called.
	 * 
	 * NB: I do not know whether a re-factoring can avoid the cast.
	 */
	CHAT_MESSAGE(ChatMsgContent.class,
			(Client client, MsgContent content) -> client.receiveChatMsgContent((ChatMsgContent) content));

	/**
	 * collection of the actions of this algorithm enumerator of the client. The
	 * collection is built at class loading by adding all the enumerators, which are
	 * actions of the algorithms (sub-types of {@link chat.common.ActionOfAClient});
	 * it is thus {@code static}. The collection is unmodifiable and the attribute
	 * is {@code final} so that no other collection can be substituted after being
	 * statically assigned. Since it is immutable, the attribute can be
	 * {@code public}.
	 */
	public static final Map<Integer, ActionOfAClient> ACTIONS;

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
	private final BiConsumer<Client, MsgContent> actionFunction;

	/**
	 * static block to build the collection attributes when loading the enumeration
	 * in the VM. A modifiable list is built and transformed into an unmodifiable
	 * one.
	 */
	static {
		ACTIONS = Collections.unmodifiableMap(Arrays.asList(Action.values()).stream()
				.collect(Collectors.toMap(action -> action.identifier(), action -> action)));
	}

	/**
	 * constructs an enumerator by assigning the {@link #actionIndex}.
	 * 
	 * @param contentClass
	 *            the type of the content.
	 * @param actionFunction
	 *            the lambda expression of the expression. In general, this is a
	 *            call similar to
	 *            {@code client.aMethod((cast to contentClass) message)}.
	 */
	Action(final Class<? extends MsgContent> contentClass, final BiConsumer<Client, MsgContent> actionFunction) {
		Objects.requireNonNull(contentClass, "argument contentClass cannot be null");
		actionIndex = chat.common.ActionOfAClient.OFFSET_CLIENT_ALGORITHMS
				+ chat.client.algorithms.Algorithm.OFFSET_CHAT_ALGORITHM + ordinal();
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
	public BiConsumer<Client, MsgContent> actionFunction() {
		return actionFunction;
	}

	@Override
	public String toString() {
		return String.valueOf(actionIndex);
	}
}
