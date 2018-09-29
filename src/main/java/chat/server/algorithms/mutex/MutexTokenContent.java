
package chat.server.algorithms.mutex;

import chat.common.MsgContent;
import chat.common.RequestVector;

/**
 * This class defines the content of a token message of the election algorithm.
 * 
 * 
 * @author Denis Conan
 * @author Manel Ben Fatma
 * @author Sirine Ben Slimene
 *
 */
public class MutexTokenContent extends MsgContent {
	@Override
	public String toString() {
		return "MuteTokenContent [sender=" + this.getSender() + "]";
	}

	/**
	 * version number for serialisation.
	 */
	
	private static final long serialVersionUID = 1L;
    /**
     * token.
     */
	private RequestVector jeton;

	/**
	 * constructs the content of a leader election message.
	 * 
	 * @param sender
	 *            the identity of the sender.
	 * @param jeton
	 *        token.
	 * 
	 */
	public MutexTokenContent(final int sender, final RequestVector jeton) {
		super(sender);
		this.jeton = jeton;
	}

	/**
	 * return jeton.
	 * @return jeton
	 *       jeton.
	 */
	public RequestVector getJeton() {
		return jeton;
	}


	
}
