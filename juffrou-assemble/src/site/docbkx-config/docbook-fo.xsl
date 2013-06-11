<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:xslthl="http://xslthl.sf.net"
	exclude-result-prefixes="xslthl" version="1.0">
	<!-- import the main stylesheet, here pointing to fo/docbook.xsl -->
	<xsl:import href="urn:docbkx:stylesheet" />
	<!-- highlight.xsl must be imported in order to enable highlighting support, 
		highlightSource=1 parameter is not sufficient -->
	<xsl:import href="urn:docbkx:stylesheet/highlight.xsl" />

    <xsl:attribute-set name="monospace.properties">
        <xsl:attribute name="font-family">
            <xsl:value-of select="$monospace.font.family"/>
        </xsl:attribute>
        <xsl:attribute name="font-size">0.6em</xsl:attribute>
    </xsl:attribute-set>

	<xsl:template match="processing-instruction('hard-pagebreak')">
	   <fo:block break-after='page'/>
	</xsl:template>

<!--###################################################
                        Programlistings
      ################################################### -->
<!-- Verbatim text formatting (programlistings) -->
	<!-- 
    <xsl:attribute-set name="monospace.verbatim.properties">
        <xsl:attribute name="font-size">
            <xsl:value-of select="$body.font.small * 1.0"/>
            <xsl:text>pt</xsl:text>
        </xsl:attribute>
    </xsl:attribute-set>
     -->
    <xsl:attribute-set name="verbatim.properties">
        <xsl:attribute name="space-before.minimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.optimum">1em</xsl:attribute>
        <xsl:attribute name="space-before.maximum">1em</xsl:attribute>
        <xsl:attribute name="border-color">#444444</xsl:attribute>
        <xsl:attribute name="border-style">solid</xsl:attribute>
        <xsl:attribute name="border-width">0.1pt</xsl:attribute>
        <xsl:attribute name="padding-top">0.5em</xsl:attribute>
        <xsl:attribute name="padding-left">0.5em</xsl:attribute>
        <xsl:attribute name="padding-right">0.5em</xsl:attribute>
        <xsl:attribute name="padding-bottom">0.5em</xsl:attribute>
        <xsl:attribute name="margin-left">0.5em</xsl:attribute>
        <xsl:attribute name="margin-right">0.5em</xsl:attribute>
    </xsl:attribute-set>
<!-- Shade (background) programlistings -->
    <xsl:param name="shade.verbatim">1</xsl:param>
    <xsl:attribute-set name="shade.verbatim.style">
        <xsl:attribute name="background-color">#F0F0F0</xsl:attribute>
    </xsl:attribute-set>

	<xsl:template match="xslthl:comment">
		<i class="hl-comment">
			<xsl:apply-templates />
		</i>
	</xsl:template>

</xsl:stylesheet>
