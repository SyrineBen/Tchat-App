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
package chat.client;


import static chat.common.Log.COMM;
import static chat.common.Log.GEN;
import static chat.common.Log.LOGGER_NAME_CHAT;
import static chat.common.Log.LOGGER_NAME_COMM;
import static chat.common.Log.LOGGER_NAME_ELECTION;
import static chat.common.Log.LOGGER_NAME_GEN;
import static chat.common.Log.LOGGER_NAME_TEST;
import static chat.common.Log.LOG_ON;
import static chat.common.Log.LOGGER_NAME_DIFFUSION;
import static chat.common.Log.DIFFUSION;
import  chat.common.VectorClock;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;

import org.apache.log4j.Level;

import chat.client.algorithms.chat.Action;
import chat.client.algorithms.chat.ChatMsgContent;
import chat.common.Log;

/**
 * This class contains the logic of a client of the chat application. It
 * configures the client, connects to a chat server, launches a thread for
 * reading chat messages from the chat server.
 * 
 * This class is provided as the starting point to implement distributed
 * algorithms and must be extended when new algorithms are implemented
 * 
 * @author Denis Conan
 * 
 */
public class Client {
	/**
	 * the runnable object of the client that receives the messages from the chat
	 * server.
	 */
	private final ReadMessagesFromNetwork runnableToRcvMsgs;
	/**
	 * the thread of the client that receives the messages from the chat server.
	 */
	private final Thread threadToRcvMsgs;
	// The following attributes are shared through getters and setters, and must be
	// accessed into synchronized blocks. We apply the idiom Self Encapsulate Field,
	// that is we use methods to manipulate these attributes, in order to make
	// explicit their manipulation (into synchronized blocks).
	/**
	 * identity of this client. The identity is computed by the server as follows:
	 * {@code identity * OFFSET_ID_CLIENT + clientNumber}. This identity is shared
	 * and must be accessed into {@code synchronized} blocks. <br>
	 * Be careful: use methods to manipulate this attribute (idiom Self Encapsulate
	 * Field).
	 */
	private int identity;
	/**
	 * number of chat messages received. This identity is shared and must be
	 * accessed into {@code synchronized} blocks. <br>
	 * Be careful: use methods to manipulate this attribute (idiom Self Encapsulate
	 * Field).
	 */
	private int nbChatMsgContentReceived;
	/**
	 * number of chat messages sent. This identity is shared and must be accessed
	 * into {@code synchronized} blocks. <br>
	 * Be careful: use methods to manipulate this attribute (idiom Self Encapsulate
	 * Field).
	 */
	private int nbChatMsgContentSent;
	
	/**
	 * bag of message not already C-delivred.
	 */
	private Map<ChatMsgContent, VectorClock> msgBag;
	
	/**
	 * vector clock.
	 */
	private VectorClock v;
	
	{
		msgBag = new HashMap<>();
		v = new VectorClock();
	}

	/**
	 * constructs a client with a connection to the chat server. The connection to
	 * the server is managed in a thread that is also a full message worker. Before
	 * creating the threaded full duplex message worker, the constructor check for
	 * the server host name and open a connection with the chat server.
	 * 
	 * NB: after the construction of a client object, the thread for reading
	 * messages must be started using the method
	 * {@link #startThreadReadMessagesFromNetwork}.
	 * 
	 * @param serverHostName
	 *            the name of the host of the server.
	 * @param serverPortNb
	 *            the port number of the accepting socket of the server.
	 */
	public Client(final String serverHostName, final int serverPortNb) {
		Log.configureALogger(LOGGER_NAME_CHAT, Level.WARN);
		Log.configureALogger(LOGGER_NAME_COMM, Level.WARN);
		Log.configureALogger(LOGGER_NAME_ELECTION, Level.WARN);
		Log.configureALogger(LOGGER_NAME_GEN, Level.WARN);
		Log.configureALogger(LOGGER_NAME_TEST, Level.WARN);
		Log.configureALogger(LOGGER_NAME_CHAT, Level.WARN);
		Log.configureALogger(LOGGER_NAME_COMM, Level.WARN);
		Log.configureALogger(LOGGER_NAME_ELECTION, Level.INFO);
		Log.configureALogger(LOGGER_NAME_GEN, Level.WARN);
		Log.configureALogger(LOGGER_NAME_TEST, Level.WARN);
		Log.configureALogger(LOGGER_NAME_DIFFUSION, Level.TRACE);
		SocketChannel rwChan;
		InetAddress destAddr;
		try {
			destAddr = InetAddress.getByName(serverHostName);
		} catch (UnknownHostException e) {
			throw new IllegalStateException("unknown host name provided");
		}
		try {
			rwChan = SocketChannel.open();
		} catch (IOException e) {
			throw new IllegalStateException("cannot open a connection to the server");
		}
		try {
			rwChan.connect(new InetSocketAddress(destAddr, serverPortNb));
		} catch (IOException e) {
			throw new IllegalStateException("cannot open a connection to the server");
		}
		runnableToRcvMsgs = new ReadMessagesFromNetwork(rwChan, this);
		threadToRcvMsgs = new Thread(runnableToRcvMsgs);
		assert invariant();
	}

	/**
	 * checks the invariant of the class.
	 * 
	 * NB: the method is final so that the method is not overridden in potential
	 * subclasses because it is called in the constructor.
	 * 
	 * @return a boolean stating whether the invariant is maintained.
	 */
	public final synchronized boolean invariant() {
		return runnableToRcvMsgs != null && threadToRcvMsgs != null && nbChatMsgContentReceived >= 0
				&& nbChatMsgContentSent >= 0;
	}

	/**
	 * gets the identity of the client. This method that manipulates a shared
	 * attribute must be accessed into {@code synchronized} blocks.
	 * 
	 * @return the identity.
	 */
	public synchronized int getIdentity() {
		return identity;
	}

	/**
	 * sets the identity of the client. This method that manipulates a shared
	 * attribute must be accessed into {@code synchronized} blocks. The invariant is
	 * not asserted at the end of the method since the method is called in the
	 * constructor of the class {@link ReadMessagesFromNetwork} in the assignment of
	 * the attribute {@link #runnableToRcvMsgs}.
	 * 
	 * @param identity
	 *            the new identity.
	 */
	protected synchronized void setIdentity(final int identity) {
		this.identity = identity;
	}

	/**
	 * gets the number of chat messages received. This method that manipulates a
	 * shared attribute must be accessed into {@code synchronized} blocks.
	 * 
	 * @return the number.
	 */
	public synchronized int getNbChatMsgContentReceived() {
		return nbChatMsgContentReceived;
	}

	/**
	 * increments the number of chat messages received. This method that manipulates
	 * a shared attribute must be accessed into {@code synchronized} blocks.
	 */
	public synchronized void incrementNbChatMsgContentReceived() {
		nbChatMsgContentReceived++;
		assert invariant();
	}

	/**
	 * gets the number of chat messages sent. This method that manipulates a shared
	 * attribute must be accessed into {@code synchronized} blocks.
	 * 
	 * @return the number.
	 */
	public synchronized int getNbChatMsgContentSent() {
		return nbChatMsgContentSent;
	}

	/**
	 * gets the vector clock.
	 * @return vector clock.
	 */
	public VectorClock getV() {
		return v;
	}



	/**
	 * increments the number of chat messages sent. This method that manipulates a
	 * shared attribute must be accessed into {@code synchronized} blocks.
	 */
	public synchronized void incrementNbChatMsgContentSent() {
		nbChatMsgContentSent++;
		assert invariant();
	}

	/**
	 * starts the thread that is responsible for reading messages from the server.
	 */
	public synchronized void startThreadReadMessagesFromNetwork() {
		threadToRcvMsgs.start();
		assert invariant();
	}

	/**
	 * treats an input line from the console. For now, it sends the input line as a
	 * chat message to the server.
	 * 
	 * @param line
	 *            the content of the message
	 * @throws IOException
	 *             the exception thrown when the sending cannot be completed.
	 */
	public void treatConsoleInput(final String line) throws IOException {
		Objects.requireNonNull(line, "argument line cannot be null");
		if (LOG_ON && GEN.isDebugEnabled()) {
			GEN.debug("new command line on console");
		}
		if (line.equals("quit")) {
			threadToRcvMsgs.interrupt();
			Thread.currentThread().interrupt();
		} else {
			synchronized (this) {
				ChatMsgContent msg = new ChatMsgContent(getIdentity(), getNbChatMsgContentSent(), line, v);
				if (LOG_ON && DIFFUSION.isInfoEnabled()) {
					DIFFUSION.info(Log.computeClientLogMessage(this, ", sending chat message: " + msg));
				}
				System.out.println(msg);
				long sent = runnableToRcvMsgs.sendMsg(Action.CHAT_MESSAGE.identifier(), getIdentity(),
						getNbChatMsgContentSent(), msg);
				incrementNbChatMsgContentSent();
				if (LOG_ON && COMM.isDebugEnabled()) {
					COMM.debug(sent + " bytes sent.");	
				}
				v.incrementEntry(getIdentity());
			}
		}
		assert invariant();
	}

	/**
	 * treats the reception of a chat message: the message is displayed in the
	 * console.
	 * 
	 * @param content
	 *            the content of the message.
	 */
	public void receiveChatMsgContent(final ChatMsgContent content) {
		Objects.requireNonNull(content, "argument content cannot be null");
		synchronized (this) {
			if (LOG_ON && DIFFUSION.isInfoEnabled()) {
				DIFFUSION.info(Log.computeClientLogMessage(this, ", received: " + content));
			}
			int q = content.getSender();
			if (q != this.getIdentity()) {
				msgBag.put(content, content.getVectorClock());
				if (LOG_ON && DIFFUSION.isInfoEnabled()) {
					DIFFUSION.info(Log.computeClientLogMessage(this, "msgBag size =" + msgBag.size()));
				}

				HashMap<ChatMsgContent, VectorClock> toremove = new HashMap<>();
				Boolean flag = true;
				while (flag) {
					flag = false;
					Iterator<Entry<ChatMsgContent, VectorClock>> it = msgBag.entrySet().iterator();
					while (it.hasNext()) {
						Entry<ChatMsgContent, VectorClock> iter = it.next();
						VectorClock vector = iter.getValue();
						if (this.v.isGreaterOrEquals(vector)) {
							System.out.println(iter.getKey());
							this.v.incrementEntry(iter.getKey().getSender());
							flag = true;
							toremove.put(iter.getKey(), iter.getValue());
						}	
					}
					Iterator<Entry<ChatMsgContent, VectorClock>> itr = msgBag.entrySet().iterator();
					while (itr.hasNext()) {
						Entry<ChatMsgContent, VectorClock> it2 = itr.next();
						msgBag.remove(it2.getKey(), it2.getValue());
					}	
				}
				if (LOG_ON && DIFFUSION.isInfoEnabled()) {
					DIFFUSION.info(Log.computeClientLogMessage(this, "msgBag size =" + msgBag.size()));
				}		
			}
			incrementNbChatMsgContentReceived();
		}
		assert invariant();
	}

	@Override
	public String toString() {
		return "Client " + identity + v.getEntry(identity);
	}
}
