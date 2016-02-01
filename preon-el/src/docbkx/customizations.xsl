<?xml version="1.0" encoding="utf-8"?>
<!--

    Copyright (c) 2009-2016 Wilfred Springer

    Permission is hereby granted, free of charge, to any person
    obtaining a copy of this software and associated documentation
    files (the "Software"), to deal in the Software without
    restriction, including without limitation the rights to use,
    copy, modify, merge, publish, distribute, sublicense, and/or sell
    copies of the Software, and to permit persons to whom the
    Software is furnished to do so, subject to the following
    conditions:

    The above copyright notice and this permission notice shall be
    included in all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
    EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
    OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
    NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
    HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
    WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
    FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
    OTHER DEALINGS IN THE SOFTWARE.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:fo="http://www.w3.org/1999/XSL/Format"
                version="1.0">

  <xsl:import href="urn:docbkx:stylesheet"/>

  <xsl:param name="body.start.indent">0mm</xsl:param>

  <!--
  <xsl:template name="section.heading">
    <xsl:param name="level" select="1"/>
    <xsl:param name="marker" select="1"/>
    <xsl:param name="title"/>
    <xsl:param name="marker.title"/>

    <xsl:variable name="title.block">
      <fo:list-block start-indent="0mm"
                     provisional-distance-between-starts="{$body.start.indent}"
                     provisional-label-separation="5mm">
        <fo:list-item>
          <fo:list-item-label end-indent="label-end()" text-align="start">
            <fo:block>
              <xsl:apply-templates select="parent::*" mode="label.markup"/>
            </fo:block>
          </fo:list-item-label>
          <fo:list-item-body start-indent="body-start()" text-align="start">
            <fo:block>
              <xsl:apply-templates select="parent::*" mode="title.markup"/>
            </fo:block>
          </fo:list-item-body>
        </fo:list-item>
      </fo:list-block>
    </xsl:variable>

    <fo:block xsl:use-attribute-sets="section.title.properties">
      <xsl:if test="$marker != 0">
        <fo:marker marker-class-name="section.head.marker">
          <xsl:copy-of select="$marker.title"/>
        </fo:marker>
      </xsl:if>

      <xsl:choose>
        <xsl:when test="$level=1">
          <fo:block xsl:use-attribute-sets="section.title.level1.properties">
            <xsl:copy-of select="$title.block"/>
          </fo:block>
        </xsl:when>
        <xsl:when test="$level=2">
          <fo:block xsl:use-attribute-sets="section.title.level2.properties">
            <xsl:copy-of select="$title.block"/>
          </fo:block>
        </xsl:when>
        <xsl:when test="$level=3">
          <fo:block xsl:use-attribute-sets="section.title.level3.properties">
            <xsl:copy-of select="$title.block"/>
          </fo:block>
        </xsl:when>
        <xsl:when test="$level=4">
          <fo:block xsl:use-attribute-sets="section.title.level4.properties">
            <xsl:copy-of select="$title.block"/>
          </fo:block>
        </xsl:when>
        <xsl:when test="$level=5">
          <fo:block xsl:use-attribute-sets="section.title.level5.properties">
            <xsl:copy-of select="$title.block"/>
          </fo:block>
        </xsl:when>
        <xsl:otherwise>
          <fo:block xsl:use-attribute-sets="section.title.level6.properties">
            <xsl:copy-of select="$title.block"/>
          </fo:block>
        </xsl:otherwise>
      </xsl:choose>
    </fo:block>
  </xsl:template>
  -->

  <xsl:attribute-set name="section.title.properties">
    <xsl:attribute name="font-family">
      <xsl:value-of select="$title.font.family"/>
    </xsl:attribute>
    <!-- font size is calculated dynamically by section.heading template -->
    <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
    <xsl:attribute name="space-before.minimum">0.8em</xsl:attribute>
    <xsl:attribute name="space-before.optimum">1.0em</xsl:attribute>
    <xsl:attribute name="space-before.maximum">1.2em</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="section.title.level1.properties">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
    <xsl:attribute name="padding-top">1cm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="section.title.level2.properties">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
    <xsl:attribute name="padding-top">1cm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="section.title.level3.properties">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
    <xsl:attribute name="padding-top">1cm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:attribute-set name="section.title.level4.properties">
    <xsl:attribute name="font-weight">bold</xsl:attribute>
    <xsl:attribute name="text-align">left</xsl:attribute>
    <xsl:attribute name="font-size">12pt</xsl:attribute>
    <xsl:attribute name="padding-top">1cm</xsl:attribute>
  </xsl:attribute-set>

  <xsl:template name="table.of.contents.titlepage" priority="1">
    <fo:block space-before="1in"
              space-before.conditionality="retain"
              space-after="12pt">
      <xsl:attribute name="font-weight">bold</xsl:attribute>
      <xsl:attribute name="text-align">left</xsl:attribute>
      <xsl:attribute name="font-size">12pt</xsl:attribute>
      <xsl:attribute name="padding-top">1cm</xsl:attribute>
      <xsl:attribute name="font-family">
        <xsl:value-of select="$title.font.family"/>
      </xsl:attribute>
      <xsl:attribute name="keep-with-next.within-column">always</xsl:attribute>
      <xsl:call-template name="gentext">
        <xsl:with-param name="key" select="'TableofContents'"/>
      </xsl:call-template>
    </fo:block>
  </xsl:template>
  <xsl:param name="generate.toc">
    appendix nop
    article/appendix nop
    article nop
    book toc,title,figure,table,example,equation
    chapter toc,title
    part toc,title
    preface toc,title
    qandadiv toc
    qandaset toc
    reference toc,title
    sect1 toc
    sect2 toc
    sect3 toc
    sect4 toc
    sect5 toc
    section toc
    set toc,title
  </xsl:param>


</xsl:stylesheet>
