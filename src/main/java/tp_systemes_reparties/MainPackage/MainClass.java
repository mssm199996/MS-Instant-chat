package tp_systemes_reparties.MainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainClass extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		BorderPane root = (BorderPane) FXMLLoader
				.load(getClass().getResource("/tp_systemes_reparties/MVC/MainFrame/MainFrameFXML.fxml"));

		primaryStage.setScene(new Scene(root));
		primaryStage.setTitle("MSSM Tchat");
		primaryStage.addEventFilter(WindowEvent.WINDOW_CLOSE_REQUEST, event -> {
			System.exit(0);
		});
		primaryStage.show();
	}
}
