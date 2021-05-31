<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : index.xsl
    Created on : July 12, 2004, 8:58 PM
    Author     : ngeor
    Description:
        Purpose of transformation follows.
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html"/>

    <!-- TODO customize transformation rules 
         syntax recommendation http://www.w3.org/TR/xslt 
    -->
    <xsl:template match="/">
<html>
<head><title>JSP Page</title></head>
<body>

<h1>JCMS Administration</h1>
<p><a href="users">User Administration</a></p>
<p><a href="roles">Role Administration</a></p>
<p><a href="nodes">Node Administration</a></p>
</body>
</html>
    </xsl:template>

</xsl:stylesheet>
