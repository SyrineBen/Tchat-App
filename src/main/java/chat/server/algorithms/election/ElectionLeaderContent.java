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

import chat.common.MsgContent;

/**
 * This class defines the content of a leader message of the election algorithm.
 * 

 * 
 * @author Denis Conan
 * @author Manel Ben Fatma
 * @author Sirine Ben Slimene
 *
 */
public class ElectionLeaderContent extends MsgContent {
	/**
	 * version number for serialisation.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * content initiator.
	 */
	private int initiator;

	/**
	 * constructs the content of a leader election message.
	 * 
	 * @param sender
	 *            the identity of the sender.
	 * @param initiator
	 *            the identity of the initiator.
	 */
	public ElectionLeaderContent(final int sender, final int initiator) {
		super(sender);
		this.initiator = initiator;
	}
    /**
     * return initiator.
     * @return initiator
     *       the identity of the initiator.  
     */
	public int getInitiator() {
		return initiator;
	}
    /**
     * set initiator.
     * @param initiator
     *       the identity of the initiator.  
     */
	public void setInitiator(final int initiator) {
		this.initiator = initiator;
	}

	@Override
	public String toString() {
		return "ElectionLeaderContent [initiator=" + initiator + "]";
	}
	
	
}
