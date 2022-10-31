package tech.asmussen.pizzaworld;

import tech.asmussen.pizzaworld.menu.Menu;
import tech.asmussen.pizzaworld.menu.Pizza;
import tech.asmussen.pizzaworld.ui.GUIApplication;

public class Main {
	
	public static void main(String[] args) {
		
		final long startTime = System.currentTimeMillis();
		
		// Start the GUI in a new thread.
		new Thread(() -> GUIApplication.main(args)).start();
		
		System.out.printf("Loaded %d pizzas in %d ms.%n",
				Menu.PIZZAS.length, System.currentTimeMillis() - startTime);
	}
}
