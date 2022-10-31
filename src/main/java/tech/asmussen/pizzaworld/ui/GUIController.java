package tech.asmussen.pizzaworld.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.menu.Pizza;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {
	
	@FXML
	private ListView<String> pizzaList;
	
	@FXML
	private Label currentPizzaName;
	
	@FXML
	private Label currentPizzaDescription;
	
	@FXML
	private Label currentPizzaBasePrice;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
		for (Pizza pizza : Menu.PIZZAS) {
			
			pizzaList.getItems().add(pizza.name());
		}
		
		pizzaList.getSelectionModel().selectedItemProperty().addListener((observableValue, pizza, value) -> {
			
			int index = Menu.indexOf(value);
			
			if (index == -1) {
				
				currentPizzaName.setText("Intet");
				currentPizzaDescription.setText("Ingen");
				currentPizzaBasePrice.setText("0 kr.");
				
			} else {
				
				Pizza selectedPizza = Menu.PIZZAS[index];
				
				currentPizzaName.setText(selectedPizza.name());
				currentPizzaDescription.setText(selectedPizza.description());
				currentPizzaBasePrice.setText(String.format("%.2f kr.", selectedPizza.price()));
			}
		});
	}
}
