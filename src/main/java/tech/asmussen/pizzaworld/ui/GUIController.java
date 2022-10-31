package tech.asmussen.pizzaworld.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.menu.Pizza;
import tech.asmussen.pizzaworld.menu.Topping;

import java.net.URL;
import java.util.ResourceBundle;

public class GUIController implements Initializable {
	
	@FXML
	private ListView<String> pizzaList;
	
	@FXML
	private ListView<String> selectedPizzaList;
	
	@FXML
	private ListView<String> extraToppingList;
	
	@FXML
	private Label currentPizzaName;
	
	@FXML
	private Label currentPizzaDescription;
	
	@FXML
	private Label currentPriceLabel;
	
	@FXML
	private Label totalPriceLabel;
	
	@FXML
	private Button orderButton;
	
	@FXML
	private Button addToCartButton;
	
	@FXML
	private Button addExtraTopping;
	
	@FXML
	private Button removeExtraTopping;
	
	private double totalPrice;
	
	private Pizza currentPizza;
	
	private double currentPrice;
	
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		
		orderButton.setDisable(true);
		
		totalPrice = 0;
		
		for (Pizza pizza : Menu.PIZZAS) {
			
			pizzaList.getItems().add(pizza.name());
		}
		
		pizzaList.getSelectionModel().selectedItemProperty().addListener((observableValue, pizza, value) -> {
			
			int index = Menu.pizzaIndexOf(value);
			
			if (index == -1) {
				
				currentPizzaName.setText("Intet");
				currentPizzaDescription.setText("Ingen");
				currentPriceLabel.setText("0 kr.");
				
				currentPizza = null;
				currentPrice = 0;
				
			} else {
				
				Pizza selectedPizza = Menu.PIZZAS[index];
				
				currentPizzaName.setText(selectedPizza.name());
				currentPizzaDescription.setText(selectedPizza.description());
				
				currentPizza = selectedPizza;
				currentPrice = selectedPizza.getFullPrice();
			}
			
			updateSelectedPizzaPrice();
			
			// Disable the 'Tilføj til Kurv' button if no pizza is selected.
			addToCartButton.setDisable(currentPizza == null);
			
			extraToppingList.getItems().clear();
			
			if (currentPizza != null) {
				
				for (Topping topping : currentPizza.toppings()) {
					
					if (extraToppingList.getItems().contains(topping.name())) continue;
					
					extraToppingList.getItems().add(topping.name());
				}
				
			} else {
				
				addExtraTopping.setDisable(true);
				removeExtraTopping.setDisable(true);
			}
		});
		
		extraToppingList.getSelectionModel().selectedItemProperty().addListener((observableValue, topping, value) -> {
			
			addExtraTopping.setDisable(value == null);
			removeExtraTopping.setDisable(value == null);
		});
	}
	
	@FXML
	protected void onAddToCartButtonClicked() {
		
		selectedPizzaList.getItems().add(currentPizza.name());
		
		updateTotalPrice();
	}
	
	@FXML
	protected void onAddExtraToppingButtonClicked() {
		
		removeExtraTopping.setDisable(currentPrice == currentPizza.getFullPrice());
		
		String topping = extraToppingList.getSelectionModel().getSelectedItem();
		
		if (topping == null) return;
		
		int index = Menu.toppingIndexOf(topping);
		
		if (index == -1) return;
		
		currentPrice += Menu.TOPPINGS[index].price();
		
		updateSelectedPizzaPrice();
	}
	
	@FXML
	protected void onRemoveExtraToppingButtonClicked() {
		
		if (currentPrice == currentPizza.getFullPrice()) {
			
			removeExtraTopping.setDisable(true);
			
			return;
		}
		
		String topping = extraToppingList.getSelectionModel().getSelectedItem();
		
		if (topping == null) return;
		
		int index = Menu.toppingIndexOf(topping);
		
		if (index == -1) return;
		
		currentPrice -= Menu.TOPPINGS[index].price();
		
		updateSelectedPizzaPrice();
	}
	
	@FXML
	protected void onOrderButtonClicked() {
		
		String message = String.format("Du har bestilt %d %s til %.2f kr!",
				selectedPizzaList.getItems().size(),
				selectedPizzaList.getItems().size() == 1 ? "pizza" : "pizzaer",
				totalPrice);
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		
		alert.setTitle("Bestilling Modtaget");
		alert.setHeaderText("Vi har modtaget din bestilling!");
		alert.setContentText(message);
		alert.show();
	}
	
	private void updateSelectedPizzaPrice() {
		
		if (currentPizza == null) return;
		
		currentPriceLabel.setText(String.format("%.2f kr.", currentPrice));
	}
	
	private void updateTotalPrice() {
		
		totalPrice += currentPrice;
		
		totalPriceLabel.setText(String.format("Total Pris: %.2f kr.", totalPrice));
		
		orderButton.setDisable(totalPrice <= 0);
		
		currentPrice = currentPizza.getFullPrice();
		
		updateSelectedPizzaPrice();
	}
}
