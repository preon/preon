<?xml version="1.0" encoding="utf-8"?>
<!--

    Copyright (C) 2008 Wilfred Springer
    
    This file is part of Preon.
    
    Preon is free software; you can redistribute it and/or modify it under the
    terms of the GNU General Public License as published by the Free Software
    Foundation; either version 2, or (at your option) any later version.
    
    Preon is distributed in the hope that it will be useful, but WITHOUT ANY
    WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
    A PARTICULAR PURPOSE. See the GNU General Public License for more details.
    
    You should have received a copy of the GNU General Public License along with
    Preon; see the file COPYING. If not, write to the Free Software
    Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
    
    Linking this library statically or dynamically with other modules is making a
    combined work based on this library. Thus, the terms and conditions of the
    GNU General Public License cover the whole combination.
    
    As a special exception, the copyright holders of this library give you
    permission to link this library with independent modules to produce an
    executable, regardless of the license terms of these independent modules, and
    to copy and distribute the resulting executable under terms of your choice,
    provided that you also meet, for each linked independent module, the terms
    and conditions of the license of that module. An independent module is a
    module which is not derived from or based on this library. If you modify this
    library, you may extend this exception to your version of the library, but
    you are not obligated to do so. If you do not wish to do so, delete this
    exception statement from your version.

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
