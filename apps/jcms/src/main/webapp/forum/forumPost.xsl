<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : forumPost.xsl
    Created on : July 11, 2004, 3:46 PM
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
                <title>forumPost.xsl</title>
            </head>
            <body>
	    <xsl:for-each select="jcms_xml_root/forumPost">
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
    

    <h3>First Post: <xsl:value-of select="@title" /></h3>
    <xsl:apply-templates select="forumPost" />
    </xsl:template>
    
    <xsl:template match="forumPost">
    <h3>Reply Post: <xsl:value-of select="@title" /></h3>
    <xsl:apply-templates />
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
