package com.example.training.pizza;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public record PizzaOrdersModel(String language, List<Order> orders) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;


}
