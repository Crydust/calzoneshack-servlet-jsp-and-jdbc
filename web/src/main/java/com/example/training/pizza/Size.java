package com.example.training.pizza;

import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public record Size(UUID id, String code) implements Serializable {
	@Serial
	private static final long serialVersionUID = 1L;

	public Size {
		requireNonNull(id);
		requireNonNull(code);
	}


}
