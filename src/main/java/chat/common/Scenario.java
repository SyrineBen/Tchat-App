package chat.common;

import static chat.common.Log.GEN;
import static chat.common.Log.LOG_ON;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import chat.client.Client;
import chat.server.Server;

/**
 * This abstract class defines the way integration tests can be written for
 * instanciating several clients and several servers in the same virtual machine
 * and for executing a sequence of actions in response to user inputs (e.g. chat
 * message on clients or command lines on servers) and to messages received from
 * the network.
 * 
 * @author Denis Conan
 */
public abstract class Scenario {
	/**
	 * the collection of clients.
	 */
	private final List<Client> clients;
	/**
	 * the collection of servers.
	 */
	private final List<Server> servers;

	/**
	 * constructs a scenario, that is initialize the data structures.
	 */
	public Scenario() {
		clients = new ArrayList<>();
		servers = new ArrayList<>();
	}

	/**
	 * instanciates a client. To match the arguments of the constructor of a client,
	 * the server host name is {@code localhost}.
	 * 
	 * @param serverPortNb
	 *            the port number of the server to connect to.
	 * @return the reference to the new client.
	 * @throws UnknownHostException
	 *             the exception thrown in case network configuration problem.
	 */
	public Client instanciateAClient(final int serverPortNb) throws UnknownHostException {
		Client chatClient = new Client(InetAddress.getLocalHost().getHostName(), serverPortNb);
		chatClient.startThreadReadMessagesFromNetwork();
		clients.add(chatClient);
		return chatClient;
	}

	/**
	 * emulates the treatment of keyboard entries. The synchronisation is made in
	 * the client method that is going to be called.
	 * 
	 * @param client
	 *            the reference to the client.
	 * @param inputLine
	 *            the line inputed.
	 * @throws IOException
	 *             the exception thrown when a communication problem occurs during
	 *             the exchanges with the server due to the keyboard entries.
	 */
	public void emulateAnInputLineFromTheConsoleForAClient(final Client client, final String inputLine)
			throws IOException {
		Objects.requireNonNull(client, "argument client cannot be null");
		Objects.requireNonNull(inputLine, "argument inputLine cannot be null");
		client.treatConsoleInput(inputLine);
	}

	/**
	 * Instantiates a server. For the sake of simplicity, the arguments are similar
	 * to the ones of the constructor: the string value {@code localhost} must be
	 * provided for every neighbouring server to which the new server connects to.
	 * 
	 * @param args
	 *            the argument of the server as written when starting the server in
	 *            a separate process:
	 *            {@literal <server number> <list of pairs hostname
	 *            servernumber>}.
	 * @return the new server.
	 */
	public Server instanciateAServer(final String args) {
		Objects.requireNonNull(args, "args cannot be null");
		Server chatServer = new Server(args.split("\\s+"));
		chatServer.startThreadReadMessagesFromNetwork();
		servers.add(chatServer);
		return chatServer;
	}

	/**
	 * emulates the treatment of keyboard entries. The synchronisation is made in
	 * the server method that is going to be called.
	 * 
	 * @param server
	 *            the reference to the server.
	 * @param inputLine
	 *            the line inputed.
	 * @throws IOException 
	 */
	public void emulateAnInputLineFromTheConsoleForAServer(final Server server, final String inputLine) throws IOException {
		Objects.requireNonNull(server, "argument server cannot be null");
		Objects.requireNonNull(inputLine, "argument inputLine cannot be null");
		server.treatConsoleInput(inputLine);
	}

	/**
	 * constructs and runs the scenario.
	 * 
	 * NB: for now, we do not separate the sequence of actions that create clients
	 * and servers from the sequence of actions that exercise the algorithms.
	 * 
	 * @throws Exception
	 *             exception thrown when executing the scenario under test.
	 */
	public abstract void constructAndRun() throws Exception;

	/**
	 * is a utility method to encapsulate {@code Thread.sleep} with an log message
	 * in case of an {@code InterruptedException} is thrown.
	 * 
	 * @param millis
	 *            the timeout duration in milliseconds.
	 */
	public void sleep(final long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			if (LOG_ON && GEN.isTraceEnabled()) {
				GEN.trace(e.getLocalizedMessage());
			}
			// Restore interrupted state...
			Thread.currentThread().interrupt();
		}
	}
}
