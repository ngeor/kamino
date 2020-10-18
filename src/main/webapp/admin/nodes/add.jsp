<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="org.jcms.model.*" %>
<%@page import="org.jcms.dbfactory.*" %>
<%
int parent = Integer.parseInt( request.getParameter("parent") );
java.util.Enumeration nodeTypeNames = Configuration.getInstance().nodeTypeNames();
%>
<html>
<head><title>JSP Page</title></head>
<body>

<form method="post" action="Add">
<input type="hidden" name="parent" value="<%= parent %>">
<table>
    <tr>
	<td>Type</td>
	<td><select name="type">
	<% while (nodeTypeNames.hasMoreElements()) {
	    String nodeTypeName = (String) nodeTypeNames.nextElement();
 %>
	    <option value="<%= nodeTypeName %>"><%= nodeTypeName %></option>
	<% } %>
	</select></td>
    </tr>
    <tr>
	<td>Title</td>
	<td><input name="title"></td>
    </tr>
    <tr>
	<td colspan="2" align="right"><input type="submit"></td>
    </tr>
</table>
</form>
</body>
</html>
