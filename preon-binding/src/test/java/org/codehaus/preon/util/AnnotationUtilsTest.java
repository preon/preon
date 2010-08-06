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

import junit.framework.TestCase;
import org.codehaus.preon.annotation.Purpose;
import org.codehaus.preon.annotation.TypePrefix;

/**
 * Just checking the walls for some Annotation properties.
 *
 * @author Wilfred Springer (wis)
 */
public class AnnotationUtilsTest extends TestCase {

    public void testEquals() {
        assertTrue(AnnotationUtils.equivalent(Test1.class, Test2.class));
        assertFalse(AnnotationUtils.equivalent(Test2.class, Test3.class));
        assertFalse(AnnotationUtils.equivalent(Test3.class, Test4.class));
        assertFalse(AnnotationUtils.equivalent(Test4.class, Test5.class));
        assertFalse(AnnotationUtils.equivalent(Test5.class, Test4.class));
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("whatever")
    public static class Test1 {
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("whatever")
    public static class Test2 {
    }

    @TypePrefix(size = 2, value = "blaat")
    @Purpose("foobar")
    public static class Test3 {
    }

    @Purpose("foobar")
    public static class Test4 {
    }

    @TypePrefix(size = 2, value = "blaat")
    public static class Test5 {
    }

}
