package tech.asmussen.pizzaworld.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GUIApplication extends Application {
	
	public static void main(String[] args) {
		
		launch(args);
	}
	
	@Override
	public void start(Stage stage) throws IOException {
		
		FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("gui-view.fxml"));
		
		double width = 960;
		double height = 520;
		
		Scene scene = new Scene(fxmlLoader.load(), width, height);
		
		stage.setTitle("Pizza World");
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.getIcons().add(new Image(Objects.requireNonNull(GUIApplication.class.getResourceAsStream("icon.png"), "Icon is missing!")));
		stage.setScene(scene);
		stage.show();
	}
}
