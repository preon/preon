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
package org.codehaus.preon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.codehaus.preon.buffer.BitBuffer;


/**
 * A simple annotation for marking particular fields to be optional, depending on the condition. The condition is based
 * on the Limbo notation. Variables are expected to be resolved relatively to the object holding the annotated field.
 * <p/> <p> Here is an example snippet: </p>
 * <p/>
 * <pre>
 * private int databaseVersion;
 *
 * @author Wilfred Springer
 * @If(&quot;databaseVersion &gt; 700&quot;)
 * @Bound private int foobar; </pre> <p/> <p> In the above case, <code>foobar</code> is only expected to be read from
 * the {@link BitBuffer} if the condition holds. If <code>databaseVersion</code> is 300, it will be skipped. </p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface If {

    /**
     * The expression to be evaluated.
     *
     * @return The expression to be evaluated.
     */
    String value();

}
