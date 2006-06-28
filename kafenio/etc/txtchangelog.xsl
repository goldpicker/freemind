<?xml version="1.0" encoding="ISO-8859-1"?>

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>

	<xsl:param name="title"/>

	<xsl:output method="text" indent="no"/>

	<xsl:template match="*">
		<xsl:copy>
			<xsl:copy-of select="attribute::*[. != '']"/>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="changelog">
		<xsl:value-of select="$title"/>
		<xsl:text>
===========================================================
</xsl:text>
		<xsl:apply-templates select=".//entry">
			<xsl:sort select="date" data-type="text" order="descending"/>
			<xsl:sort select="time" data-type="text" order="descending"/>
		</xsl:apply-templates>
	</xsl:template>
  
	<xsl:template match="entry">
		<xsl:text>
-----------------------------------------------------------
</xsl:text>
		<xsl:value-of select="date"/>
		<xsl:text> by </xsl:text>
		<xsl:value-of select="author"/>
		<xsl:text>: 
</xsl:text>
		<xsl:apply-templates select="msg"/>
		<xsl:text>
</xsl:text>
	</xsl:template>

	<xsl:template match="date">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="time">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="author">
		<xsl:value-of select="."/>
	</xsl:template>

	<xsl:template match="msg">
		<xsl:apply-templates/>
	</xsl:template>
  
</xsl:stylesheet>
