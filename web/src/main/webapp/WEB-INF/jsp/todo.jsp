<%@page contentType="text/html" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<jsp:useBean id="todoList" scope="request"
			 type="java.util.Collection<com.example.training.demo.TodoItem>"/>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8"/>
	<title>TodoJspServlet</title>
</head>
<body>
<h1>TodoJspServlet</h1>
<c:url var="formAction" value="/TodoJspServlet"/>
<c:url var="removeFormAction" value="/TodoJspServlet">
	<c:param name="button" value="remove"/>
</c:url>
<form method='POST' action='${fn:escapeXml(formAction)}'>
	<p>
		<input type='text' name='label' id='label' autofocus='autofocus'/>
		<button type='submit' name='button' id='button_add' value='add'>Add</button>
	</p>
	<c:choose>
		<c:when test="${empty todoList}">
			<p id='items_empty'>The list is empty.</p>
		</c:when>
		<c:otherwise>
			<table id='items'>
				<c:forEach var="item" items="${todoList}">
					<c:set var="checkboxId" value="done[${item.id}]"/>
					<tr>
						<td>
							<input type='hidden' name='__checkbox_done' value='${fn:escapeXml(item.id)}'/>
							<input type='checkbox' name='done' value='${fn:escapeXml(item.id)}'
								   id='${fn:escapeXml(checkboxId)}'${item.done ? ' checked="checked"' : ''}/>
						</td>
						<td><label for='${fn:escapeXml(checkboxId)}'>${fn:escapeXml(item.label)}</label></td>
						<td>
							<button type='submit' name='id' value='${fn:escapeXml(item.id)}'
									formaction='${fn:escapeXml(removeFormAction)}'>
								Remove
							</button>
						</td>
					</tr>
				</c:forEach>
			</table>
			<p>
				<button type='submit' name='button' id='button_save' value='save'>Save</button>
			</p>
		</c:otherwise>
	</c:choose>
	<c:url var="backUrl" value="/"/>
	<p><a href='${fn:escapeXml(backUrl)}'>Go back</a>.</p>
</form>
</body>
</html>
