package com.example.training.pizza;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;

public record Pizza(UUID id, Size size, Crust crust, Sauce sauce, Set<Topping> toppings) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public Pizza(UUID id, Size size, Crust crust, Sauce sauce, Set<Topping> toppings) {
		this.id = requireNonNull(id);
		this.size = requireNonNull(size);
		this.crust = requireNonNull(crust);
		this.sauce = requireNonNull(sauce);
		this.toppings = requireNonNullElse(toppings, Set.of());
	}
}
