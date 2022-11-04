package tech.asmussen.pizzaworld.menu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

public final class Menu {
	
	public static final byte MAX_EXTRA_TOPPINGS = 10;
	public static final byte MIN_EXTRA_TOPPINGS = 0;
	
	public static final Topping[] TOPPINGS;
	public static final Pizza[] PIZZAS;
	
	static {
		
		Topping[] toppings;
		Pizza[] pizzas;
		
		// Load the toppings from Toppings.json and the pizzas from Pizzas.json.
		BufferedReader toppingsReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Menu.class.getResourceAsStream("Toppings.json"), "Toppings.json not found!")));
		BufferedReader pizzasReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(Menu.class.getResourceAsStream("Pizzas.json"), "Pizzas.json not found!")));
		
		StringBuilder toppingJsonBuilder = new StringBuilder();
		StringBuilder pizzasJsonBuilder = new StringBuilder();
		
		try {
			
			String line;
			while ((line = toppingsReader.readLine()) != null) {
				
				toppingJsonBuilder.append(line);
			}
			
			toppingsReader.close();
			
			while ((line = pizzasReader.readLine()) != null) {
				
				pizzasJsonBuilder.append(line);
			}
			
			pizzasReader.close();
			
			Gson gson = new Gson();
			
			JsonArray jsonToppings = gson.fromJson(toppingJsonBuilder.toString(), JsonArray.class);
			
			toppings = gson.fromJson(jsonToppings, Topping[].class);
			
			JsonArray jsonPizzas = gson.fromJson(pizzasJsonBuilder.toString(), JsonArray.class);
			
			pizzas = new Pizza[jsonPizzas.size()];
			
			for (int i = 0; i < jsonPizzas.size(); i++) {
				
				int id = jsonPizzas.get(i).getAsJsonObject().get("id").getAsInt();
				
				String name = jsonPizzas.get(i).getAsJsonObject().get("name").getAsString();
				String description = jsonPizzas.get(i).getAsJsonObject().get("description").getAsString();
				
				double price = jsonPizzas.get(i).getAsJsonObject().get("price").getAsDouble();
				
				JsonArray jsonToppingIds = jsonPizzas.get(i).getAsJsonObject().get("toppings").getAsJsonArray();
				
				Topping[] pizzaToppings = new Topping[jsonToppingIds.size()];
				
				for (int j = 0; j < jsonToppingIds.size(); j++) {
					
					int toppingId = jsonToppingIds.get(j).getAsInt();
					
					for (Topping topping : toppings) {
						
						if (topping.id() == toppingId) {
							
							pizzaToppings[j] = topping;
						}
					}
				}
				
				pizzas[i] = new Pizza(id, name, description, price, pizzaToppings);
			}
			
		} catch (Exception e) {
			
			toppings = new Topping[0];
			pizzas = new Pizza[0];
		}
		
		TOPPINGS = toppings;
		PIZZAS = pizzas;
	}
	
	public static int pizzaIndexOf(String pizzaName) {
		
		for (int i = 0; i < PIZZAS.length; i++) {
			
			if (PIZZAS[i].name().equalsIgnoreCase(pizzaName)) {
				
				return i;
			}
		}
		
		return -1;
	}
	
	public static int toppingIndexOf(String toppingName) {
		
		for (int i = 0; i < TOPPINGS.length; i++) {
			
			if (TOPPINGS[i].name().equalsIgnoreCase(toppingName)) {
				
				return i;
			}
		}
		
		return -1;
	}
}
