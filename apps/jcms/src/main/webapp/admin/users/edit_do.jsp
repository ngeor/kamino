<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="org.jcms.model.User"%>
<jsp:useBean id="userFactory" class="org.jcms.dbfactory.UserFactory" scope="page" />
<%
User user = userFactory.selectOne( Integer.parseInt( request.getParameter("id") ) );
pageContext.setAttribute("user", user);
%>
<jsp:setProperty name="user" property="email" />
<jsp:setProperty name="user" property="password" />
<jsp:setProperty name="user" property="lastname" />
<jsp:setProperty name="user" property="firstname" />
<jsp:setProperty name="user" property="nickname" />
<%
user.setActive( "on".equals( request.getParameter("active")) );
userFactory.update( user );
%>

<html>
<head><title>JSP Page</title></head>
<body>
<h1>SUCCESS</h1>
<p><a href="index.jsp">Return</a></p>
</body>
</html>
