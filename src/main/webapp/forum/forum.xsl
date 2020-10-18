<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : forumGroup.xsl
    Created on : July 11, 2004, 2:33 PM
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
            <head>
                <title>forum</title>
            </head>
            <body>
	    <xsl:apply-templates />
            </body>
        </html>
    </xsl:template>

    <xsl:template match="forum">
    <h1><xsl:value-of select="@title" /></h1>
    <!-- path -->
    <h2><xsl:apply-templates select="parent" /></h2>

    <table width="100%" border="1">
	<tr>
	    <th>Topic</th>
	    <th>Poster</th>
	</tr>
	<xsl:apply-templates select="forumPost" />
    </table>
    </xsl:template>
    
    <xsl:template match="forumPost">
	<tr>
	    <td><a><xsl:attribute name="href">view_node.jsp?id=<xsl:value-of select="@id" /></xsl:attribute><xsl:value-of select="@title" /></a></td>
	    <td></td>
	</tr>
    </xsl:template>
    
    <xsl:template match="parent">
	<xsl:for-each select="*">
	    <xsl:apply-templates select="parent" />
	    <a><xsl:attribute name="href">view_node.jsp?id=<xsl:value-of select="@id" /></xsl:attribute><xsl:value-of select="@title" /></a>
	</xsl:for-each>
	<xsl:if test="ancestor::parent"> &gt; </xsl:if>
    </xsl:template>

    <xsl:template match="jcms_time">
	<p><font size="1"><xsl:value-of select="." /></font></p>
    </xsl:template>    
    
</xsl:stylesheet>
