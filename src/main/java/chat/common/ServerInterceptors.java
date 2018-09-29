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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import chat.server.Server;

/**
 * This class contains the interception of the calls to the actions to receive
 * messages in the server. The behaviour is controlled by the boolean constant
 * {@link #interceptionEnabled}. When set, the default method
 * {@link chat.common.ActionOfAServer#executeOrIntercept(Server, MsgContent)}
 * redirects the receipt of the message to the static method
 * {@link #intercept(Server, Optional) #intercept(Server,
 * Optional&lt;MsgContent&gt;)}. The latter method loops on all the
 * interceptors, which are stored on the static collection
 * {@link #INTERCEPTORS}. Each interceptor may intercept the message, that is
 * may delay its receipt.
 * 
 * @author Denis Conan
 */
public final class ServerInterceptors {

	/**
	 * states whether some non-determinism is introduced to test distributed
	 * algorithms. This is done by re-routing in the default method
	 * {@link chat.common.ActionOfAServer#executeOrIntercept(Server, MsgContent)}.
	 */
	private static boolean interceptionEnabled = false;

	/**
	 * the collection of collections of interceptors organised for servers.
	 */
	private static final HashMap<Server, List<InterceptorOfAServer<? extends MsgContent>>> INTERCEPTORS = new HashMap<>();

	/**
	 * Utility classes must not have a default or public constructor.
	 */
	private ServerInterceptors() {
	}

	/**
	 * gets the boolean value of the attribute {@link #interceptionEnabled}.
	 * 
	 * @return the boolean value.
	 */
	public static boolean isInterceptionEnabled() {
		return interceptionEnabled;
	}

	/**
	 * sets the boolean value of the attribute {@link #interceptionEnabled}.
	 * 
	 * @param interceptionEnabled
	 *            the new boolean value.
	 */
	public static void setInterceptionEnabled(final boolean interceptionEnabled) {
		ServerInterceptors.interceptionEnabled = interceptionEnabled;
	}

	/**
	 * adds an interceptor.
	 * 
	 * @param <C>
	 *            the type of the message to intercept.
	 * @param name
	 *            the name of the interceptor to add.
	 * @param server
	 *            the reference to the server.
	 * @param conditionForIntercepting
	 *            the function lambda that states whether the message must be
	 *            delayed.
	 * @param conditionForExecuting
	 *            the function method reference that states whether the treatment of
	 *            the message must be applied now.
	 * @param treatmentOfADelayedMsg
	 *            the consumer lambda that perform the treatment on delayed
	 *            messages.
	 */
	public static <C extends MsgContent> void addAnInterceptor(final String name, final Server server,
			final Function<C, Boolean> conditionForIntercepting, final Function<C, Boolean> conditionForExecuting,
			final Consumer<C> treatmentOfADelayedMsg) {
		Objects.requireNonNull(name, "argument name cannot be null");
		if ("".equals(name)) {
			throw new IllegalArgumentException("argument name cannot be empty string");
		}
		Objects.requireNonNull(server, "null server");
		Objects.requireNonNull(conditionForIntercepting, "argument conditionForDelaying cannot be null");
		Objects.requireNonNull(conditionForExecuting, "argument conditionForTreatment cannot be null");
		Objects.requireNonNull(treatmentOfADelayedMsg, "argument treatmentOfADelayedMsg");
		List<InterceptorOfAServer<? extends MsgContent>> serverInterceptors = INTERCEPTORS.getOrDefault(server,
				new ArrayList<>());
		serverInterceptors.add(new InterceptorOfAServer<C>(name, server, conditionForIntercepting,
				conditionForExecuting, treatmentOfADelayedMsg));
		INTERCEPTORS.put(server, serverInterceptors);
	}

	/**
	 * This method is called by the default method
	 * {@link chat.common.ActionOfAServer#executeOrIntercept(Server, MsgContent)}
	 * when the interception mechanism is activated, that is
	 * {@link #isInterceptionEnabled} is {@code true}. The method loops on the
	 * collection of interceptors and apply the method
	 * {@link InterceptorOfAServer#doIntercept(Optional)
	 * InterceptorOfAServer#doIntercept(Optional&lt;MsgContent&gt;)} of
	 * interceptors. The role of the latter method is to intercept the message, that
	 * is to delay the delivering/treatment of the message.
	 * 
	 * @param server
	 *            the reference to the server.
	 * @param msg
	 *            the message to treat.
	 * @return the message, if no intercepted.
	 */
	public static Optional<MsgContent> intercept(final Server server, final Optional<MsgContent> msg) {
		Objects.requireNonNull(server, "argument server cannot be null");
		List<InterceptorOfAServer<? extends MsgContent>> listOfInterceptors = INTERCEPTORS.getOrDefault(server,
				Collections.emptyList());
		for (InterceptorOfAServer<? extends MsgContent> interceptor : listOfInterceptors) {
			if (msg.isPresent() && !interceptor.doIntercept(msg).isPresent()) {
				return Optional.empty();
			}
		}
		return msg;
	}
}
