package tp_systemes_reparties.Network.DomainModel;

import java.io.IOException;

public interface NetworkInterface {

	public void connect() throws IOException;

	public void sendMessage(Message message) throws IOException;

	public default void sendRequestMessage(String message) throws IOException {
		RequestMessage requestMessage = new RequestMessage();
		requestMessage.setContent(message);

		this.sendMessage(requestMessage);
	}

	public Message receiveMessage() throws IOException, ClassNotFoundException;

	public void closeConnection() throws IOException;

	public default SimpleMessage receiveSimpleMessage() throws IOException, ClassNotFoundException {
		return (SimpleMessage) receiveMessage();
	}
}
