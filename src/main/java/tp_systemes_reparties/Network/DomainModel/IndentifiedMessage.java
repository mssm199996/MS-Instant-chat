package tp_systemes_reparties.Network.DomainModel;

public class IndentifiedMessage implements Message {

	private static final long serialVersionUID = 3274436686865611693L;

	private int id;

	public IndentifiedMessage() {
		this(0);
	}

	public IndentifiedMessage(int id) {
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String contenu() {
		return this.id + "";
	}

	@Override
	public String expediteur() {
		return "UNKNOWN";
	}

	@Override
	public int hashCode() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof IndentifiedMessage && this.getId() == ((IndentifiedMessage) o).getId();
	}

	@Override
	public String toString() {
		return "IDENTIFIED MESSAGE N° " + this.id;
	}
}
