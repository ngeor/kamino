<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="org.jcms.model.*" %>
<%@page import="org.jcms.dbfactory.*" %>

<%!
private void writeNode(NodeFactory nodeFactory, JspWriter out, Node rootNode) throws java.io.IOException, java.sql.SQLException {
    Node[] n = nodeFactory.selectChildrenOfType(rootNode, null, null);
    if (n.length <= 0)
	return;
    
    out.print("<ul>");

    
    for (int i = 0; i < n.length; i++) {
	out.print("<li>");
	out.print( n[i].getTitle() );
	out.print(" [");
	out.print( n[i].getType().toString() );
	out.print("]");
	out.print("<a title=\"Insert new node\" href=\"add.jsp?parent=");
	out.print(n[i].getId() );
	out.print("\">[A]</a>");
	out.print("</li>");
	writeNode(nodeFactory, out, n[i]);
    }
    out.print("</ul>");
    
}
%>
<html>
<head><title>JSP Page</title></head>
<body>
<h1><a href="..">JCMS Administration</a> : Node Administration</h1>
<%
NodeFactory nodeFactory = new NodeFactory();
RootNode rootNode = nodeFactory.rootNode();
%>
<p>Root Of Content <a title="Insert new node" href="add.jsp?parent=<%= rootNode.getId() %>">[A]</a></p>
<% writeNode(nodeFactory, out, rootNode); %>
</body>
</html>
