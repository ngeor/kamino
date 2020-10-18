<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<html>
<head><title>JSP Page</title></head>
<body>

<form method="post" action="add_do.jsp">
<table>
    <tr>
	<td>Email</td>
	<td><input name="email"></td>
    </tr>
    <tr>
	<td>Password</td>
	<td><input name="password"></td>
    </tr>
    <tr>
	<td>Lastname</td>
	<td><input name="lastname"></td>
    </tr>
    <tr>
	<td>Firstname</td>
	<td><input name="firstname"></td>
    </tr>
    <tr>
	<td>Nickname</td>
	<td><input name="nickname"></td>
    </tr>
    <tr>
	<td>Active</td>
	<td><input type="checkbox" name="active"></td>
    </tr>    
    <tr>
	<td colspan="2" align="right"><input type="submit"></td>
    </tr>
</table>
</form>

</body>
</html>
