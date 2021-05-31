<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<jsp:useBean id="userFactory" class="org.jcms.dbfactory.UserFactory" scope="page" />
<%
int userID = Integer.parseInt(request.getParameter("id"));
userFactory.delete(userID);
%>
<html>
<head><title>JSP Page</title></head>
<body>
<h1>SUCCESS</h1>
<p><a href="index.jsp">Return</a></p>
</body>
</html>