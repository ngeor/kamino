<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : index.xsl
    Created on : July 12, 2004, 9:03 PM
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
<h1><a href="..">JCMS Administration</a> : User Administration</h1>
<p><a title="Add" href="add.jsp">[A]</a></p>
<table>
<tr>
    <th>id</th>
    <th>email</th>
    <th>nickname</th>
    <th>lastname</th>
    <th>firstname</th>
    <th>active</th>
    <th>Commands</th>
</tr>
<xsl:apply-templates />

</table>
</body>
</html>
    </xsl:template>

<xsl:template match="user">
    <tr>
    <td><xsl:value-of select="@id" /></td>
    <td><xsl:value-of select="@email" /></td>
    <td><xsl:value-of select="@nickname" /></td>
    <td><xsl:value-of select="@lastname" /></td>
    <td><xsl:value-of select="@firstname" /></td>
    <td><xsl:if test="@active='true'">X</xsl:if></td>
    <td>
    <a><xsl:attribute name="href">edit.jsp?id=<xsl:value-of select="@id" /></xsl:attribute>[E]</a>
    <a><xsl:attribute name="href">delete.jsp?id=<xsl:value-of select="@id" /></xsl:attribute>[D]</a>
    </td>
</tr>
</xsl:template>
</xsl:stylesheet>
