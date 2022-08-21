package com.example.training.pizza;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toSet;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

public record Order(
	UUID id,
	String firstname,
	String lastname,
	String email,
	Set<Pizza> pizzas
) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public Order(UUID id, String firstname, String lastname, String email, Set<Pizza> pizzas) {
		this.id = requireNonNull(id);
		this.firstname = requireNonNull(firstname);
		this.lastname = requireNonNull(lastname);
		this.email = requireNonNull(email);
		this.pizzas = requireNonNullElse(pizzas, Set.of());
	}

	public Pizza getPizzaById(UUID pizzaId) {
		requireNonNull(pizzaId);
		return pizzas.stream()
				.filter(pizza -> pizzaId.equals(pizza.id()))
				.findFirst()
				.orElseThrow();
	}

	public Order withFirstname(String firstname) {
		requireNonNull(firstname);
		return new Order(
			this.id,
			firstname,
			this.lastname,
			this.email,
			this.pizzas
		);
	}

	public Order withLastname(String lastname) {
		requireNonNull(lastname);
		return new Order(
			this.id,
			this.firstname,
			lastname,
			this.email,
			this.pizzas
		);
	}

	public Order withEmail(String email) {
		requireNonNull(email);
		return new Order(
			this.id,
			this.firstname,
			this.lastname,
			email,
			this.pizzas
		);
	}

	public Order withPizza(Pizza pizza) {
		requireNonNull(pizza);
		return new Order(
			this.id,
			this.firstname,
			this.lastname,
			this.email,
			Stream.concat(this.pizzas.stream(), Stream.of(pizza)).collect(toSet())
		);
	}

	public Order withoutPizza(Pizza pizza) {
		requireNonNull(pizza);
		return new Order(
				this.id,
				this.firstname,
				this.lastname,
				this.email,
				this.pizzas.stream().filter(not(pizza::equals)).collect(toSet())
		);
	}
}
