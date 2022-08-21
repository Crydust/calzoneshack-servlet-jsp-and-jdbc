package com.example.training.pizza;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record Topping(UUID id, String code) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public Topping {
		requireNonNull(id);
		requireNonNull(code);
	}


}
