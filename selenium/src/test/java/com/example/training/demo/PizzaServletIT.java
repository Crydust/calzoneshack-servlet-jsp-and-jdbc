package com.example.training.demo;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;

import java.util.Set;

import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

import com.example.training.demo.PizzaServletPage.PizzaValue;

@ExtendWith(HtmlUnitSeleniumJupiter.class)
class PizzaServletIT {

	@TestTemplate
	void shouldLoadPizzaServletPage(WebDriver driver) {
		PizzaServletPage.go(driver).checkPageIsLoaded();
	}

	@TestTemplate
	void shouldAddPizza(WebDriver driver) {
		PizzaValue pizza = new PizzaValue("(30cm) Medium", "Italian", "Tomato Sauce",
				Set.of("Mozzarella", "Black olives", "Jalapenos", "Pepperoni"));
		PizzaServletPage page = PizzaServletPage.go(driver);

		page = page.addPizza(pizza);

		assertThat(page.readPizzas(), contains(pizza));
	}

	@TestTemplate
	void shouldRemovePizza(WebDriver driver) {
		PizzaValue pizza = new PizzaValue("(30cm) Medium", "Italian", "Tomato Sauce",
				Set.of("Mozzarella", "Black olives", "Jalapenos", "Pepperoni"));
		PizzaServletPage page = PizzaServletPage.go(driver);
		page = page.addPizza(pizza);

		page = page.removePizza(pizza);

		assertThat(page.readPizzas(), not(contains(pizza)));
	}
}
