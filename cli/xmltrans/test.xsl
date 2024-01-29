<?xml version="1.0" encoding="ISO-8859-7"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="1.0" encoding="iso-8859-7"/>
<!--
<xsl:template>
	<xsl:value-of/>
</xsl:template>
-->
<xsl:template match="/">
	<html>
	<xsl:apply-templates select="program"/>
	</html>
</xsl:template>

<xsl:template match="program">
<head>
	<title><xsl:value-of select="@title"/></title>
	<meta HTTP-EQUIV="Content-Type" Content="text/html; charset=iso-8859-7"/>
	<style type="text/css">
		h1 { background-color: #FFCC99; color: #FFFFFF; font-size: 20pt; text-align: center; letter-spacing: 0.5em;
			font-family: "Arial Unicode MS, Verdana" }
		th { background-color: #808080; color: #FFFFFF; font-family: "Verdana"; font-size:10pt }
		td { font-family: "Verdana"; font-size: 10pt }
	</style>
</head>

<body>

<h1><xsl:value-of select="@title"/></h1>

<table align="center" border="0" bordercolor="0" cellspacing="0" cellpadding="4">
	<tr>
		<th>Ημερομηνία</th>
		<th>Ώρα</th>
		<th>Μάθημα</th>
		<th>Αίθουσες</th>
	</tr>
	
	<!-- EDO TELIONOUN TA STATHERA DEDOMENA TOY PINAKA -->
	
	<xsl:for-each select="lesson">
	<xsl:sort select="date"/>
	<xsl:sort select="time"/>
		<tr>
			<td align="right"><xsl:value-of select="date"/></td>
			<td align="right"><xsl:value-of select="time"/></td>
			<td><xsl:value-of select="name"/></td>
			<td>
				<xsl:for-each select="location">
					<xsl:value-of select="."/><br/>
				</xsl:for-each>
			</td>
		</tr>
	</xsl:for-each>
</table>

</body>
</xsl:template>

</xsl:stylesheet>
