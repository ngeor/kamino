<%@page contentType="text/xml"%>
<%@page pageEncoding="UTF-8"%>
<%@page import="org.jcms.model.*"%>
<%@page import="org.jcms.dbfactory.*"%>
<%@page import="org.jcms.xml.*"%>
<%
UserFactory userFactory = new UserFactory();
User[] users = userFactory.selectAll(null);
UserWriter xmlWriter = new UserWriter(out);
%>
<users>
<% for (int i = 0; i < users.length; i++) {
    xmlWriter.writeUser(users[i]);
    }
%>
</users>
