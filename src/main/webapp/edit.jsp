<%@ page contentType='text/html; charset=utf-8' %>
<%
String xmlOut;
String action = request.getParameter("action");
if ("edit".equals(action))
{
	net.ngeor.icqfriends.Utility.updateUIN(getServletContext().getRealPath("/WEB-INF/icqdata.xml"),
		request.getParameter("originaluin"),
		request.getParameter("uin"),
		request.getParameter("nickname")
	);
	net.ngeor.icqfriends.Utility.sendRedirect("index.jsp", "Η ενημέρωση ήταν επιτυχής", "Επιστροφή",
		response.getWriter(), getServletContext().getRealPath("/WEB-INF/xsl/redirect.xsl"));
}
else
{
	String reqUIN = request.getParameter("uin");
	if (reqUIN == null || reqUIN.length() <= 0) {
		response.sendRedirect("error.jsp");
	}

	xmlOut = net.ngeor.icqfriends.Utility.getICQFriendXML(getServletContext().getRealPath("/WEB-INF/icqdata.xml"), reqUIN);

	if (xmlOut != null) {
		net.ngeor.icqfriends.Utility.transformString(xmlOut, response.getWriter(),
			getServletContext().getRealPath("/WEB-INF/xsl/edit.xsl"));
	}
	else
	{
		response.sendRedirect("error.jsp");
	}
}
%>
