<%@page import="org.jcms.model.*, org.jcms.dbfactory.*, org.demosite.model.*"%><%
Configuration conf = Configuration.getInstance();
NodeFactory nf = new NodeFactory();
RootNode rootNode = nf.rootNode();
response.sendRedirect("view_node.jsp?id=" + rootNode.getId());
%>
