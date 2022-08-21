package com.example.training.demo;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public final class TodoItem implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	private final UUID id;
	private final boolean done;
	private final String label;

	public TodoItem(String label) {
		this(UUID.randomUUID(), false, label);
	}

	public TodoItem(UUID id, boolean done, String label) {
		this.id = id;
		this.done = done;
		this.label = label;
	}

	public UUID getId() {
		return id;
	}

	public boolean isDone() {
		return done;
	}

	public TodoItem withDone(boolean done) {
		if (this.done == done) {
			return this;
		}
		return new TodoItem(id, done, label);
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return "TodoItem{" + "id=" + id + ", done=" + done + ", label=" + label + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TodoItem other = (TodoItem) obj;
		return Objects.equals(this.id, other.id);
	}

}
