<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="1.0" encoding="utf-8"/>

<xsl:template match="/">

	<xsl:apply-templates select="page"/>

</xsl:template>

<xsl:template match="page">


<html>
<head>
	<title>ICQFriends</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" href="main.css" type="text/css" />
	<meta http-equiv="refresh"><xsl:attribute name="content">0;url=<xsl:value-of select="@url" /></xsl:attribute></meta>
</head>
<body>
<h1>ICQ Contact List Management</h1>
<p><xsl:value-of select="@message" /></p>
<p><a><xsl:attribute name="href"><xsl:value-of select="@url" /></xsl:attribute><xsl:value-of select="@goback" /></a></p>
</body>
</html>

</xsl:template>

</xsl:stylesheet>

