<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : index.xsl
    Created on : July 11, 2004, 2:19 PM
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
                <title>Welcome</title>
            </head>
            <body>
	    <xsl:apply-templates />
            </body>
        </html>
    </xsl:template>
    
    <xsl:template match="forums">
    <h1>Forums</h1>
    <xsl:apply-templates />
    </xsl:template>
    
    <xsl:template match="forumGroup">
	<p><a><xsl:attribute name="href">view_node.jsp?id=<xsl:value-of select="@id" /></xsl:attribute><xsl:value-of select="@title" /></a></p>
    </xsl:template>
    
    <xsl:template match="jcms_time">
	<p><font size="1"><xsl:value-of select="." /></font></p>
    </xsl:template>

</xsl:stylesheet>
