package tp_systemes_reparties.Network.DomainModel;

import tp_systemes_reparties.Network.NetworkInterfaces.Client.Client;

public abstract class SimpleMessage implements Message {

	private static final long serialVersionUID = 1491931241946374231L;

	private String source;
	private String content;

	public SimpleMessage() {

	}

	public SimpleMessage(Client client) {
		this.source = client.getClient().getInetAddress().getHostAddress();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return this.source + ": " + this.content;
	}

	@Override
	public String contenu() {
		return this.content;
	}

	@Override
	public String expediteur() {
		return this.source;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RequestMessage && this.getSource().equals(((RequestMessage) o).getSource())
				&& this.getContent().equals(((RequestMessage) o).getContent());
	}
}
