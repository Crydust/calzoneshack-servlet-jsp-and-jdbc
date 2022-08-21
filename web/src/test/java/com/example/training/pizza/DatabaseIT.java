package com.example.training.pizza;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

class DatabaseIT {

	@RegisterExtension
	final TemporaryDatabaseExtension ds = new TemporaryDatabaseExtension();

	Database database;

	@BeforeEach
	void setUp() {
		database = new Database(ds.get());
	}

	@Test
	void shouldReadSizes() {
		List<Size> sizes = database.readSizes();

		assertThat(sizes, containsInAnyOrder(
				new Size(id("1"), "25cm"),
				new Size(id("2"), "30cm"),
				new Size(id("3"), "35cm"),
				new Size(id("4"), "40cm")
		));
	}

	@Test
	void shouldFindExistingSize() {
		Optional<Size> size = database.findSize(id("1"));

		assertThat(size.orElse(null), is(new Size(id("1"), "25cm")));
	}

	@Test
	void shouldNotFindNonExistingSize() {
		Optional<Size> size = database.findSize(id("5"));

		assertThat(size.orElse(null), is(nullValue()));
	}

	@Test
	void shouldReadCrusts() {
		List<Crust> crusts = database.readCrusts();

		assertThat(crusts, containsInAnyOrder(
				new Crust(id("1"), "classic"),
				new Crust(id("2"), "italian"),
				new Crust(id("3"), "cheesy-crust")
		));
	}

	@Test
	void shouldFindExistingCrust() {
		Optional<Crust> crust = database.findCrust(id("1"));

		assertThat(crust.orElse(null), is(new Crust(id("1"), "classic")));
	}

	@Test
	void shouldNotFindNonExistingCrust() {
		Optional<Crust> crust = database.findCrust(id("5"));

		assertThat(crust.orElse(null), is(nullValue()));
	}

	@Test
	void shouldReadSauces() {
		List<Sauce> sauces = database.readSauces();

		assertThat(sauces, containsInAnyOrder(
				new Sauce(id("1"), "bbq"),
				new Sauce(id("2"), "white"),
				new Sauce(id("3"), "red")
		));
	}

	@Test
	void shouldFindExistingSauce() {
		Optional<Sauce> sauce = database.findSauce(id("1"));

		assertThat(sauce.orElse(null), is(new Sauce(id("1"), "bbq")));
	}

	@Test
	void shouldNotFindNonExistingSauce() {
		Optional<Sauce> sauce = database.findSauce(id("5"));

		assertThat(sauce.orElse(null), is(nullValue()));
	}

	@Test
	void shouldReadToppings() {
		List<Topping> toppings = database.readToppings();

		assertThat(toppings, hasItems(
				new Topping(id("1"), "pineapple"),
				new Topping(id("2"), "bacon"),
				new Topping(id("3"), "barbecue-swirl"),
				// ...
				new Topping(id("27"), "vegan-cheese")
		));
	}

	@Test
	void shouldFindExistingToppings() {
		Set<Topping> toppings = database.findToppings(Set.of(
				id("1"),
				id("2"),
				id("3")
		));

		assertThat(toppings, containsInAnyOrder(
				new Topping(id("1"), "pineapple"),
				new Topping(id("2"), "bacon"),
				new Topping(id("3"), "barbecue-swirl")
		));
	}

	@Test
	void shouldNotFindNonExistingToppings() {
		Set<Topping> toppings = database.findToppings(Set.of(
				id("61"),
				id("62"),
				id("63")
		));

		assertThat(toppings, is(empty()));
	}

	@Test
	void shouldReadOrders() {
		List<Order> orders = database.readOrders();

		assertThat(orders, contains(new Order(
				id("1"),
				"Kristof",
				"Neirynck",
				"kristof@example.com",
				Set.of(new Pizza(
						id("1"),
						new Size(id("3"), "35cm"),
						new Crust(id("2"), "italian"),
						new Sauce(id("3"), "red"),
						Set.of(
								new Topping(id("1"), "pineapple"),
								new Topping(id("4"), "mushrooms"),
								new Topping(id("5"), "mozzarella")
						)
				))
		)));
	}

	@Test
	void shouldSaveOrderWithZeroPizzas() {
		Order orderWithZeroPizzas = new Order(
				randomId(),
				"John",
				"Doe",
				"john.doe@example.com",
				Set.of()
		);

		database.saveOrder(orderWithZeroPizzas);

		List<Order> orders = database.readOrders();
		assertThat(orders, hasItem(orderWithZeroPizzas));
	}

	@Test
	void shouldSaveOrderWithOnePizzasWithZeroToppings() {
		Order orderWithOnePizzasWithZeroToppings = new Order(
				randomId(),
				"John",
				"Doe",
				"john.doe@example.com",
				Set.of(new Pizza(
						randomId(),
						new Size(id("3"), "35cm"),
						new Crust(id("2"), "italian"),
						new Sauce(id("3"), "red"),
						Set.of()
				))
		);

		database.saveOrder(orderWithOnePizzasWithZeroToppings);

		List<Order> orders = database.readOrders();
		assertThat(orders, hasItem(orderWithOnePizzasWithZeroToppings));
	}

	@Test
	void shouldSaveOrderWithTwoPizzas() {
		Order orderWithTwoPizzas = new Order(
				randomId(),
				"John",
				"Doe",
				"john.doe@example.com",
				Set.of(new Pizza(
						randomId(),
						new Size(id("3"), "35cm"),
						new Crust(id("2"), "italian"),
						new Sauce(id("3"), "red"),
						Set.of(
								new Topping(id("1"), "pineapple"),
								new Topping(id("4"), "mushrooms"),
								new Topping(id("5"), "mozzarella")
						)
				), new Pizza(
						randomId(),
						new Size(id("1"), "25cm"),
						new Crust(id("1"), "classic"),
						new Sauce(id("1"), "bbq"),
						Set.of(
								new Topping(id("19"), "paprika"),
								new Topping(id("20"), "jalapenos")
						)
				))
		);

		database.saveOrder(orderWithTwoPizzas);

		List<Order> orders = database.readOrders();
		assertThat(orders, hasItem(orderWithTwoPizzas));
	}

	private static UUID randomId() {
		return UUID.randomUUID();
	}

	private static UUID id(String uuid) {
		if (!uuid.contains("-")) {
			return UUID.fromString("0-0-0-0-" + uuid);
		}
		return UUID.fromString(uuid);
	}
}
