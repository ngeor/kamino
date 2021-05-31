<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id="userFactory" class="org.jcms.dbfactory.UserFactory" scope="page" />
<%
int userID = Integer.parseInt(request.getParameter("id"));
%>
<html>
<head><title>JSP Page</title></head>
<body>
<form method="post" action="delete_do.jsp">
    <input type="hidden" name="id" value="<%= userID %>">
    <p>are you sure?</p>
    <input type="submit">
</form>
</body>
</html>
