<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<jsp:useBean id="model" scope="request" type="com.example.training.pizza.Model"/>
<fmt:setLocale value="${model.language()}"/>
<!DOCTYPE html>
<html lang="${fn:escapeXml(model.language())}">
<head>
	<meta charset="UTF-8"/>
	<title>PizzaServlet</title>
</head>
<body>
<h1><c:out value="${null}"><fmt:message key="pizza.title"/></c:out></h1>
<p><c:out value="${null}"><fmt:message key="pizza.description"/></c:out></p>

<c:url var="formAction" value="/PizzaServlet"/>

<form method='POST' action='${fn:escapeXml(formAction)}'>
	<p>
		<label for="firstname"><c:out value="${null}"><fmt:message key="customer.firstname"/></c:out></label>
		<input type="text" id="firstname" name="firstname" value="${fn:escapeXml(model.order().firstname())}"
			   autofocus="autofocus"/>
	</p>
	<p>
		<label for="lastname"><c:out value="${null}"><fmt:message key="customer.lastname"/></c:out></label>
		<input type="text" id="lastname" name="lastname" value="${fn:escapeXml(model.order().lastname())}"/>
	</p>
	<p>
		<label for="email"><c:out value="${null}"><fmt:message key="customer.email"/></c:out></label>
		<input type="text" id="email" name="email" value="${fn:escapeXml(model.order().email())}"/>
	</p>

	<fieldset>
		<legend><c:out value="${null}"><fmt:message key="pizza.size"/></c:out></legend>
		<c:forEach var="size" items="${model.sizes()}" varStatus="status">
			<input type="radio" name="size" value="${fn:escapeXml(size.id())}" id="${fn:escapeXml('size_' += status.index)}"
				${status.first ? ' checked="checked"' : ''}
			>
			<label for="${fn:escapeXml('size_' += status.index)}">
				<c:out value="${null}"><fmt:message key="pizza.size.${size.code()}"/></c:out>
			</label>
		</c:forEach>
	</fieldset>

	<fieldset>
		<legend><c:out value="${null}"><fmt:message key="pizza.crust"/></c:out></legend>
		<c:forEach var="crust" items="${model.crusts()}" varStatus="status">
			<input type="radio" name="crust" value="${fn:escapeXml(crust.id())}"
				   id="${fn:escapeXml('crust_' += status.index)}"
				${status.first ? ' checked="checked"' : ''}
			>
			<label for="${fn:escapeXml('crust_' += status.index)}">
				<c:out value="${null}"><fmt:message key="pizza.crust.${crust.code()}"/></c:out>
			</label>
		</c:forEach>
	</fieldset>

	<fieldset>
		<legend><c:out value="${null}"><fmt:message key="pizza.sauce"/></c:out></legend>
		<c:forEach var="sauce" items="${model.sauces()}" varStatus="status">
			<input type="radio" name="sauce" value="${fn:escapeXml(sauce.id())}"
				   id="${fn:escapeXml('sauce_' += status.index)}"
				${status.first ? ' checked="checked"' : ''}
			>
			<label for="${fn:escapeXml('sauce_' += status.index)}">
				<c:out value="${null}"><fmt:message key="pizza.sauce.${sauce.code()}"/></c:out>
			</label>
		</c:forEach>
	</fieldset>

	<fieldset>
		<legend><c:out value="${null}"><fmt:message key="pizza.topping"/></c:out></legend>
		<c:forEach var="topping" items="${model.toppings()}" varStatus="status">
			<input type='hidden' name='__checkbox_toppings' value='${fn:escapeXml(topping.id())}'/>
			<input type='checkbox' name='toppings' value='${fn:escapeXml(topping.id())}'
				   id='${fn:escapeXml('toppings_' += status.index)}'/>
			<label for="${fn:escapeXml('toppings_' += status.index)}">
				<c:out value="${null}"><fmt:message key="pizza.topping.${topping.code()}"/></c:out>
			</label>
		</c:forEach>
	</fieldset>

	<p>
		<button type='submit' name='button' id='button_add' value='add'>
			<c:out value="${null}"><fmt:message key="add"/></c:out>
		</button>
	</p>

	<c:choose>
		<c:when test="${empty model.order().pizzas()}">
			<p id='items_empty'>The list is empty.</p>
		</c:when>
		<c:otherwise>
			<table id='items'>
				<thead>
					<tr>
						<th><c:out value="${null}"><fmt:message key="pizza.size"/></c:out></th>
						<th><c:out value="${null}"><fmt:message key="pizza.crust"/></c:out></th>
						<th><c:out value="${null}"><fmt:message key="pizza.sauce"/></c:out></th>
						<th><c:out value="${null}"><fmt:message key="pizza.topping"/></c:out></th>
                        <th><c:out value="${null}"><fmt:message key="remove"/></c:out></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach var="pizza" items="${model.order().pizzas()}" varStatus="status">
						<tr>
							<td><c:out value="${null}"><fmt:message key="pizza.size.${pizza.size().code()}"/></c:out></td>
							<td><c:out value="${null}"><fmt:message key="pizza.crust.${pizza.crust().code()}"/></c:out></td>
							<td><c:out value="${null}"><fmt:message key="pizza.sauce.${pizza.sauce().code()}"/></c:out></td>
							<td>
								<ul>
									<c:forEach var="item" items="${pizza.toppings()}">
										<li>
											<c:out value="${null}"><fmt:message key="pizza.topping.${item.code()}"/></c:out>
										</li>
									</c:forEach>
								</ul>
							</td>
							<td>
								<c:url var="formActionRemove" value="/PizzaServlet?button=remove"/>
								<button type='submit'
										name='pizzaId' value="${fn:escapeXml(pizza.id())}"
										formaction='${fn:escapeXml(formActionRemove)}'
										id='${fn:escapeXml('button_remove_' += status.index)}'>
									Remove
								</button>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>

			<p>
				<button type='submit' name='button' id='button_checkout' value='checkout'>
					<c:out value="${null}"><fmt:message key="checkout"/></c:out>
				</button>
			</p>
		</c:otherwise>
	</c:choose>

	<p>
		<label for="language"><c:out value="${null}"><fmt:message key="language"/></c:out></label>
		<select id="language" name="language">
			<c:forEach var="language" items="${model.languages()}">
				<option value="${fn:escapeXml(language)}" ${model.language() == language ? ' selected="selected"' : ''}>
					<c:out value="${null}"><fmt:message key="language.${language}"/></c:out>
				</option>
			</c:forEach>
		</select>

		<button type='submit' name='button' id='button_choose' value='chooseLanguage'>
			<c:out value="${null}"><fmt:message key="choose"/></c:out>
		</button>
	</p>

	<c:url var="backUrl" value="/"/>
	<p><a href='${fn:escapeXml(backUrl)}'>Go back</a>.</p>
</form>
</body>
</html>
