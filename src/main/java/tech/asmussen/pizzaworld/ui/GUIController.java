package tech.asmussen.pizzaworld.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.menu.Pizza;
import tech.asmussen.pizzaworld.menu.Topping;

import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;

public class GUIController implements Initializable {
	
	@FXML
	private ListView<String> pizzas;
	
	@FXML
	private ListView<String> toppings;
	
	@FXML
	private Label pizzaName;
	
	@FXML
	private Label pizzaDescription;
	
	@FXML
	private Label pizzaPrice;
	
	@FXML
	private Button addToppingButton;
	
	@FXML
	private Button removeToppingButton;
	
	@FXML
	private ListView<String> cart;
	
	@FXML
	private Button addToCartButton;
	
	@FXML
	private Label totalPrice;
	
	@FXML
	private Label toppingCountLabel;
	
	@FXML
	private Button orderButton;
	
	private double cartTotal;
	
	private double pizzaTotal;
	
	// Topping -> Number of that topping.
	private final HashMap<Topping, Integer> extraToppings = new HashMap<>();
	
	private Topping selectedTopping;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Arrays.stream(Menu.PIZZAS).forEach(pizza -> pizzas.getItems().add(String.format("%s - %.2f kr.", pizza.name(), pizza.price())));
		
		pizzas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			Pizza pizza = Menu.PIZZAS[pizzas.getSelectionModel().getSelectedIndex()];
			
			pizzaName.setText(pizza.name());
			pizzaDescription.setText(pizza.description());
			pizzaPrice.setText(String.format("%.2f kr.", pizza.price()));
			pizzaTotal = pizza.getFullPrice();
			
			toppings.getItems().clear();
			
			Arrays.stream(pizza.toppings()).forEach(topping -> {
				
				String formattedTopping = String.format("%s - %.2f kr.", topping.name(), topping.price());
				
				if (!toppings.getItems().contains(formattedTopping)) {
					
					toppings.getItems().add(formattedTopping);
				}
			});
			
			updatePizza();
		});
		
		toppings.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			updatePizza();
			
			selectedTopping = Menu.TOPPINGS[toppings.getSelectionModel().getSelectedIndex()];
		});
	}
	
	private void updatePizza() {
		
		double total = pizzaTotal;
		
		for (Topping topping : extraToppings.keySet()) {
			
			total += topping.price() * extraToppings.get(topping);
		}
		
		pizzaPrice.setText(String.format("%.2f kr.", total));
		
		int toppingCount = extraToppings.values().stream().mapToInt(Integer::intValue).sum();
		
		addToppingButton.setDisable(toppingCount >= Menu.MAX_EXTRA_TOPPINGS);
		removeToppingButton.setDisable(toppingCount <= Menu.MIN_EXTRA_TOPPINGS);
	}
	
	@FXML
	protected void onAddToppingButtonClick() {
		
		extraToppings.put(selectedTopping, extraToppings.getOrDefault(selectedTopping, 0) + 1);
	}
	
	@FXML
	protected void onRemoveToppingButtonClick() {
		
		extraToppings.put(selectedTopping, extraToppings.getOrDefault(selectedTopping, 0) - 1);
	}
	
	@FXML
	protected void onAddToCartButtonClick() {
	
	}
}
