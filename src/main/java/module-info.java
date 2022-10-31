module tech.asmussen.pizzaworld {
	
	requires javafx.controls;
	requires javafx.fxml;
	requires com.google.gson;
	
	exports tech.asmussen.pizzaworld.menu;
	opens tech.asmussen.pizzaworld.menu to com.google.gson;
	
	exports tech.asmussen.pizzaworld.ui;
	opens tech.asmussen.pizzaworld.ui to javafx.fxml;
}
