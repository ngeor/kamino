<?xml version="1.0" encoding="UTF-8" ?>

<!--
    Document   : index.xsl
    Created on : July 12, 2004, 8:18 PM
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
                <title>index.xsl</title>
            </head>
            <body>
		<h1>Welcome</h1>
		<h2><a href="forum">Forum</a></h2>
		<hr/>
		<p><a href="admin">Administrator interface</a></p>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
