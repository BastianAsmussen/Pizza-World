package tech.asmussen.pizzaworld;

import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.ui.GUIApplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PizzaWorld {
	
	public static void main(String[] args) {
		
		final long startTime = System.currentTimeMillis();
		
		// Start the GUI in a new thread.
		new Thread(() -> GUIApplication.main(args)).start();
		
		System.out.println("Program started successfully!");
		
		System.out.printf("Pizzas: %d%n", Menu.PIZZAS.length);
		System.out.printf("Toppings: %d%n", Menu.TOPPINGS.length);
		System.out.printf("Load Time: %d ms.%n", System.currentTimeMillis() - startTime);
	}
	
	public static String getTimeFormatted() {
		
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy @ HH:mm");
		
		return format.format(new Date());
	}
}
