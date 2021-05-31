<%@ page contentType='text/html; charset=utf-8' %>

<%
	net.ngeor.icqfriends.Utility.transformFile(getServletContext().getRealPath("/WEB-INF/icqdata.xml"),
		response.getWriter(),
		getServletContext().getRealPath("/WEB-INF/xsl/index.xsl")
	);
%>

