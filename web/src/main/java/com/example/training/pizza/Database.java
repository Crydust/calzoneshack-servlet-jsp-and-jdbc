package com.example.training.pizza;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.sql.DataSource;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@SuppressWarnings({"PMD.CouplingBetweenObjects"})
public final class Database {
	public final DataSource ds;

	public Database(DataSource ds) {
		this.ds = ds;
	}

	private Order parseOrder(String json) {
		try {
			return ObjectMapperHolder.OBJECT_MAPPER.readValue(json, Order.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Size> readSizes() {
		final List<Size> sizes = new ArrayList<>();
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from size");
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				sizes.add(new Size(rs.getObject("id", UUID.class), rs.getString("code")));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return sizes;
	}

	public Optional<Size> findSize(UUID id) {
		requireNonNull(id);
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from size where id = ?")) {
			ps.setObject(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new Size(rs.getObject("id", UUID.class), rs.getString("code")));
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Crust> readCrusts() {
		final List<Crust> crusts = new ArrayList<>();
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from crust");
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				crusts.add(new Crust(rs.getObject("id", UUID.class), rs.getString("code")));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return crusts;
	}

	public Optional<Crust> findCrust(UUID id) {
		requireNonNull(id);
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from crust where id = ?")) {
			ps.setObject(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new Crust(rs.getObject("id", UUID.class), rs.getString("code")));
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Sauce> readSauces() {
		final List<Sauce> sauces = new ArrayList<>();
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from sauce");
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				sauces.add(new Sauce(rs.getObject("id", UUID.class), rs.getString("code")));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return sauces;
	}

	public Optional<Sauce> findSauce(UUID id) {
		requireNonNull(id);
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from sauce where id = ?")) {
			ps.setObject(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					return Optional.of(new Sauce(rs.getObject("id", UUID.class), rs.getString("code")));
				} else {
					return Optional.empty();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Topping> readToppings() {
		final List<Topping> toppings = new ArrayList<>();
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("select id, code from topping");
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				toppings.add(new Topping(rs.getObject("id", UUID.class), rs.getString("code")));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return toppings;
	}

	public Set<Topping> findToppings(Collection<UUID> ids) {
		requireNonNull(ids);
		if (ids.isEmpty()) {
			return Set.of();
		}
		final Set<Topping> toppings = new LinkedHashSet<>();
		final String questionMarks = ids.stream()
				.map(it -> "?")
				.collect(joining(", "));
		final String sql = "select id, code from topping where id in (" +
				questionMarks +
				")";
		//noinspection SqlSourceToSinkFlow
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement(sql)) {
			int parameterIndex = 1;
			for (UUID id : ids) {
				ps.setObject(parameterIndex, id);
				parameterIndex++;
			}
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					toppings.add(new Topping(rs.getObject("id", UUID.class), rs.getString("code")));
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return toppings;
	}

	@Deprecated
	public List<Order> readOrdersWithoutJson() {

		final Map<UUID, Size> sizeMap = readSizes().stream().collect(toMap(Size::id, identity()));
		final Map<UUID, Crust> crustMap = readCrusts().stream().collect(toMap(Crust::id, identity()));
		final Map<UUID, Sauce> sauceMap = readSauces().stream().collect(toMap(Sauce::id, identity()));
		final Map<UUID, Topping> toppingMap = readToppings().stream().collect(toMap(Topping::id, identity()));

		final List<Order> orders = new ArrayList<>();
		try (Connection con = ds.getConnection()) {

			final Map<UUID, Set<Topping>> pizzaToppings = new HashMap<>();
			try (PreparedStatement ps = con.prepareStatement("select pizza_id, topping_id from pizza_topping");
				 ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					pizzaToppings.computeIfAbsent(
							rs.getObject("pizza_id", UUID.class),
							(pizzaId) -> new LinkedHashSet<>()
					).add(toppingMap.get(rs.getObject("topping_id", UUID.class)));
				}
			}

			final Map<UUID, Set<Pizza>> orderPizzas = new HashMap<>();
			try (PreparedStatement ps = con.prepareStatement("select id, order_id, size_id, crust_id, sauce_id from pizza");
				 ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					final UUID pizzaId = rs.getObject("id", UUID.class);
					orderPizzas.computeIfAbsent(
							rs.getObject("order_id", UUID.class),
							(orderId) -> new LinkedHashSet<>()
					).add(new Pizza(
							pizzaId,
							sizeMap.get(rs.getObject("size_id", UUID.class)),
							crustMap.get(rs.getObject("crust_id", UUID.class)),
							sauceMap.get(rs.getObject("sauce_id", UUID.class)),
							pizzaToppings.getOrDefault(pizzaId, Set.of())
					));
				}
			}

			try (PreparedStatement ps = con.prepareStatement("select id, firstname, lastname, email from \"ORDER\"");
				 ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					final UUID orderId = rs.getObject("id", UUID.class);
					orders.add(new Order(
							orderId,
							rs.getString("firstname"),
							rs.getString("lastname"),
							rs.getString("email"),
							orderPizzas.getOrDefault(orderId, Set.of())
					));
				}
			}

		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		return orders;
	}

	public List<Order> readOrders() {
		final List<Order> orders = new ArrayList<>();
		try (Connection con = ds.getConnection();
			 PreparedStatement ps = con.prepareStatement("""
					 select JSON_OBJECT(
					 	'id': id,
					 	'firstname': firstname,
					 	'lastname': lastname,
					 	'email': email,
					 	'pizzas':
					 		select JSON_ARRAYAGG(JSON_OBJECT(
					 			'id': id,
					 			'size':
					 				select JSON_OBJECT('id': id, 'code': code)
					 				from size
					 				where size.id = size_id,
					 			'crust':
					 				select JSON_OBJECT('id': id, 'code': code)
					 				from crust
					 				where crust.id = crust_id,
					 			'sauce':
					 				select JSON_OBJECT('id': id, 'code': code)
					 				from sauce
					 				where sauce.id = sauce_id,
					 			'toppings':
					 			 	select JSON_ARRAYAGG(JSON_OBJECT(
					 					'id': topping.id,
					 					'code': topping.code
					 			 	))
					 			 	from topping
					 			 	where id in (
					 					select topping_id
					 					from pizza_topping
					 					where pizza_id = pizza.id
					 			 	)
					 		))
					 		from pizza
					 		where order_id = "ORDER".id
					 		group by order_id
					 )
					 from "ORDER"
					 """);
			 ResultSet rs = ps.executeQuery()) {
			while (rs.next()) {
				orders.add(parseOrder(rs.getString(1)));
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		return orders;
	}

	public void saveOrder(Order order) {
		requireNonNull(order);
		try (Connection con = ds.getConnection()) {
			con.setAutoCommit(false);
			try (PreparedStatement ps = con.prepareStatement("insert into \"ORDER\" (id, firstname, lastname, email)\n" +
					"values (?, ?, ?, ?)")) {
				ps.setObject(1, order.id());
				ps.setString(2, order.firstname());
				ps.setString(3, order.lastname());
				ps.setString(4, order.email());
				ps.executeUpdate();
			}

			try (PreparedStatement ps = con.prepareStatement("insert into pizza (id, order_id, size_id, crust_id, sauce_id)\n" +
					"values (?, ?, ?, ?, ?)")) {
				for (Pizza pizza : order.pizzas()) {
					ps.setObject(1, pizza.id());
					ps.setObject(2, order.id());
					ps.setObject(3, pizza.size().id());
					ps.setObject(4, pizza.crust().id());
					ps.setObject(5, pizza.sauce().id());
					ps.addBatch();
				}
				ps.executeBatch();
			}

			try (PreparedStatement ps = con.prepareStatement("insert into pizza_topping (id, pizza_id, topping_id)\n" +
					"values (?, ?, ?)")) {
				for (Pizza pizza : order.pizzas()) {
					for (Topping topping : pizza.toppings()) {
						ps.setObject(1, UUID.randomUUID());
						ps.setObject(2, pizza.id());
						ps.setObject(3, topping.id());
						ps.addBatch();
					}
				}
				ps.executeBatch();
			}

			con.commit();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	private static final class ObjectMapperHolder {
		private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

		private ObjectMapperHolder() {
			throw new UnsupportedOperationException();
		}
	}

}
