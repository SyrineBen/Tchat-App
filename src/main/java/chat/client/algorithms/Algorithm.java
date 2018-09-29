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
package chat.client.algorithms;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import chat.client.Client;
import chat.common.ActionOfAClient;

/**
 * This Enumeration type declares the algorithms of the chat client. For now,
 * there is only one algorithm: the algorithm for exchanging chat messages.
 * 
 * TODO add new algorithms when necessary, update the description of the
 * enumeration, and remove this comment.
 * 
 * @author Denis Conan
 */
public enum Algorithm {
	/**
	 * the chat algorithm.
	 */
	ALGORITHM_CHAT(chat.client.algorithms.chat.Action.ACTIONS);
	/**
	 * collection of the actions of this algorithm enumerator of the client. The
	 * collection is built at class loading by parsing the collections of actions of
	 * the algorithms; it is thus {@code static}. The collection is unmodifiable and
	 * the attribute is {@code final} so that no other collection can be substituted
	 * after being statically assigned.
	 */
	private final Map<Integer, ActionOfAClient> mapOfActions;

	/**
	 * index of the first message type of the chat algorithm.
	 */
	public static final int OFFSET_CHAT_ALGORITHM = 0;

	/**
	 * constructs an enumerator by assigning the map of actions of this algorithm to
	 * the algorithm enumerator. See the enumerations for the algorithm: e.g.
	 * {@link chat.client.algorithms.chat.Action}.
	 * 
	 * @param mapOfActions
	 *            collection of actions of this algorithm.
	 */
	Algorithm(final Map<Integer, ActionOfAClient> mapOfActions) {
		this.mapOfActions = mapOfActions;
	}

	/**
	 * obtains the map of actions of the algorithm.
	 * 
	 * @return the map of actions.
	 */
	private Map<Integer, ? extends ActionOfAClient> getMapOfActions() {
		return mapOfActions;
	}

	/**
	 * searches for the action to execute in the collection of algorithms of the
	 * algorithm of the client, each algorithm having a collection of actions. The
	 * synchronisation is made into the client method that is going to be executed.
	 * 
	 * @param client
	 *            the reference to the client.
	 * @param actionIndex
	 *            index of the action to execute.
	 * @param content
	 *            content of the message just received.
	 */
	public static void execute(final Client client, final int actionIndex, final Object content) {
		Arrays.asList(values()).stream().map(Algorithm::getMapOfActions).map(actions -> actions.get(actionIndex))
				.filter(Objects::nonNull).filter(action -> action.contentClass().isInstance(content))
				.forEach(action -> action.executeOrIntercept(
						Optional.ofNullable(client).orElseThrow(IllegalArgumentException::new),
						action.contentClass().cast(content)));
	}
}
