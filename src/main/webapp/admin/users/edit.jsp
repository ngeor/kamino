<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="org.jcms.model.User"%>
<jsp:useBean id="userFactory" class="org.jcms.dbfactory.UserFactory" scope="page" />
<%
User user = userFactory.selectOne( Integer.parseInt( request.getParameter("id") ) );
%>

<html>
<head><title>JSP Page</title></head>
<body>


<form method="post" action="edit_do.jsp">
<input type="hidden" name="id" value="<%= user.getId() %>">
<table>
    <tr>
	<td>Email</td>
	<td><input name="email" value="<%= user.getEmail() %>"></td>
    </tr>
    <tr>
	<td>Password</td>
	<td><input name="password" value="<%= user.getPassword() %>"></td>
    </tr>
    <tr>
	<td>Lastname</td>
	<td><input name="lastname" value="<%= user.getLastname() %>"></td>
    </tr>
    <tr>
	<td>Firstname</td>
	<td><input name="firstname" value="<%= user.getFirstname() %>"></td>
    </tr>
    <tr>
	<td>Nickname</td>
	<td><input name="nickname" value="<%= user.getNickname() %>"></td>
    </tr>
    <tr>
	<td>Active</td>
	<td><input type="checkbox" name="active" <% if (user.isActive()) { %>checked<% }%>></td>
    </tr>    
    <tr>
	<td colspan="2" align="right"><input type="submit"></td>
    </tr>
</table>
</form>

</body>
</html>
