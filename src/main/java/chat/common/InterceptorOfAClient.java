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

import static chat.common.Log.INTERCEPT;
import static chat.common.Log.LOG_ON;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

import chat.client.Client;

/**
 * This class contains the interceptor for a client. The method
 * {@link #doIntercept(Optional) #doIntercept(Optional&lt;MsgContent&gt;)} is
 * called before the message receipt, and more precisely from the method
 * {@link ClientInterceptors#intercept(Client, Optional)
 * ClientInterceptors#intercept(Client, Optional&lt;MsgContent&gt;)}. A message
 * is intercepted when the lambda expression of method reference
 * {@link #conditionForIntercepting} returns {@code true}. If so, a
 * {@link TreatDelayedMessageToAClient} thread is created and started that will
 * later run the lambda expression of the method reference
 * {@link #treatmentOfADelayedMsg} if the method reference returns {@code true}.
 * 
 * @param <C>
 *            the type of the message to intercept.
 * 
 * @author Denis Conan
 */
public class InterceptorOfAClient<C extends MsgContent> {
	/**
	 * the name of the interceptor.
	 */
	private final String name;
	/**
	 * the reference to the client.
	 */
	private final Client client;
	/**
	 * the function method reference for deciding whether a message must be delayed.
	 * The synchronisation is made into the methods that call this function.
	 */
	private final Function<C, Boolean> conditionForIntercepting;
	/**
	 * the function method reference for deciding when the treatment of the message
	 * must be applied. The synchronisation is made into the methods that call this
	 * function.
	 */
	private final Function<C, Boolean> conditionForExecuting;
	/**
	 * the consumer method reference for the treatment of the delayed messages. The
	 * synchronisation is made into the methods that call this function.
	 */
	private final Consumer<C> treatmentOfADelayedMsg;

	/**
	 * constructs an interceptor for the given client.
	 * 
	 * @param name
	 *            the name of the interceptor.
	 * @param client
	 *            the reference to the client.
	 * @param conditionForIntercepting
	 *            the function method reference that states whether the message must
	 *            be delayed.
	 * @param conditionForExecuting
	 *            the function method reference that states whether the treatment of
	 *            the message must be applied now.
	 * @param treatmentOfADelayedMsg
	 *            the consumer lambda that perform the treatment on delayed
	 *            messages.
	 */
	public InterceptorOfAClient(final String name, final Client client,
			final Function<C, Boolean> conditionForIntercepting, final Function<C, Boolean> conditionForExecuting,
			final Consumer<C> treatmentOfADelayedMsg) {
		Objects.requireNonNull(name, "argument name cannot be null");
		if ("".equals(name)) {
			throw new IllegalArgumentException("argument name cannot be empty string");
		}
		Objects.requireNonNull(client, "argument client cannot be null");
		Objects.requireNonNull(conditionForIntercepting, "argument conditionForDelaying cannot be null");
		Objects.requireNonNull(conditionForExecuting, "argument conditionForTreatment cannot be null");
		Objects.requireNonNull(treatmentOfADelayedMsg, "argument treatmentOfADelayedMsg");
		this.name = name;
		this.client = client;
		this.conditionForIntercepting = conditionForIntercepting;
		this.conditionForExecuting = conditionForExecuting;
		this.treatmentOfADelayedMsg = treatmentOfADelayedMsg;
		assert invariant();
	}

	/**
	 * checks the invariant of the object.
	 * 
	 * @return true when satisfied.
	 */
	public boolean invariant() {
		return name != null && !"".equals(name) && client != null && conditionForIntercepting != null
				&& conditionForExecuting != null && treatmentOfADelayedMsg != null;
	}

	/**
	 * gets the name of the interceptor.
	 * 
	 * @return the name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * gets the reference to the client.
	 * 
	 * @return the reference to the client.
	 */
	public Client getClient() {
		return client;
	}

	/**
	 * applies the condition of the interceptor, and either create a thread to delay
	 * the delivering/treatment of the message (return an empty {@code Optional}) or
	 * return the same message.
	 * 
	 * @param msg
	 *            the message to intercept.
	 * @return the message, if no intercepted.
	 */
	@SuppressWarnings("unchecked") // due to (unsafe) downcast in Function.apply
	public Optional<C> doIntercept(final Optional<MsgContent> msg) {
		Objects.requireNonNull(msg, "argument content cannot be null");
		synchronized (client) {
			// TODO avoid testing using exception
			try {
				if (msg.isPresent() && conditionForIntercepting.apply((C) msg.get())) {
					new Thread(new TreatDelayedMessageToAClient<C>(this, client, (C) msg.get())).start();
					if (LOG_ON && INTERCEPT.isInfoEnabled()) {
						INTERCEPT.info("interceptor " + name + " at client " + client.getIdentity()
								+ " intercepts message: " + msg.get());
					}
					return Optional.empty();
				} else {
					return Optional.ofNullable((C) msg.orElse(null));
				}
			} catch (Exception e) {
				return Optional.ofNullable((C) msg.orElse(null));
			}
		}
	}

	/**
	 * launches the treatment of the delayed message. This method is a delegate from
	 * method {@link TreatDelayedMessageToAClient#run()}.
	 * 
	 * @param content
	 *            the content of the delayed message.
	 * @return {@code true} when the treatment has been applied and the calling
	 *         thread can end its execution.
	 */
	public boolean doTreatDelayedMessage(final C content) {
		Objects.requireNonNull(content, "null content");
		synchronized (client) {
			if (this.conditionForExecuting.apply((C) content)) {
				try {
					treatmentOfADelayedMsg.accept((C) content);
					if (LOG_ON && INTERCEPT.isInfoEnabled()) {
						INTERCEPT.info("treatment by client interceptor " + name + ": " + content);
					}
				} catch (ClassCastException e) {
					if (LOG_ON) {
						INTERCEPT.warn(
								"class cast exception when executing by client interceptor " + name + ": " + content);
					}
				}
				return true;
			} else {
				if (LOG_ON && INTERCEPT.isDebugEnabled()) {
					INTERCEPT.debug("bad condition for executing by client interceptor " + name + ": " + content);
				}
				return false;
			}
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((client == null) ? 0 : client.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		InterceptorOfAClient<C> other = (InterceptorOfAClient<C>) obj;
		if (client == null) {
			if (other.client != null) {
				return false;
			}
		} else if (!client.equals(other.client)) {
			return false;
		}
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "InterceptorOfAClient [name=" + name + ", server=" + client.getIdentity() + "]";
	}
}
