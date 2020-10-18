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
                <title>forumGroup.xsl</title>
            </head>
            <body>
	    <xsl:for-each select="jcms_xml_root/forumGroup">
		<xsl:call-template name="main" />
	    </xsl:for-each>
	    <xsl:for-each select="jcms_xml_root/jcms_time">
		<xsl:apply-templates select="."/>
    	    </xsl:for-each>
            </body>
        </html>
    </xsl:template>

    <xsl:template name="main">
    <h1><xsl:value-of select="@title" /></h1>
    <!-- path -->
    <h2><xsl:apply-templates select="parent" /></h2>
    <xsl:apply-templates select="forumGroup" />
    <table width="100%" border="1">
	<tr>
	    <th>Forum</th>
	    <th>Last Poster</th>
	    <th>Total posts</th>
	</tr>
    <xsl:apply-templates select="forum" />
    </table>
    </xsl:template>
    
    <xsl:template match="forumGroup">
    <p>Forum Sub Category: <a><xsl:attribute name="href">view_node.jsp?id=<xsl:value-of select="@id" /></xsl:attribute><xsl:value-of select="@title" /></a></p>
    </xsl:template>
    
    <xsl:template match="forum">
	<tr>
	    <td><a><xsl:attribute name="href">view_node.jsp?id=<xsl:value-of select="@id" /></xsl:attribute><xsl:value-of select="@title" /></a></td>
	    <td></td>
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
