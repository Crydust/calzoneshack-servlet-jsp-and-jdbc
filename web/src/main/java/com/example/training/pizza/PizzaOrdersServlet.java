package com.example.training.pizza;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.Serial;
import java.util.List;
import java.util.UUID;

import javax.sql.DataSource;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PizzaOrdersServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;
	private static final String URLPATTERN = "/PizzaOrdersServlet";
	private static final String JSP = "/WEB-INF/jsp/pizzaOrders.jsp";
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

		final Database database = new Database(ds);
		final List<Order> orders = database.readOrders();
		final PizzaOrdersModel model = new PizzaOrdersModel("en", orders);

		request.setAttribute("model", model);
		forwardToJsp(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String button = request.getParameter("button");
		if (button == null) {
			redirectToGet(request, response);
			return;
		}
		switch (button) {
			case "remove" -> handleRemove(request);
			default -> throw new IllegalArgumentException("Unknown button '" + button + "'");
		}
		redirectToGet(request, response);
	}

	private void handleRemove(HttpServletRequest request) {
		requireNonNull(request);
		final String idString = request.getParameter("id");
		final UUID id = UUID.fromString(requireNonNull(idString));
		final Database database = new Database(ds);
		database.removeOrderById(id);
	}

}
