package tp_systemes_reparties.Network.NetworkInterfaces.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;

import tp_systemes_reparties.Network.DomainModel.Message;
import tp_systemes_reparties.Network.DomainModel.RequestMessage;
import tp_systemes_reparties.Network.NetworkInterfaces.ServerWorker.ServerWorker;

public abstract class Server {
	private ServerSocket socket;
	private int port;
	private boolean serverOn;
	private Map<String, Function<Entry<ServerWorker, RequestMessage>, RequestMessage>> remoteFunctions;
	private Thread thread;

	public Server(int port) throws IOException {
		this.port = port;
		this.serverOn = true;
		this.remoteFunctions = new HashMap<>();
	}

	public void startInstance() throws IOException {
		this.socket = new ServerSocket(this.port);
		this.onServerStarted();

		this.thread = new Thread(() -> {
			try {
				while (this.serverOn) {
					this.initServerWorker(this.socket.accept()).start();
				}
			} catch (IOException exp) {
				exp.printStackTrace();
			}
		});

		this.thread.start();

		System.out.println("The server identified by " + this.port + " is ON");
	}

	public ServerSocket getSocket() {
		return socket;
	}

	public void setSocket(ServerSocket socket) {
		this.socket = socket;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isServerOn() {
		return serverOn;
	}

	public void setServerOn(boolean serverOn) {
		this.serverOn = serverOn;
	}

	public Map<String, Function<Entry<ServerWorker, RequestMessage>, RequestMessage>> getRemoteFunctions() {
		return remoteFunctions;
	}

	public void setRemoteFunctions(
			Map<String, Function<Entry<ServerWorker, RequestMessage>, RequestMessage>> remoteFunctions) {
		this.remoteFunctions = remoteFunctions;
	}

	public void registerFunction(String functionDefinition,
			Function<Entry<ServerWorker, RequestMessage>, RequestMessage> function) {
		this.remoteFunctions.put(functionDefinition, function);
	}

	public Function<Entry<ServerWorker, RequestMessage>, RequestMessage> retrieveFunction(String functionDefinition) {
		return this.remoteFunctions.get(functionDefinition);
	}

	public ServerWorker initServerWorker(Socket client) {
		ServerWorker serverWorker = new ServerWorker(this, client) {

			@Override
			public void onCommunicationMessageArrived(Message communicationMessage) {
				onMessageArrived(this, communicationMessage);
			}
		};

		this.onConnectionRequestAccepted(client, serverWorker);

		return serverWorker;
	}

	@SuppressWarnings("deprecation")
	public void closeConnection() throws IOException {
		this.socket.close();
		this.serverOn = false;

		this.thread.stop();
		this.thread = null;
	}

	public abstract void onServerStarted();

	public abstract void onConnectionRequestAccepted(Socket client, ServerWorker serverWorker);

	public abstract void onMessageArrived(ServerWorker serverWorker, Message communicationMessage);
}
