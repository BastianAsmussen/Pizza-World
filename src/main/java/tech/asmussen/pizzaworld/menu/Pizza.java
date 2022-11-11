package tech.asmussen.pizzaworld.menu;

public record Pizza(int id, String name, String description, double price, Topping[] toppings) {
	
	public double fullPrice() {
		
		double fullPrice = price;
		
		for (Topping topping : toppings) {
			
			fullPrice += topping.price();
		}
		
		return fullPrice;
	}
}
