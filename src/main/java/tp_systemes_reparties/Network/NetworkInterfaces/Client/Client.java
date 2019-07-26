package tp_systemes_reparties.Network.NetworkInterfaces.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import tp_systemes_reparties.Network.DomainModel.Message;
import tp_systemes_reparties.Network.DomainModel.NetworkInterface;

public abstract class Client implements NetworkInterface {
	private String serverIp;
	private int serverPort;
	private Socket client;

	private ObjectOutputStream emeetter;
	private ObjectInputStream receiver;

	public Client() {
		this("localhost", 80);
	}

	public Client(int serverPort) {
		this("localhost", serverPort);
	}

	public Client(String serverIp, int serverPort) {
		this.serverIp = serverIp;
		this.serverPort = serverPort;
	}

	@Override
	public void connect() throws IOException {
		this.client = new Socket(this.serverIp, this.serverPort);
		this.emeetter = new ObjectOutputStream(this.client.getOutputStream());
		this.receiver = new ObjectInputStream(this.client.getInputStream());

		this.onConnectionRequestAccepted();
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
	}

	public String getServerIp() {
		return serverIp;
	}

	public void setServerIp(String serverIp) {
		this.serverIp = serverIp;
	}

	public int getServerPort() {
		return serverPort;
	}

	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	public Socket getClient() {
		return client;
	}

	public void setClient(Socket client) {
		this.client = client;
	}

	public ObjectOutputStream getEmeetter() {
		return emeetter;
	}

	public void setEmeetter(ObjectOutputStream emeetter) {
		this.emeetter = emeetter;
	}

	public ObjectInputStream getReceiver() {
		return receiver;
	}

	public void setReceiver(ObjectInputStream receiver) {
		this.receiver = receiver;
	}

	public abstract void onConnectionRequestAccepted();
}
