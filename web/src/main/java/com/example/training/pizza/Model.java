package com.example.training.pizza;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record Model(
	List<String> languages,
	String language,
	List<Size> sizes,
	List<Crust> crusts,
	List<Sauce> sauces,
	List<Topping> toppings,
	Order order
) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public Model {
		requireNonNull(languages);
		requireNonNull(language);
		requireNonNull(sizes);
		requireNonNull(crusts);
		requireNonNull(sauces);
		requireNonNull(toppings);
		requireNonNull(order);
	}

}
