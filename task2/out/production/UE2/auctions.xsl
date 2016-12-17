<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <html>
            <body>
                <h1>Auction House - Products Overview</h1>
                <xsl:for-each select="/auctions/bids/product">
                    <xsl:variable name="pid" select="@id" />
                    <xsl:variable name="product" select="/auctions/products/product[@id=$pid]" />
                    <h2>
                        <xsl:value-of select="$product/name/text()" />
                    </h2>
                    <p>
                        <xsl:value-of select="$product/description" disable-output-escaping="yes" />
                    </p>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
