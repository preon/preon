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
