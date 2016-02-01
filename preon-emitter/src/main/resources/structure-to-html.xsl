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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

  <xsl:output method="html"/>

  <xsl:template match="/">
    <html>
      <style type="text/css">
.value {
    font-size: 14pt;
    font-weight: bold;
}
body {
    font-family: Calibri, Arial, sans;
}

#details {
    margin-left: 20px;
    float:left;
}
pre {
    float:left;
    margin: 0px;
    height: 80%; overflow-y: auto; overflow-x: hidden; padding-right: 40px
}
.cursor {
    background-color: orange;
}

.callout {
    padding: 5px;
    border: 1px solid #7992B0;
    background-color: #8AA9B7;
}
      </style>
      <script type="text/javascript" src="./jquery-1.4.2.min.js"></script>
      <script type="text/javascript" src="./jquery.callout-min.js"></script>
      <script type="text/javascript"><![CDATA[
    $(document).ready(function() {

        $("#dump span").mouseenter(function() {
            var pos = $(this).prevAll().length;
            var nodes = findNodes(pos);
            highlight(nodes);
            documentNodes(nodes);
        });
        
        $("#dump span").mouseleave(function() {
            $("#dump").children().removeClass("cursor");
        });
        
    });

    function documentNodes(nodes) {
        $("#details").empty();
        for (i = 0; i < nodes.length; i++) {
            documentNode(details[nodes[i].id]);
        }
    }
    
    function documentNode(desc) {
        if (desc.value) {
            $("#details").append("<span class='value'>" + desc.value + "</span><br>");
        }
        if (desc.slot) {
            $("#details").append(desc.slot + " (" + desc.type + ")<br>");
        } else {
            $("#details").append("a " + desc.type + "<br>");
        }
        if (desc.owner) {
            $("#details").append("part of ");
            documentNode(details[desc.owner]);
        }
    }
    
    function highlight(nodes) {
        if (nodes.length != 0) {
            var min = $("#dump").children().length;
            var max = 0;
            for (i = 0; i < nodes.length; i++) {
                min = Math.min(min, Math.floor(nodes[i].start / 8));
                max = Math.max(max, Math.ceil(nodes[i].end / 8));
            }
            $("#dump").children().slice(min, max).addClass("cursor");
            text = min + ", " + max;
        }
    }
   
   function findNodes(pos) {
        var result = new Array();
        for (i = 0; i < struct.length; i++) {
            element = struct[i];
            if (
                (element.start < pos * 8 && element.end > pos * 8) 
                    || (element.end > (pos + 1) * 8 && element.start < (pos + 1) * 8)
                    || (element.start > pos * 8 && element.end < (pos + 1) * 8))
            {
                result[result.length] = element;
            }
        }
        return result;
    }

      ]]></script>
      <body>
        <h2>Dumpr</h2>
        <xsl:apply-templates select="emitter"/>
      </body>
    </html>
  </xsl:template>
  
  <xsl:template match="emitter">
    <pre id="dump"><xsl:apply-templates select="bytes"/></pre>
    <div id="details"/>
    <script type="text/javascript">
var struct = <xsl:call-template name="generate-structure"/>;
var details = <xsl:call-template name="generate-details"/>;
    </script>
  </xsl:template>

  <xsl:template match="span">
    <xsl:copy>
      <xsl:copy-of select="@*"/>
      <xsl:apply-templates/>
    </xsl:copy>
  </xsl:template>

  <xsl:template name="generate-details">
    <xsl:text>{</xsl:text>
    <xsl:for-each select="//fragment">
      <xsl:if test="not(position()=1)">
        <xsl:text>, 
</xsl:text>
      </xsl:if>
      <xsl:text>"</xsl:text>
      <xsl:value-of select="generate-id()"/>
      <xsl:text>" : {</xsl:text>
      <xsl:text>"type" : "</xsl:text>
      <xsl:value-of select="end/@type"/>
      <xsl:text>"</xsl:text>
      <xsl:if test="end/@value and not(descendant::fragment)">
        <xsl:text>, "value" : "</xsl:text>
        <xsl:value-of select="end/@value"/>
        <xsl:text>"</xsl:text>
      </xsl:if>
      <xsl:if test="parent::slot">
        <xsl:text>, "slot" : "</xsl:text>
        <xsl:value-of select="parent::slot/@name"/>
        <xsl:text>"</xsl:text>
      </xsl:if>
      <xsl:if test="ancestor::fragment">
        <xsl:text>, "owner" : "</xsl:text>
        <xsl:value-of select="generate-id(ancestor::fragment[1])"/>
        <xsl:text>"</xsl:text>
      </xsl:if>
      <xsl:text>}</xsl:text>
    </xsl:for-each>
    <xsl:text>}</xsl:text>
  </xsl:template>

  <xsl:template name="generate-structure">
    <xsl:text>[</xsl:text>
    <xsl:apply-templates select="//fragment[not(descendant::fragment)]" mode="leaf">
      <xsl:sort select="start/@position" data-type="number" order="ascending"/>
    </xsl:apply-templates>
    <xsl:text>]</xsl:text>
  </xsl:template>

  <xsl:template match="fragment" mode="leaf">
    <xsl:if test="not(position()=1)">
      <xsl:text>, 
</xsl:text>
    </xsl:if>
    <xsl:text>{</xsl:text>
    <xsl:text>"id" : "</xsl:text>
    <xsl:value-of select="generate-id()"/>
    <xsl:text>"</xsl:text>
    <xsl:text>, "start" : </xsl:text>
    <xsl:value-of select="start/@position"/>
    <xsl:text>, "end" : </xsl:text>
    <xsl:value-of select="end/@position"/>
    <xsl:text>}</xsl:text>
  </xsl:template>


</xsl:stylesheet>