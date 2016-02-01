/**
 * Copyright (c) 2009-2016 Wilfred Springer
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package org.codehaus.preon.rendering;

import java.util.ArrayList;
import java.util.List;

/**
 * An object that will take a camelcase identifier, and break it apart into different chunks.
 *
 * @author Wilfred Springer (wis)
 */
public class CamelCaseRewriter implements IdentifierRewriter {

    private boolean startWithUppercase;

    public CamelCaseRewriter() {
        startWithUppercase = true;
    }

    public CamelCaseRewriter(boolean startWithUppercase) {
        this.startWithUppercase = startWithUppercase;
    }

    public String rewrite(String name, boolean startWithCapital) {
        List<String> parts = new ArrayList<String>();
        StringBuilder portion = new StringBuilder();
        int length = name.length();
        for (int i = 0; i < length; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                String part = portion.toString();
                if (part.length() > 0) {
                    parts.add(part);
                }
                portion.setLength(0);
                portion.append(Character.toLowerCase(c));
            } else {
                portion.append(c);
            }
        }
        String part = portion.toString();
        if (part.length() > 0) {
            parts.add(part);
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i != 0) {
                result.append(' ');
            }
            if (i == 0 && startWithCapital) {
                result.append(Character.toUpperCase(parts.get(i).charAt(0)));
                result.append(parts.get(i).substring(1));
            } else {
                result.append(parts.get(i));
            }
        }
        return result.toString();
    }

    public String rewrite(String name) {
        return rewrite(name, startWithUppercase);
    }

}
