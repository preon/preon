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
package org.codehaus.preon.emitter;

import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HexUtilsTest {

    private String line = "Wondering how this works out. I would expect it's all going to work out fine, but it's hard to tell";

    @Test
    public void shouldRenderCorrectlyWithoutAddress() throws IOException {
        byte[] buffer = line.getBytes("UTF-8");
        StringBuilder builder = new StringBuilder();
        HexUtils.dump(buffer, builder, 16);
        String[] lines = builder.toString().split("\n");
        assertThat(lines.length, is(7));
        assertThat(lines[0], is("57 6f 6e 64 65 72 69 6e  67 20 68 6f 77 20 74 68  |Wondering.how.th|"));
        assertThat(lines[1], is("69 73 20 77 6f 72 6b 73  20 6f 75 74 2e 20 49 20  |is.works.out..I.|"));
        assertThat(lines[2], is("77 6f 75 6c 64 20 65 78  70 65 63 74 20 69 74 27  |would.expect.it.|"));
        assertThat(lines[3], is("73 20 61 6c 6c 20 67 6f  69 6e 67 20 74 6f 20 77  |s.all.going.to.w|"));
        assertThat(lines[4], is("6f 72 6b 20 6f 75 74 20  66 69 6e 65 2c 20 62 75  |ork.out.fine..bu|"));
        assertThat(lines[5], is("74 20 69 74 27 73 20 68  61 72 64 20 74 6f 20 74  |t.it.s.hard.to.t|"));
        assertThat(lines[6], is("65 6c 6c                                          |ell             |"));
    }


}
