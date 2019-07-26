package tp_systemes_reparties.Network.NetworkInterfaces.ServerWorker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

import tp_systemes_reparties.Network.DomainModel.Message;
import tp_systemes_reparties.Network.DomainModel.NetworkInterface;
import tp_systemes_reparties.Network.NetworkInterfaces.Server.Server;

public abstract class ServerWorker extends Thread implements NetworkInterface {

	private Socket client;
	private ObjectInputStream receiver;
	private ObjectOutputStream emeetter;
	private boolean handle;
	private Server server;

	public ServerWorker(Server server, Socket client) {
		this.client = client;
		this.server = server;
	}

	@Override
	public void run() {
		try {
			this.connect();

			while (this.handle) {
				Message message = this.receiveMessage();

				this.onCommunicationMessageArrived(message);

				if (message.equals(Message.DISCONNECTION_MESSAGE)) {
					this.closeConnection();
				}
			}
		} catch (SocketException exp) {
			exp.printStackTrace();
		} catch (IOException | ClassNotFoundException exp) {
			exp.printStackTrace();
		}
	}

	@Override
	public void connect() throws IOException {
		this.emeetter = new ObjectOutputStream(this.client.getOutputStream());
		this.receiver = new ObjectInputStream(this.client.getInputStream());
		this.handle = true;
	}

	@Override
	public void sendMessage(Message message) throws IOException {
		this.emeetter.writeObject(message);
		this.emeetter.flush();
	}

	@Override
	public Message receiveMessage() throws IOException, ClassNotFoundException {
		return (Message) this.receiver.readObject();
	}

	@Override
	public void closeConnection() throws IOException {
		this.emeetter.close();
		this.receiver.close();
		this.client.close();

		this.handle = false;
	}

	public abstract void onCommunicationMessageArrived(Message communicationMessage);
}