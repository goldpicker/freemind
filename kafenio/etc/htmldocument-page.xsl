<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>

	<xsl:output method="html" indent="yes" encoding="US-ASCII"
		doctype-public="-//W3C//DTD HTML 4.01//EN"
		doctype-system="http://www.w3.org/TR/html401/strict.dtd"/>

		<xsl:template match="*">
			<xsl:copy>
				<xsl:apply-templates/>
			</xsl:copy>
		</xsl:template>

		<xsl:template match="documentation-page">
			<html>
				<head>
					<title><xsl:value-of select="title"/></title>
					<style type="text/css">
						body, p {
							font-family: Verdana, Arial, Helvetica, sans-serif;
							font-size: 12px;
							color: #000000;
							background-color: #ffffff;
						}
						tr, td {
							font-family: Verdana, Arial, Helvetica, sans-serif;
							background: #eeeee0;
						}
						td {
							padding-left: 20px;
						}
						.section-headline {
							font-family: Verdana, Arial, Helvetica, sans-serif;
							font-weight: bold;
							font-size: 16px;
							text-align: left;
							background: #00557C;
							color: #FFFFFF;
							padding-left: 3px;
						}
						.content-headline {
							font-family: Verdana, Arial, Helvetica, sans-serif;
							font-weight: bold;
							font-size: 14px;
							text-align: left;
							background: #EEEEEE;
							padding-left: 3px;
						}
						.section-content {
							font-family: Verdana, Arial, Helvetica, sans-serif;
							font-weight: normal;
							font-size: 12px;
							text-align: left;
							background: #FFFFFF;
							padding-left: 3px;
						}
						a {
							color: #000000;
						}
						b {
							font-weight: bold;
						}
					</style>
				</head>
				<body>
					<h1><a name="top"><xsl:value-of select="title"/></a></h1>
					<hr/>
					<table border="0" width="100%" cellspacing="1">
						<xsl:apply-templates select=".//section"/>
					</table>
				</body>
			</html>
		</xsl:template>
  
		<xsl:template match="section">
			<table border="0" width="100%" cellspacing="1">
				<tr>
					<td class="section-headline">
						<xsl:apply-templates select="headline"/>
					</td>
				</tr>
				<tr>
					<td class="section-content">
						<table border="0" width="100%" cellspacing="1">
							<xsl:apply-templates select="parameters"/>
						</table>
					</td>
				</tr>
			</table><BR/><BR/>
		</xsl:template>

		<xsl:template match="parameters">
			<xsl:for-each select=".">
				<xsl:apply-templates select="parameter-description"/>
				<xsl:apply-templates select="parameter">
		            <xsl:sort select="name" data-type="text" order="ascending"/>
				</xsl:apply-templates>
			</xsl:for-each>
		</xsl:template>
		
		<xsl:template match="parameter">
			<tr>
				<xsl:apply-templates/>
			</tr>
		</xsl:template>

		<xsl:template match="parameter-description">
			<tr>
				<xsl:for-each select="item">
					<td class="content-headline">
						<xsl:apply-templates/>
					</td>
				</xsl:for-each>
			</tr>
		</xsl:template>

		<xsl:template match="headline">
			<xsl:value-of select="."/>
		</xsl:template>

		<xsl:template match="name">
			<TD class="section-content">
				<B><xsl:value-of select="."/></B>
			</TD>
		</xsl:template>

		<xsl:template match="type">
			<TD class="section-content">
				<i><xsl:value-of select="."/></i>
			</TD>
		</xsl:template>

		<xsl:template match="description">
			<TD class="section-content">
				<xsl:value-of select="."/>
			</TD>
		</xsl:template>

</xsl:stylesheet>
