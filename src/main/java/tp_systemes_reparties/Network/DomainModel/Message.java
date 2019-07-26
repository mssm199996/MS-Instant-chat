package tp_systemes_reparties.Network.DomainModel;

import java.io.Serializable;

public interface Message extends Serializable {
	public static final Message DISCONNECTION_MESSAGE = new IndentifiedMessage(2);
	public static final Message ACKNOWLEDGE_MESSAGE = new IndentifiedMessage(1);
	public static final Message UNRECOGNIZED_MESSAGE = new IndentifiedMessage(0);

	public abstract String expediteur();

	public abstract String contenu();
}
