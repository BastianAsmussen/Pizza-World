package tech.asmussen.pizzaworld.ui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import tech.asmussen.pizzaworld.PizzaWorld;
import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.menu.Pizza;
import tech.asmussen.pizzaworld.menu.Topping;

import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class GUIController extends PizzaWorld implements Initializable {
	
	private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,###.##");
	private static final DecimalFormat QUANTITY_FORMAT = new DecimalFormat("#,###");
	
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
		
		Arrays.stream(Menu.PIZZAS).forEach(pizza -> pizzas.getItems().add(String.format("%s - %s", pizza.name(), PRICE_FORMAT.format(pizza.fullPrice()) + " kr.")));
		
		pizzas.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			
			resetPizza();
			
			int index = Menu.pizzaIndexOf(newValue.split(" - ")[0]);
			
			Pizza pizza = Menu.PIZZAS[index];
			
			pizzaNameLabel.setText(pizza.name());
			pizzaDescription.setText(pizza.description());
			pizzaPriceLabel.setText(PRICE_FORMAT.format(pizza.fullPrice()) + " kr.");
			pizzaTotal = pizza.fullPrice();
			
			toppings.getItems().clear();
			
			Arrays.stream(pizza.toppings()).forEach(topping -> {
				
				String formattedTopping = String.format("%s - %s", topping.name(), PRICE_FORMAT.format(topping.price()) + " kr.");
				
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
		
		cart.getItems().add(String.format("%s - %s", pizza.name(), PRICE_FORMAT.format(pizzaPrice) + " kr."));
		cart.scrollTo(cart.getItems().size() - 1);
		
		cartTotal += pizzaPrice;
		
		totalPrice.setText(String.format("Total pris: %s", PRICE_FORMAT.format(cartTotal) + " kr."));
		
		resetPizza();
		
		orderButton.setDisable(cart.getItems().size() == 0);
	}
	
	private void updatePizza() {
		
		double total = pizzaTotal;
		
		for (Topping topping : extraToppings.keySet()) {
			
			total += topping.price() * extraToppings.get(topping);
		}
		
		pizzaPriceLabel.setText(PRICE_FORMAT.format(total) + " kr.");
		
		updateToppingButtons();
		
		addToCartButton.setDisable(false);
	}
	
	private void resetCart() {
		
		cart.getItems().clear();
		cartTotal = 0;
		
		totalPrice.setText("Total pris: 0 kr.");
		
		orderButton.setDisable(true);
		
		resetPizza();
	}
	
	private void resetPizza() {
		
		pizzaNameLabel.setText("Ingen.");
		pizzaDescription.setText("Intet.");
		pizzaPriceLabel.setText(PRICE_FORMAT.format(0) + " kr.");
		pizzaTotal = 0;
		
		toppings.getItems().clear();
		
		selectedTopping = null;
		extraToppings.clear();
		
		updateToppingButtons();
		
		toppingCountLabel.setText(String.format("%d/%s", 0, QUANTITY_FORMAT.format(Menu.MAX_EXTRA_TOPPINGS)));
		
		addToCartButton.setDisable(true);
	}
	
	private void updateToppingButtons() {
		
		int totalToppingCount = extraToppings.values().stream().mapToInt(Integer::intValue).sum();
		int selectedToppingCount = extraToppings.getOrDefault(selectedTopping, 0);
		
		addToppingButton.setDisable(totalToppingCount >= Menu.MAX_EXTRA_TOPPINGS || selectedTopping == null);
		removeToppingButton.setDisable(selectedToppingCount <= Menu.MIN_EXTRA_TOPPINGS);
		showExtraToppingsButton.setDisable(totalToppingCount == 0 || selectedTopping == null);
		
		toppingCountLabel.setText(String.format("%s/%s", QUANTITY_FORMAT.format(totalToppingCount), QUANTITY_FORMAT.format(Menu.MAX_EXTRA_TOPPINGS)));
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
		
		String pizzaCount = String.format("%s %s", QUANTITY_FORMAT.format(cart.getItems().size()), cart.getItems().size() == 1 ? "pizza" : "pizzaer");
		
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
		
		Label header = new Label(String.format("Kvittering (%s)", PRICE_FORMAT.format(cartTotal) + " kr."));
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
		
		AtomicReference<Double> total = new AtomicReference<>((double) 0);
		extraToppings.forEach((topping, count) -> total.updateAndGet(v -> v + topping.price() * count));
		
		Label header = new Label(String.format("Ekstra Toppings (%s)", PRICE_FORMAT.format(total.get()) + " kr."));
		TextArea content = new TextArea();
		
		ArrayList<String> formattedToppings = new ArrayList<>();
		
		for (Topping topping : extraToppings.keySet()) {
			
			int count = extraToppings.get(topping);
			
			if (count == 0) continue;
			
			double price = topping.price() * count;
			
			formattedToppings.add(String.format("%s x%d - %s", topping.name(), count, PRICE_FORMAT.format(price) + " kr."));
		}
		
		content.setText(String.join("\n", formattedToppings));
		content.setEditable(false);
		content.setWrapText(true);
		
		container.getChildren().addAll(header, content);
		
		DialogPane dialogPane = new DialogPane();
		
		dialogPane.setContent(container);
		
		return dialogPane;
	}
}
