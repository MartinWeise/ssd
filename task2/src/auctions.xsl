<?xml version="1.0" encoding="ISO-8859-1"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">

    <xsl:output method="html" indent="yes" encoding="UTF-8"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/auctions">
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                <title>Auction House - Products Overview</title>
            </head>
            <body>
                <h1>Auction House - Products Overview</h1>
                <xsl:for-each select="/auctions/bids/product">
                    <xsl:variable name="pid" select="@id" />
                    <xsl:variable name="product" select="/auctions/products/product[@id=$pid]" />
                    <h2>
                        <xsl:value-of select="$product/name/text()" />
                    </h2>
                    <p>
                        <xsl:apply-templates select="/auctions/products/product[@id=$pid]/description/node()" />
                    </p>
                    <p>Total Bids: <xsl:value-of select="count(/auctions/bids/product[@id=$pid]/bid)" /></p>
                    <ul>
                        <xsl:for-each select="/auctions/bids/product[@id=$pid]/bid">
                            <li>
                                <xsl:if test="position() = last()">
                                    <xsl:attribute name="style">
                                        <xsl:text>color:red</xsl:text>
                                    </xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="text()" />
                            </li>
                        </xsl:for-each>
                    </ul>
                </xsl:for-each>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="/auctions/products/product/description">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <p>
                <xsl:apply-templates/>
            </p>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/auctions/products/product/description/a">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="/auctions/products/product/description/it">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
