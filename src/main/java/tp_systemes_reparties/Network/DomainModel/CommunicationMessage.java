package tp_systemes_reparties.Network.DomainModel;

import tp_systemes_reparties.Network.NetworkInterfaces.Client.Client;

public class CommunicationMessage extends SimpleMessage {

	private static final long serialVersionUID = -1768134606501957579L;

	public CommunicationMessage(Client client, String content) {
		super(client);

		this.setContent(content);
	}
}
