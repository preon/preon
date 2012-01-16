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
package org.codehaus.preon.codec;

import org.codehaus.preon.Codec;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.Bound;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.If;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ConditionalsTest {

    @Test
    public void preon46HandleFalseTest() throws DecodingException {
        Codec<Preon46> codec = Codecs.create(Preon46.class);
        Preon46 value = Codecs.decode(codec, (byte) 0, (byte) 1);
        assertThat(value.flag, is(false));
        assertThat(value.second, is(0));
    }

    @Test
    public void preon46HandleTrueTest() throws DecodingException {
        Codec<Preon46> codec = Codecs.create(Preon46.class);
        Preon46 value = Codecs.decode(codec, (byte) 1, (byte) 1);
        assertThat(value.flag, is(true));
        assertThat(value.second, is(1));
    }

    public static class Preon46 {
        @Bound
        boolean notUsed1;
        @Bound
        boolean notUsed2;
        @Bound
        boolean notUsed3;
        @Bound
        boolean notUsed4;
        @Bound
        boolean notUsed5;
        @Bound
        boolean notUsed6;
        @Bound
        boolean notUsed7;
        @Bound
        boolean flag;

        @If("flag")
        @BoundNumber(size = "8")
        int second;
    }

}
