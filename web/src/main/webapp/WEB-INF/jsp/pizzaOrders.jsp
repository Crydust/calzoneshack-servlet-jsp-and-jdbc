<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="model" scope="request" type="com.example.training.pizza.PizzaOrdersModel"/>
<!DOCTYPE html>
<html lang="${fn:escapeXml(model.language())}">
<head>
	<meta charset="UTF-8"/>
	<title>PizzaOrdersServlet</title>
</head>
<body>
<h1>PizzaOrdersServlet</h1>

<c:url var="formAction" value="/PizzaOrdersServlet"/>

<form method='POST' action='${fn:escapeXml(formAction)}'>

	<c:choose>
		<c:when test="${empty model.orders()}">
			<p id='orders_empty'>The list is empty.</p>
		</c:when>
		<c:otherwise>

			<table id='orders'>
				<tr>
					<th><c:out value="${null}"><fmt:message key="customer.firstname"/></c:out></th>
					<th><c:out value="${null}"><fmt:message key="customer.lastname"/></c:out></th>
					<th><c:out value="${null}"><fmt:message key="customer.email"/></c:out></th>
					<th>pizzas</th>
				</tr>
				<c:forEach var="order" items="${model.orders()}">
					<tr>
						<td><c:out value="${order.firstname()}"/></td>
						<td><c:out value="${order.lastname()}"/></td>
						<td><c:out value="${order.email()}"/></td>
						<td>
							<table class="pizzas">
								<tr>
									<th><c:out value="${null}"><fmt:message key="pizza.size"/></c:out></th>
									<th><c:out value="${null}"><fmt:message key="pizza.crust"/></c:out></th>
									<th><c:out value="${null}"><fmt:message key="pizza.sauce"/></c:out></th>
									<th><c:out value="${null}"><fmt:message key="pizza.topping"/></c:out></th>
								</tr>
								<c:forEach var="pizza" items="${order.pizzas()}">
									<tr>
										<td><c:out value="${null}"><fmt:message
												key="pizza.size.${pizza.size().code()}"/></c:out></td>
										<td><c:out value="${null}"><fmt:message
												key="pizza.crust.${pizza.crust().code()}"/></c:out></td>
										<td><c:out value="${null}"><fmt:message
												key="pizza.sauce.${pizza.sauce().code()}"/></c:out></td>
										<td>
											<ul class="toppings">
												<c:forEach var="topping" items="${pizza.toppings()}">
													<li>
														<c:out value="${null}"><fmt:message
																key="pizza.topping.${topping.code()}"/></c:out>
													</li>
												</c:forEach>
											</ul>
										</td>
									</tr>
								</c:forEach>
							</table>
						</td>
					</tr>
				</c:forEach>
			</table>
		</c:otherwise>
	</c:choose>

	<c:url var="backUrl" value="/"/>
	<p><a href='${fn:escapeXml(backUrl)}'>Go back</a>.</p>
</form>
</body>
</html>
