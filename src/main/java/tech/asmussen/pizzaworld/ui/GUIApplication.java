package tech.asmussen.pizzaworld.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class GUIApplication extends Application {
	
	@Override
	public void start(Stage stage) throws IOException {
		
		FXMLLoader fxmlLoader = new FXMLLoader(GUIApplication.class.getResource("gui-view.fxml"));
		
		Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
		
		double width = visualBounds.getWidth() / 2;
		double height = visualBounds.getHeight() / 2;
		
		Scene scene = new Scene(fxmlLoader.load(), width, height);
		
		scene.setFill(Color.web("#424549"));
		
		stage.setTitle("Pizza World");
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.getIcons().add(new Image(Objects.requireNonNull(GUIApplication.class.getResourceAsStream("Icon.png"), "Icon is missing!")));
		stage.setScene(scene);
		stage.show();
	}
	
	public static void main(String[] args) {
		
		launch();
	}
}
