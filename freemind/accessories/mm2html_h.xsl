<?xml version='1.0'?>

<!--
        : This code released under the GPL.
        : (http://www.gnu.org/copyleft/gpl.html)
    Document   : mm2latexarticl.xsl
    Created on : 01 February 2004, 17:17
    Author     : joerg feuerhake joerg.feuerhake@free-penguin.org
    Description: transforms freemind mm format to latex scrartcl, handles crossrefs ignores the rest. feel free to customize it while leaving the ancient authors
                    mentioned. thank you
    Thanks to:  Tayeb.Lemlouma@inrialpes.fr for writing the LaTeX escape scripts and giving inspiration
    ChangeLog:

    See: http://freemind.sourceforge.net/
-->

<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>
<xsl:output method="html"  indent="no" encoding="ISO-8859-1" />
<xsl:key name="refid" match="node" use="@ID" />

<xsl:template match="/">
<xsl:variable name="mapversion" select="map/@version"/>

        <html>&#xA;
            <head>&#xA;
                <title><xsl:value-of select="map/node/@TEXT"/>//mm2html.xsl FreemindVersion:<xsl:value-of select="$mapversion"/></title>&#xA;
                <style>&#xA;
                body{&#xA;
                font-size:10pt;&#xA;
                color:rgb(0,0,0);&#xA;
                backgound-color:rgb(255,255,255);&#xA;
                font-family:sans-serif;&#xA;
                }&#xA;
                p.info{&#xA;
                font-size:8pt;&#xA;
                text-align:right;&#xA;
                color:rgb(127,127,127);&#xA;
                }&#xA;
                </style>&#xA;
            </head>&#xA;
            &#xA;
            <body>&#xA;
            &#xA;
          
            <p>&#xA;
            <xsl:apply-templates/>&#xA;
            </p>&#xA;
              <p class="info">&#xA;
            <xsl:value-of select="map/node/@TEXT"/>//mm2html.xsl FreemindVersion:<xsl:value-of select="$mapversion"/>
            &#xA;
            </p>&#xA;
            </body>&#xA;
        </html>&#xA;
    </xsl:template>
 

<!-- ======= Body ====== -->

<!-- Sections Processing -->
<xsl:template match="node">
<xsl:variable name="target" select="arrowlink/@DESTINATION"/>

<xsl:if test="@ID != ''">
 <a>
 <xsl:attribute name="name">
            	<xsl:value-of select="@ID"/>
            </xsl:attribute>
</a>&#xA;
</xsl:if>

<xsl:if test="(count(ancestor::node())-2)=1">
<h1><xsl:value-of select="@TEXT"/></h1>&#xA;
</xsl:if>

<xsl:if test="(count(ancestor::node())-2)=2">
<h2><xsl:value-of select="@TEXT"/></h2>&#xA;
</xsl:if>

<xsl:if test="(count(ancestor::node())-2)=3">
<h3><xsl:value-of select="@TEXT"/></h3>&#xA;
</xsl:if>

<xsl:if test="(count(ancestor::node())-2)=4">
<h4><xsl:value-of select="@TEXT"/></h4>&#xA;
</xsl:if>

<xsl:if test="arrowlink/@DESTINATION != ''">
<a>
 <xsl:attribute name="href">
            	<xsl:text>#</xsl:text><xsl:value-of select="arrowlink/@DESTINATION"/>
            </xsl:attribute>
            <xsl:for-each select="key('refid', arrowlink/@DESTINATION)">
                <xsl:value-of select="@TEXT"/>
            </xsl:for-each>
      </a>&#xA;
</xsl:if>

<xsl:if test="(count(ancestor::node())-2)=5">
<h5><xsl:value-of select="@TEXT"/></h5>
<xsl:if test="current()/node">
<xsl:call-template name="itemization"/>
</xsl:if>
</xsl:if>

<!--<xsl:if test="(count(ancestor::node())-2)>4">

<xsl:call-template name="itemization"/>

</xsl:if>-->

<xsl:if test="5 > (count(ancestor::node())-2)">
<xsl:apply-templates select="node"/>
</xsl:if>


</xsl:template>

<xsl:template name="itemization">
<xsl:param name="i" select="current()/node" />

<ul>&#xA;
<xsl:for-each select="$i">
<xsl:if test="@ID != ''">
 <a>
 <xsl:attribute name="name">
            	<xsl:value-of select="@ID"/>
            </xsl:attribute>
</a>&#xA;
</xsl:if>
<li>&#xA;
<xsl:value-of select="@TEXT"/>
<xsl:if test="arrowlink/@DESTINATION != ''">

<a>
 <xsl:attribute name="href">
            	<xsl:text>#</xsl:text><xsl:value-of select="arrowlink/@DESTINATION"/>
            </xsl:attribute>
           <xsl:for-each select="key('refid', arrowlink/@DESTINATION)">
                <xsl:value-of select="@TEXT"/>
            </xsl:for-each>
</a>&#xA;
</xsl:if>
</li>&#xA;
</xsl:for-each >

<xsl:if test="$i/node">
<xsl:call-template name="itemization">
<xsl:with-param name="i" select="$i/node"/>
</xsl:call-template>
</xsl:if>
</ul>&#xA;
</xsl:template>


</xsl:stylesheet>
