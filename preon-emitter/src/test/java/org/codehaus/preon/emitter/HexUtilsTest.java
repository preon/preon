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
