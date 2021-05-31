<%@page contentType="text/xml"%><%@page pageEncoding="UTF-8"%><%@page import="org.jcms.model.*, org.jcms.dbfactory.*, org.demosite.model.*"%><?xml version="1.0" encoding="UTF-8"?>
<% long time = System.currentTimeMillis(); %>
<%
Configuration conf = Configuration.getInstance();
NodeFactory nf = new NodeFactory();
Node rootNode = nf.selectOne( Integer.parseInt(request.getParameter("id")) );
rootNode.setChildren( nf.selectChildrenRecursive( rootNode ) );
rootNode.setParents( nf.selectParentsRecursive( rootNode ) );
%>
<?xml-stylesheet href="<%= rootNode.getType() %>.xsl" type="text/xsl"?>
<jcms_xml_root>
<%
org.jcms.xml.NodeWriter xmlWriter = new org.jcms.xml.NodeWriter(out);
xmlWriter.writeNode(rootNode);
%>
<% time = System.currentTimeMillis() - time; %>
<jcms_time><%= time %></jcms_time>
</jcms_xml_root>