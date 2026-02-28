package com.example.training.pizza;

import java.io.IOException;
import java.io.Serial;
import java.util.List;

import jakarta.annotation.Resource;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

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

}
