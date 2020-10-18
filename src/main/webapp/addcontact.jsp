<%@ page contentType='text/html; charset=utf-8' %>

<%
String uin, nickname;
uin = request.getParameter("uin");
nickname = request.getParameter("nickname");
if (uin == null || uin.length() <= 0 || nickname == null || nickname.length() <= 0)
{
	response.sendRedirect("error.jsp");
}
else
{

	net.ngeor.icqfriends.Utility.addUIN(
		getServletContext().getRealPath("/WEB-INF/icqdata.xml"),
		request.getParameter("uin"),
		request.getParameter("nickname")
	);

	net.ngeor.icqfriends.Utility.sendRedirect("index.jsp", "Η προσθήκη ολοκληρώθηκε με επιτυχία", "Επιστροφή",
		response.getWriter(), getServletContext().getRealPath("/WEB-INF/xsl/redirect.xsl"));
}
%>