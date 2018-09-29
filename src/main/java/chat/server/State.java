package chat.server;

/**
 * This Enumeration type declares the different States of a server.
 * 
 * @author BEN SLIMENE Sirine
 * @author BEN FATMA Manel
 */

public enum State {
	/**
	 * éta initial du serveur.
	 */
	sleeping, 
	/**
	 * server is a candidate.
	 */
	candidate,
	/**
	 * server is a leader.
	 */
	leader,
	/**
	 * server is nonleader.
	 */
	nonleader,
	/**
	 * server is the initiator of election.
	 */
	initiator
}
