package com.example.training.pizza;

import static java.util.Objects.requireNonNullElse;
import static java.util.stream.Collectors.toSet;

import java.io.IOException;
import java.io.Serial;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import javax.sql.DataSource;

public class PizzaServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;
	private static final String URLPATTERN = "/PizzaServlet";
	private static final String JSP = "/WEB-INF/jsp/pizza.jsp";
	private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "fr", "nl");
	private static final String DEFAULT_LANGUAGE = "en";
	@Resource(name = "jdbc/MyDataSource")
	private DataSource ds;

	private static void forwardToJsp(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher(JSP).forward(request, response);
	}

	private static void redirectToGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + URLPATTERN));
	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html; charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession();

		String language = (String) session.getAttribute("language");
		if (language == null) {
			if (request.getHeader("Accept-Language") != null) {
				language = request.getLocale().getLanguage().toLowerCase(Locale.ROOT);
				if (!SUPPORTED_LANGUAGES.contains(language)) {
					language = DEFAULT_LANGUAGE;
				}
			}
			session.setAttribute("language", language);
		}

		Order order = (Order) session.getAttribute("order");
		if (order == null) {
			order = new Order(UUID.randomUUID(), "", "", "", Set.of());
		}

		final Database database = new Database(ds);
		final List<Size> sizes = database.readSizes();
		final List<Crust> crusts = database.readCrusts();
		final List<Sauce> sauces = database.readSauces();
		final List<Topping> toppings = database.readToppings();
		final Model model = new Model(SUPPORTED_LANGUAGES, language, sizes, crusts, sauces, toppings, order);

		request.setAttribute("model", model);
		forwardToJsp(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			redirectToGet(request, response);
			return;
		}
		String button = request.getParameter("button");
		if (button == null) {
			redirectToGet(request, response);
			return;
		}
		switch (button) {
			case "add" -> handleAdd(request);
			case "remove" -> handleRemove(request);
			case "checkout" -> handleCheckout(request);
			case "chooseLanguage" -> handleChooseLanguage(request);
			default -> throw new IllegalArgumentException("Unknown button '" + button + "'");
		}
		redirectToGet(request, response);
	}

	private void handleAdd(HttpServletRequest request) {
		Order order = (Order) request.getSession().getAttribute("order");
		if (order == null) {
			order = new Order(UUID.randomUUID(), "", "", "", Set.of());
		}
		order = order.withFirstname(request.getParameter("firstname"));
		order = order.withLastname(request.getParameter("lastname"));
		order = order.withEmail(request.getParameter("email"));
		final Database database = new Database(ds);
		final Size size = database.findSize(UUID.fromString(request.getParameter("size"))).orElseThrow();
		final Crust crust = database.findCrust(UUID.fromString(request.getParameter("crust"))).orElseThrow();
		final Sauce sauce = database.findSauce(UUID.fromString(request.getParameter("sauce"))).orElseThrow();
		final Set<Topping> toppings = database.findToppings(Arrays.stream(requireNonNullElse(request.getParameterValues("toppings"), new String[0])).map(UUID::fromString).collect(toSet()));
		Pizza pizza = new Pizza(UUID.randomUUID(), size, crust, sauce, toppings);
		order = order.withPizza(pizza);
		request.getSession().setAttribute("order", order);
	}

	private void handleRemove(HttpServletRequest request) {
		Order order = (Order) request.getSession().getAttribute("order");
		if (order == null) {
			order = new Order(UUID.randomUUID(), "", "", "", Set.of());
		}
		order = order.withFirstname(request.getParameter("firstname"));
		order = order.withLastname(request.getParameter("lastname"));
		order = order.withEmail(request.getParameter("email"));
		UUID pizzaId = UUID.fromString(request.getParameter("pizzaId"));
		Pizza pizzaToRemove = order.getPizzaById(pizzaId);
		order = order.withoutPizza(pizzaToRemove);
		request.getSession().setAttribute("order", order);
	}

	private void handleCheckout(HttpServletRequest request) {
		Order order = (Order) request.getSession().getAttribute("order");
		if (order == null) {
			return;
		}
		order = order.withFirstname(request.getParameter("firstname"));
		order = order.withLastname(request.getParameter("lastname"));
		order = order.withEmail(request.getParameter("email"));

		final Database database = new Database(ds);
		database.saveOrder(order);

		request.getSession().setAttribute("order", new Order(UUID.randomUUID(), "", "", "", Set.of()));
	}

	private void handleChooseLanguage(HttpServletRequest request) {
		final String language = request.getParameter("language");
		if (SUPPORTED_LANGUAGES.contains(language)) {
			request.getSession().setAttribute("language", language);
		}
	}

}
