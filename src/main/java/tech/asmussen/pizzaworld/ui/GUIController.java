package tech.asmussen.pizzaworld.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.menu.Pizza;
import tech.asmussen.pizzaworld.menu.Topping;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

public class GUIController implements Initializable {
	
	// Topping -> Number of that topping.
	private final HashMap<Topping, Integer> extraToppings = new HashMap<>();
	
	@FXML
	private ListView<String> pizzas;
	@FXML
	private ListView<String> toppings;
	@FXML
	private Label pizzaNameLabel;
	@FXML
	private Label pizzaDescription;
	@FXML
	private Label pizzaPriceLabel;
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
	
	@FXML
	private Button showExtraToppingsButton;
	
	private double cartTotal;
	private double pizzaTotal;
	
	private Topping selectedTopping;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
		Arrays.stream(Menu.PIZZAS).forEach(pizza -> pizzas.getItems().add(String.format("%s - %.2f kr.", pizza.name(), pizza.getFullPrice())));
		
		pizzas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			resetPizza();
			
			int index = Menu.pizzaIndexOf(newValue.split(" - ")[0]);
			
			Pizza pizza = Menu.PIZZAS[index];
			
			pizzaNameLabel.setText(pizza.name());
			pizzaDescription.setText(pizza.description());
			pizzaPriceLabel.setText(String.format("%.2f kr.", pizza.getFullPrice()));
			pizzaTotal = pizza.getFullPrice();
			
			toppings.getItems().clear();
			
			Arrays.stream(pizza.toppings()).forEach(topping -> {
				
				String formattedTopping = String.format("%s - %.2f kr.", topping.name(), topping.price());
				
				if (!toppings.getItems().contains(formattedTopping)) {
					
					toppings.getItems().add(formattedTopping);
				}
			});
			
			updatePizza();
			
			toppings.scrollTo(toppings.getItems().size() - 1);
		});
		
		toppings.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			if (newValue == null) return;
			
			int index = Menu.toppingIndexOf(newValue.split(" - ")[0]);
			
			selectedTopping = Menu.TOPPINGS[index];
			
			updateToppingButtons();
		});
	}
	
	private void updateCart() {
		
		int pizzaIndex = Menu.pizzaIndexOf(pizzaNameLabel.getText());
		
		Pizza pizza = Menu.PIZZAS[pizzaIndex];
		
		String rawPizzaPrice = pizzaPriceLabel.getText().split(" ")[0];
		double pizzaPrice = Double.parseDouble(rawPizzaPrice.replaceAll(",", "."));
		
		cart.getItems().add(String.format("%s - %.2f kr.", pizza.name(), pizzaPrice));
		cart.scrollTo(cart.getItems().size() - 1);
		
		cartTotal += pizzaPrice;
		
		totalPrice.setText(String.format("Total Pris: %.2f kr.", cartTotal));
		
		resetPizza();
		
		orderButton.setDisable(cart.getItems().size() == 0);
	}
	
	private void updatePizza() {
		
		double total = pizzaTotal;
		
		for (Topping topping : extraToppings.keySet()) {
			
			total += topping.price() * extraToppings.get(topping);
		}
		
		pizzaPriceLabel.setText(String.format("%.2f kr.", total));
		
		updateToppingButtons();
		
		addToCartButton.setDisable(false);
	}
	
	private void resetCart() {
		
		cart.getItems().clear();
		cartTotal = 0;
		
		totalPrice.setText("Total Pris: 0 kr.");
		
		orderButton.setDisable(true);
		
		resetPizza();
	}
	
	private void resetPizza() {
		
		pizzaNameLabel.setText("Ingen.");
		pizzaDescription.setText("Intet.");
		pizzaPriceLabel.setText(String.format("%.2f kr.", 0.0));
		pizzaTotal = 0;
		
		toppings.getItems().clear();
		
		selectedTopping = null;
		extraToppings.clear();
		
		updateToppingButtons();
		
		toppingCountLabel.setText(String.format("%d/%d", 0, Menu.MAX_EXTRA_TOPPINGS));
		
		addToCartButton.setDisable(true);
	}
	
	private void updateToppingButtons() {
		
		int totalToppingCount = extraToppings.values().stream().mapToInt(Integer::intValue).sum();
		int selectedToppingCount = extraToppings.getOrDefault(selectedTopping, 0);
		
		addToppingButton.setDisable(totalToppingCount >= Menu.MAX_EXTRA_TOPPINGS || selectedTopping == null);
		removeToppingButton.setDisable(selectedToppingCount <= Menu.MIN_EXTRA_TOPPINGS);
		showExtraToppingsButton.setDisable(totalToppingCount == 0 || selectedTopping == null);
		
		toppingCountLabel.setText(String.format("%d/%d", totalToppingCount, Menu.MAX_EXTRA_TOPPINGS));
	}
	
	@FXML
	protected void onAddToppingButtonClick() {
		
		int currentToppingCount = extraToppings.getOrDefault(selectedTopping, 0);
		
		extraToppings.put(selectedTopping, currentToppingCount + 1);
		
		updatePizza();
	}
	
	@FXML
	protected void onRemoveToppingButtonClick() {
		
		int toppingCount = extraToppings.getOrDefault(selectedTopping, 0);
		
		extraToppings.put(selectedTopping, toppingCount - 1);
		
		updatePizza();
	}
	
	@FXML
	protected void onAddToCartButtonClick() {
		
		updateCart();
	}
	
	@FXML
	protected void onOrderButtonClick() {
		
		String pizzaCount = String.format("%d %s", cart.getItems().size(), cart.getItems().size() == 1 ? "pizza" : "pizzaer");
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		
		alert.setTitle(String.format("Bestilling (%s)", getTimeFormatted()));
		alert.setHeaderText(String.format("Din bestilling af %s er modtaget!", pizzaCount));
		alert.getDialogPane().setContent(getReceipt());
		alert.show();
		
		resetCart();
	}
	
	@FXML
	protected void onShowExtraToppingsButtonClick() {
		
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		
		alert.setTitle(String.format("Ekstra Toppings (%s)", getTimeFormatted()));
		alert.setHeaderText("Se dine valgte ekstra toppings.");
		alert.getDialogPane().setContent(getExtraToppings());
		alert.show();
	}
	
	private DialogPane getReceipt() {
		
		VBox container = new VBox();
		
		Label header = new Label(String.format("Kvittering (%.2f kr.)", cartTotal));
		TextArea content = new TextArea();
		
		content.setText(String.join("\n", cart.getItems()));
		content.setEditable(false);
		content.setWrapText(true);
		
		container.getChildren().addAll(header, content);
		
		DialogPane dialogPane = new DialogPane();
		
		dialogPane.setContent(container);
		
		return dialogPane;
	}
	
	private DialogPane getExtraToppings() {
		
		VBox container = new VBox();
		
		Label header = new Label("Ekstra Toppings");
		TextArea content = new TextArea();
		
		ArrayList<String> formattedToppings = new ArrayList<>();
		
		for (Topping topping : extraToppings.keySet()) {
			
			int count = extraToppings.get(topping);
			
			if (count == 0) continue;
			
			double price = topping.price() * count;
			
			formattedToppings.add(String.format("%s x%d - %.2f kr.", topping.name(), count, price));
		}
		
		content.setText(String.join("\n", formattedToppings));
		content.setEditable(false);
		content.setWrapText(true);
		
		container.getChildren().addAll(header, content);
		
		DialogPane dialogPane = new DialogPane();
		
		dialogPane.setContent(container);
		
		return dialogPane;
	}
	
	private String getTimeFormatted() {
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy @ HH:mm");
		
		return format.format(new Date());
	}
}
