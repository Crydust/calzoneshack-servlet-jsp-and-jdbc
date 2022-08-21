package com.example.training.demo;

import static java.util.Collections.synchronizedSet;
import static java.util.Objects.requireNonNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class TodoList extends AbstractSet<TodoItem> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;
	private final Set<TodoItem> items;

	public TodoList() {
		items = synchronizedSet(new LinkedHashSet<>());
	}

	public void removeById(UUID id) {
		synchronized (items) {
			items.removeIf(item -> id.equals(item.getId()));
		}
	}

	public void setDoneById(UUID id, boolean done) {
		requireNonNull(id);
		synchronized (items) {
			items.stream()
					.filter(item -> id.equals(item.getId()))
					.filter(item -> item.isDone() != done)
					.findFirst()
					.ifPresent(item -> {
						items.remove(item);
						items.add(item.withDone(done));
					});
		}
	}

	@Override
	public boolean add(TodoItem item) {
		requireNonNull(item);
		synchronized (items) {
			return this.items.add(item);
		}
	}

	@Override
	public Iterator<TodoItem> iterator() {
		synchronized (items) {
			return List.copyOf(items).iterator();
		}
	}

	@Override
	public int size() {
		return items.size();
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + Objects.hashCode(this.items);
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
		final TodoList other = (TodoList) obj;
		return Objects.equals(this.items, other.items);
	}

}
