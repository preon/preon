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
package org.codehaus.preon.util;
/*
 * Copyright (C) 2008 Wilfred Springer
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

/*package org.codehaus.preon.util;

import org.codehaus.preon.el.DescriptionBuilder;
import org.codehaus.preon.el.ReferenceResolver;
import org.codehaus.preon.el.UnresolvableReferenceException;

public class ChainingResolver implements ReferenceResolver {
    
    private ReferenceResolver match;
    
    private String key;
    
    private ReferenceResolver alternative;
    
    public ChainingResolver(ReferenceResolver match, String key, ReferenceResolver alternative) {
        this.match = match;
        this.alternative = alternative;
        this.key = key;
    }

    public Object resolve(String... reference) throws UnresolvableReferenceException {
        if (key.equals(reference[0])) {
            return match.resolve(chop(reference));
        } else {
            return alternative.resolve(reference);
        }
    }

    public void describe(StringBuilder builder, String... reference)
            throws UnresolvableReferenceException {
        if (key.equals(reference[0])) {
            match.describe(builder);
        } else {
            alternative.describe(builder);
        }
    }

    public void describe(DescriptionBuilder builder, String... reference)
            throws UnresolvableReferenceException {
        if (key.equals(reference[0])) {
            match.describe(builder);
        } else {
            alternative.describe(builder);
        }
    }
    
    private String[] chop(String... reference) {
        String[] result = new String[reference.length - 1];
        System.arraycopy(reference, 1, result, 0, result.length);
        return result;
    }
    
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("ChainingResolver[first");
        builder.append(match.toString());
        builder.append(", alternative=");
        builder.append(alternative.toString());
        builder.append("]");
        return builder.toString();
    }

}
*/