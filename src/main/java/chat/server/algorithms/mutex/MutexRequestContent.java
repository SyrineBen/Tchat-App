
package chat.server.algorithms.mutex;

import chat.common.MsgContent;

/**
 * This class defines the content of a token message of the election algorithm.
 * 
 * 
 * @author Denis Conan
 * @author Manel Ben Fatma
 * @author Sirine Ben Slimene
 *
 */
public class MutexRequestContent extends MsgContent {
	@Override
	public String toString() {
		return "MutexRequestContent [sender=" + this.getSender() + "]";
	}

	/**
	 * version number for serialisation.
	 */
	
	private static final long serialVersionUID = 1L;
	/**
	 * horloge locale.
	 */
	private int ns;

	/**
	 * constructs the content of a leader election message.
	 * 
	 * @param sender
	 *            the identity of the sender.
	 * @param ns
	 *            horloge locale.
	 */
	public MutexRequestContent(final int sender, final int ns) {
		super(sender);
		this.ns = ns;
	}
    /**
     * return horloge locale.
     * @return ns
     */
	public int getNs() {
		return ns;
	}
    /**
     * set the local clock.
     * @param ns
     *      horloge locale.
     */
	public void setNs(final int ns) {
		this.ns = ns;
	}

	
}
