<?xml version="1.0" encoding="utf-8"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

<xsl:output method="html" version="1.0" encoding="utf-8"/>

<xsl:template match="/">

	<xsl:apply-templates select="icqfriend"/>

</xsl:template>

<xsl:template match="icqfriend">
<html>
<head>
	<title>Διαγραφή Επαφής</title>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<link rel="stylesheet" type="text/css" href="main.css" />
</head>
<body>
<form action="delete.jsp" method="post">
<input type="hidden" name="action" value="delete" />
<input type="hidden" name="uin">
<xsl:attribute name="value"><xsl:value-of select="@uin" /></xsl:attribute>
</input>
<p>Θέλετε σίγουρα να διαγραφεί η παρακάτω επαφή;</p>
<table align="center" border="0" bordercolor="0" cellspacing="0" cellpadding="4">
	<tr>
		<td>Nick name</td>
		<td><xsl:value-of select="@nickname" /></td>
	</tr>
	<tr>
		<td>UIN</td>
		<td><xsl:value-of select="@uin" /></td>
	</tr>
	<tr><td colspan="2" align="right"><input type="submit" value="Διαγραφή" />
	<input type="button" value="Ακύρωση" onclick="history.go(-1);"/> </td> </tr>

</table>
</form>

</body>
</html>

</xsl:template>

</xsl:stylesheet>
