package tp_systemes_reparties.MVC.MainFrame;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import tp_systemes_reparties.Network.DomainModel.CommunicationMessage;
import tp_systemes_reparties.Network.DomainModel.Message;
import tp_systemes_reparties.Network.NetworkInterfaces.Client.Client;
import tp_systemes_reparties.Network.NetworkInterfaces.Server.Server;
import tp_systemes_reparties.Network.NetworkInterfaces.ServerWorker.ServerWorker;

public class MainFrameController implements Initializable {

	public static Paint BLUE_COLOR = Color.BLUE;
	public static Paint GREEN_COLOR = Color.GREEN;
	public static Paint RED_COLOR = Color.RED;
	public static Paint ORANGE_COLOR = Color.ORANGE;

	@FXML
	private JFXTextField ipAdress;

	@FXML
	private JFXTextField localServerPort;

	@FXML
	private JFXTextField remoteServerPort;

	@FXML
	private JFXRadioButton server;

	@FXML
	private ToggleGroup typeConnexion;

	@FXML
	private JFXRadioButton client;

	@FXML
	private Rectangle connectionStateRectangle;

	@FXML
	private Label connectionState;

	@FXML
	private ListView<Message> messagesList;

	@FXML
	private JFXTextField messageToSend;

	@FXML
	private JFXButton connectButton, disconnectButton;

	private Server serverNetworkInterface;
	private Client clientNetworkInterface;

	@FXML
	void connectHandler(ActionEvent event) {
		try {
			Integer remoteServerPort = Integer.parseInt(this.remoteServerPort.getText());
			Integer localServerPort = Integer.parseInt(this.localServerPort.getText());
			Toggle selection = this.typeConnexion.getSelectedToggle();

			if (selection.equals(this.server)) {
				this.updateConnectionState(MainFrameController.ConnectionState.CONNECTING);

				this.serverNetworkInterface = new Server(localServerPort) {

					@Override
					public void onServerStarted() {
						updateConnectionState(MainFrameController.ConnectionState.WAITING);
					}

					@Override
					public void onConnectionRequestAccepted(Socket client, ServerWorker serverWorker) {
						try {
							clientNetworkInterface = new Client(remoteServerPort) {

								@Override
								public void onConnectionRequestAccepted() {
									updateConnectionState(MainFrameController.ConnectionState.CONNECTED);
								}
							};

							clientNetworkInterface.connect();
						} catch (IOException e) {
							e.printStackTrace();

							disconnectHandler(null);
						}
					}

					@Override
					public void onMessageArrived(ServerWorker serverWorker, Message communicationMessage) {
						Platform.runLater(() -> {
							messagesList.getItems().add(communicationMessage);
						});
					}
				};

				this.serverNetworkInterface.startInstance();
			} else {
				this.serverNetworkInterface = new Server(localServerPort) {

					@Override
					public void onServerStarted() {
					}

					@Override
					public void onMessageArrived(ServerWorker serverWorker, Message communicationMessage) {
						Platform.runLater(() -> {
							messagesList.getItems().add(communicationMessage);
						});
					}

					@Override
					public void onConnectionRequestAccepted(Socket client, ServerWorker serverWorker) {
					}
				};

				this.serverNetworkInterface.startInstance();

				this.clientNetworkInterface = new Client(remoteServerPort) {

					@Override
					public void onConnectionRequestAccepted() {
						updateConnectionState(MainFrameController.ConnectionState.CONNECTED);
					}
				};

				this.clientNetworkInterface.connect();
			}
		} catch (IOException e) {
			e.printStackTrace();

			this.disconnectHandler(null);
		}
	}

	@FXML
	void disconnectHandler(ActionEvent event) {
		try {
			this.clientNetworkInterface.closeConnection();
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
		}

		try {
			Thread.sleep(2_000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try {
			this.serverNetworkInterface.closeConnection();
		} catch (NullPointerException | IOException e) {
			e.printStackTrace();
		}

		this.updateConnectionState(MainFrameController.ConnectionState.DISCONNECTED);
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.initToggles();
		this.initRectangles();
		this.initTextFields();

		this.updateConnectionState(MainFrameController.ConnectionState.DISCONNECTED);
	}

	private void initTextFields() {
		this.messageToSend.setOnKeyReleased(event -> {
			if (event.getCode() == KeyCode.ENTER) {
				Message message = new CommunicationMessage(this.clientNetworkInterface, this.messageToSend.getText());

				try {
					this.clientNetworkInterface.sendMessage(message);
					this.messageToSend.setText("");
					this.messagesList.getItems().add(message);
				} catch (IOException e) {
					e.printStackTrace();

					disconnectHandler(null);
				}
			}
		});
	}

	private void initToggles() {
		this.typeConnexion.selectedToggleProperty().addListener((obs, oldValue, newValue) -> {
			boolean isServer = newValue.equals(this.server);

			this.ipAdress.setDisable(isServer);
		});
	}

	private void initRectangles() {
		this.connectionStateRectangle.fillProperty().addListener((obs, oldValue, newValue) -> {
			boolean isDisconnected = newValue.equals(MainFrameController.RED_COLOR);
			boolean isReadyToSendMessage = newValue.equals(MainFrameController.BLUE_COLOR);

			this.ipAdress.setDisable(!isDisconnected);
			this.remoteServerPort.setDisable(!isDisconnected);
			this.localServerPort.setDisable(!isDisconnected);
			this.connectButton.setDisable(!isDisconnected);
			this.disconnectButton.setDisable(isDisconnected);
			this.server.setDisable(!isDisconnected);
			this.client.setDisable(!isDisconnected);

			this.messageToSend.setDisable(!isReadyToSendMessage);
		});
	}

	public void updateConnectionState(ConnectionState connectionState) {
		switch (connectionState) {
		case CONNECTED:
			this.connectionStateRectangle.setFill(MainFrameController.BLUE_COLOR);
			this.connectionState.setText("Network interface ON and connected to a pair.");
			break;
		case WAITING:
			this.connectionStateRectangle.setFill(MainFrameController.GREEN_COLOR);
			this.connectionState.setText("Network interface ON and waiting for a pair...");
			break;
		case CONNECTING:
			this.connectionStateRectangle.setFill(MainFrameController.ORANGE_COLOR);
			this.connectionState.setText("Network interface connecting, please wait...");
			break;
		case DISCONNECTED:
			this.connectionStateRectangle.setFill(MainFrameController.RED_COLOR);
			this.connectionState.setText("Network interface disconnected.");
			break;
		}
	}

	public static enum ConnectionState {
		DISCONNECTED, CONNECTING, WAITING, CONNECTED
	}
}
