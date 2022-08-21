package com.example.training.demo;

import static java.util.Objects.requireNonNull;
import static java.util.Objects.requireNonNullElse;

import java.io.IOException;
import java.io.Serial;
import java.util.Set;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class TodoJspServlet extends HttpServlet {

	@Serial
	private static final long serialVersionUID = 1L;
	private static final String URLPATTERN = "/TodoJspServlet";
	private static final String JSP = "/WEB-INF/jsp/todo.jsp";

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
		TodoList todoList = (TodoList) session.getAttribute("todoList");
		if (todoList == null) {
			todoList = new TodoList();
			session.setAttribute("todoList", todoList);
		}

		request.setAttribute("todoList", todoList);
		forwardToJsp(request, response);
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		HttpSession session = request.getSession(false);
		if (session == null) {
			redirectToGet(request, response);
			return;
		}
		TodoList todoList = (TodoList) session.getAttribute("todoList");
		if (todoList == null) {
			redirectToGet(request, response);
			return;
		}
		String button = request.getParameter("button");
		if (button == null) {
			redirectToGet(request, response);
			return;
		}
		switch (button) {
			case "add" -> handleAdd(request, todoList);
			case "remove" -> handleRemove(request, todoList);
			case "save" -> handleSave(request, todoList);
			default -> throw new IllegalArgumentException("Unknown button '" + button + "'");
		}
		session.setAttribute("todoList", todoList);
		redirectToGet(request, response);
	}

	private void handleAdd(HttpServletRequest request, TodoList todoList) {
		requireNonNull(request);
		requireNonNull(todoList);
		final String label = request.getParameter("label");
		todoList.add(new TodoItem(label));
	}

	private static void handleRemove(HttpServletRequest request, TodoList todoList) {
		requireNonNull(request);
		requireNonNull(todoList);
		final String idString = request.getParameter("id");
		final UUID id = UUID.fromString(requireNonNull(idString));
		todoList.removeById(id);
	}

	private static void handleSave(HttpServletRequest request, TodoList todoList) {
		requireNonNull(request);
		requireNonNull(todoList);
		final String[] checkboxes = requireNonNullElse(request.getParameterValues("__checkbox_done"), new String[0]);
		final Set<String> checkedCheckboxes = Set.of(requireNonNullElse(request.getParameterValues("done"), new String[0]));
		for (String checkbox : checkboxes) {
			final UUID id = UUID.fromString(checkbox);
			final boolean done = checkedCheckboxes.contains(checkbox);
			todoList.setDoneById(id, done);
		}
	}

}
