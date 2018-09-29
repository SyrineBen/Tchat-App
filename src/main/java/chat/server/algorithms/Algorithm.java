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
package chat.server.algorithms;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import chat.common.ActionOfAServer;
import chat.server.Server;

/**
 * This Enumeration type declares the algorithms of the server.There
 * is two algorithms: the algorithm for the election and the algorithm for
 * the mutex.
 * 
 * 
 * @author Denis Conan
 * @author BEN FATMA Manel
 * @author BEN SLIMENE Sirine
 */
public enum Algorithm {
	/**
	 * the election algorithm.
	 */
	ALGORITHM_ELECTION(chat.server.algorithms.election.Action.ACTIONS),
	/**
	 * the mutex algorithm.
	 */
	ALGORITHM_MUTEX(chat.server.algorithms.mutex.Action.ACTIONS);

	/**
	 * collection of the actions of this algorithm enumerator of the server. The
	 * collection is built at class loading by parsing the collections of actions of
	 * the algorithms; it is thus {@code static}. The collection is unmodifiable and
	 * the attribute is {@code final} so that no other collection can be substituted
	 * after being statically assigned.
	 */
	private final Map<Integer, ? extends ActionOfAServer> mapOfActions;

	/**
	 * index of the first message type of the election algorithm.
	 */
	public static final int OFFSET_ELECTION_ALGORITHM = 0;
	/**
	 * index of the first message type of the mutex algorithm.
	 */
	public static final int OFFSET_MUTEX_ALGORITHM = 10;

	/**
	 * is the constructor of this algorithm object.
	 * 
	 * @param mapOfActions
	 *            collection of actions of this algorithm.
	 */
	Algorithm(final Map<Integer, ? extends ActionOfAServer> mapOfActions) {
		this.mapOfActions = mapOfActions;
	}

	/**
	 * obtains the map of actions of the algorithm.
	 * 
	 * @return the map of actions.
	 */
	private Map<Integer, ? extends ActionOfAServer> getMapOfActions() {
		return mapOfActions;
	}

	/**
	 * searches for the action to execute in the collection of actions of the
	 * algorithm of the server. The synchronisation is made into the server method
	 * that is going to be executed.
	 * 
	 * @param server
	 *            the reference to the server.
	 * @param actionIndex
	 *            index of the action to execute.
	 * @param content
	 *            content of the message just received.
	 */
	public static void execute(final Server server, final int actionIndex, final Object content) {
		Arrays.asList(values()).stream().map(Algorithm::getMapOfActions).map(actions -> actions.get(actionIndex))
				.filter(Objects::nonNull).filter(action -> action.contentClass().isInstance(content))
				.forEach(action -> action.executeOrIntercept(
						Optional.ofNullable(server).orElseThrow(IllegalArgumentException::new),
						action.contentClass().cast(content)));
	}
}
