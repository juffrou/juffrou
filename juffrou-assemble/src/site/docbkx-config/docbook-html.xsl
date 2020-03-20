<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xslthl="http://xslthl.sf.net"
                exclude-result-prefixes="xslthl" version="1.0">
	<!-- import the main stylesheet, here pointing to fo/docbook.xsl -->
	<xsl:import href="urn:docbkx:stylesheet" />
	<!-- highlight.xsl must be imported in order to enable highlighting support, 
		highlightSource=1 parameter is not sufficient -->
	<xsl:import href="urn:docbkx:stylesheet/highlight.xsl" />

	<xsl:template match="xslthl:comment">
		<i class="hl-comment">
			<xsl:apply-templates />
		</i>
	</xsl:template>

	<xsl:template name="user.head.content">
<!-- Google Analytics -->
<script type="text/javascript">
(function(i,s,o,g,r,a,m){i['GoogleAnalyticsObject']=r;i[r]=i[r]||function(){
(i[r].q=i[r].q||[]).push(arguments)},i[r].l=1*new Date();a=s.createElement(o),
m=s.getElementsByTagName(o)[0];a.async=1;a.src=g;m.parentNode.insertBefore(a,m)
})(window,document,'script','//www.google-analytics.com/analytics.js','ga');

ga('create', 'UA-36088259-2', 'github.com');
ga('send', 'pageview');
</script>
 	</xsl:template>

	<!-- CUSTOMIZATION
	     BASED ON THE FILE (docbook-xsl-ns-1.78.1)/html/highlight.xsl
	     (basically copy some entries of that file to this file and then change some properties) -->
	
	<!-- <xsl:attribute-set name="section.title.level1.properties"> <xsl:attribute 
		name="border-top">0.5pt solid black</xsl:attribute> <xsl:attribute name="border-bottom">0.5pt 
		solid black</xsl:attribute> <xsl:attribute name="padding-top">6pt</xsl:attribute> 
		<xsl:attribute name="padding-bottom">3pt</xsl:attribute> </xsl:attribute-set> -->

	<xsl:template match="xslthl:keyword" mode="xslthl">
		<strong class="hl-keyword" style="color:#950055">
			<xsl:apply-templates mode="xslthl" />
		</strong>
	</xsl:template>
	<xsl:template match="xslthl:string" mode="xslthl">
			<span class="hl-string" style="color:blue">
				<xsl:apply-templates mode="xslthl" />
			</span>
	</xsl:template>


</xsl:stylesheet>
