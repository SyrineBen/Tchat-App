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

import java.io.Serializable;

/**
 * This abstract class defines the the message contents used in the client or
 * the server.
 * 
 * @author Denis Conan
 */
public abstract class MsgContent implements Serializable {
	/**
	 * serialisation number.
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * the identity of the sender.
	 */
	private int sender;

	/**
	 * constructs a message.
	 * 
	 * @param sender
	 *            the identity of the sender.
	 */
	public MsgContent(final int sender) {
		if (sender < 0) {
			throw new IllegalArgumentException("invalid id for the sender(" + sender + ")");
		}
		this.sender = sender;
		assert invariantMsgContent();
	}

	/**
	 * checks the invariant of the class.
	 * 
	 * @return the boolean stating the invariant is maintained.
	 */
	public final boolean invariantMsgContent() {
		return sender >= 0;
	}

	/**
	 * gets the identity of the sender.
	 * 
	 * @return the identity of the sender.
	 */
	public int getSender() {
		return sender;
	}
}
