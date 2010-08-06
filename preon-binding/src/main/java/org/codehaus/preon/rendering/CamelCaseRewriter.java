/**
 * Copyright (C) 2009-2010 Wilfred Springer
 *
 * This file is part of Preon.
 *
 * Preon is free software; you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version.
 *
 * Preon is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Preon; see the file COPYING. If not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Linking this library statically or dynamically with other modules is making a
 * combined work based on this library. Thus, the terms and conditions of the
 * GNU General Public License cover the whole combination.
 *
 * As a special exception, the copyright holders of this library give you
 * permission to link this library with independent modules to produce an
 * executable, regardless of the license terms of these independent modules, and
 * to copy and distribute the resulting executable under terms of your choice,
 * provided that you also meet, for each linked independent module, the terms
 * and conditions of the license of that module. An independent module is a
 * module which is not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the library, but
 * you are not obligated to do so. If you do not wish to do so, delete this
 * exception statement from your version.
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
