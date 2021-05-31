<%@ page contentType='text/html; charset=utf-8' %>
<%
String action = request.getParameter("action");
if ("delete".equals(action)) {
	net.ngeor.icqfriends.Utility.deleteUIN(
		getServletContext().getRealPath("/WEB-INF/icqdata.xml"),
		request.getParameter("uin")
	);
	net.ngeor.icqfriends.Utility.sendRedirect("index.jsp", "Η διαγραφή ήταν επιτυχής", "Επιστροφή",
		response.getWriter(), getServletContext().getRealPath("/WEB-INF/xsl/redirect.xsl"));
}
else
{
	String xmlOut;
	String reqUIN = request.getParameter("uin");
	if (reqUIN == null || reqUIN.length() <= 0) {
		response.sendRedirect("error.jsp");
	}

	xmlOut = net.ngeor.icqfriends.Utility.getICQFriendXML(getServletContext().getRealPath("/WEB-INF/icqdata.xml"), reqUIN);

	if (xmlOut != null) {
		net.ngeor.icqfriends.Utility.transformString(xmlOut, response.getWriter(),
			getServletContext().getRealPath("/WEB-INF/xsl/delete.xsl"));
	}
	else
	{
		response.sendRedirect("error.jsp");
	}
}
%>
