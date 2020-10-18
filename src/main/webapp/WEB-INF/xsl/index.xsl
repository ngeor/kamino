<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="1.0" encoding="utf-8"/>

<xsl:template match="/">

	<xsl:apply-templates select="icqfriends"/>

</xsl:template>

<xsl:template match="icqfriends">


<html>
<head>
	<title>ICQFriends</title>
	<link rel="stylesheet" href="main.css" type="text/css" />
</head>
<body>
<h1>ICQ Contact List Management</h1>

<form action="addcontact.jsp" method="post">
<p><img src="images/add.png" />
Nickname <input name="nickname" /> UIN <input name="uin" /> <input type="submit" value="Προσθήκη Επαφής" /></p>
</form>

<table border="0" bordercolor="0" cellspacing="0" cellpadding="0">
<tr>
<td class="bottomBorder">
<table border="0" bordercolor="0" cellspacing="0" cellpadding="4">
	<tr>
		<th class="leftBorder">Nick name</th>
		<th class="leftBorder">UIN</th>
		<th class="leftrightBorder">Εντολές</th>
	</tr>

	<!-- EDO TELIONOUN TA STATHERA DEDOMENA TOY PINAKA -->

	<xsl:for-each select="icqfriend">
	<xsl:sort select="@nickname"/>
	<xsl:sort select="@uin"/>
		<tr>
			<td class="leftBorder"><xsl:value-of select="@nickname"/></td>
			<td class="leftborder"><xsl:value-of select="@uin"/></td>
			<td class="leftrightBorder"><a>
			<xsl:attribute name="href">edit.jsp?uin=<xsl:value-of select="@uin"/>
			</xsl:attribute><img border="0" src="images/edit.png" alt="Επεξεργασία" /></a> |
			<a>
			<xsl:attribute name="href">delete.jsp?uin=<xsl:value-of select="@uin"/>
			</xsl:attribute><img border="0" src="images/delete.png" alt="Διαγραφή" /></a></td>
		</tr>
	</xsl:for-each>

</table>
</td>
</tr>
</table>
</body>
</html>

</xsl:template>

</xsl:stylesheet>

