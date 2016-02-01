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
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.util.EvenlyDistributedLazyList;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class ListCodecIntegrationTest {

    @Test
    public void shouldLoadListLazily() throws DecodingException {
        Codec<Test1> codec = Codecs.create(Test1.class);
        Test1 result = Codecs.decode(codec, new byte[] { 2, 3, 'a', 'b', 'c', 'd', 'e', 'f' });
        assertThat(result.records, instanceOf(EvenlyDistributedLazyList.class));
        assertThat(result.records.size(), is(2));
        assertThat(result.records.get(0).value, is("abc"));
        assertThat(result.records.get(1).value, is("def"));
    }

    @Test
    public void shouldLoadDynamically() throws DecodingException {
        Codec<Test3> codec = Codecs.create(Test3.class);
        Test3 result = Codecs.decode(codec, new byte[] { 2, 2, 1, 'a', 'b', 'c', 0, 'e', 'f' });
        // In this case, we can no longer lazy load elements.
        assertThat(result.records, not(instanceOf(EvenlyDistributedLazyList.class)));
        assertThat(result.records.size(), is(2));
        assertThat(result.records.get(0).value, is("abc"));
        assertThat(result.records.get(1).value, is("ef"));
    }

    @Test
    public void shouldLoadArrayOfBooleans() throws DecodingException {
        Codec<Test5> codec = Codecs.create(Test5.class);
        Test5 value = Codecs.decode(codec, (byte) 0xf0);
        assertThat(value.booleans, is(not(nullValue())));
        assertThat(value.booleans.length, is(8));
        assertThat(value.booleans[0], is(true));
        assertThat(value.booleans[1], is(true));
        assertThat(value.booleans[2], is(true));
        assertThat(value.booleans[3], is(true));
        assertThat(value.booleans[4], is(false));
        assertThat(value.booleans[5], is(false));
        assertThat(value.booleans[6], is(false));
        assertThat(value.booleans[7], is(false));
    }

    @Test
    public void shouldLoadListOfBooleans() throws DecodingException {
        Codec<Test6> codec = Codecs.create(Test6.class);
        Test6 value = Codecs.decode(codec, (byte) 0xf0);
        assertThat(value.booleans, is(not(nullValue())));
        assertThat(value.booleans.size(), is(8));
    }


    public static class Test1 {

        @BoundNumber(size = "8")
        public int nrRecords;

        @BoundNumber(size = "8")
        public int nrCharacters;

        @BoundList(size = "nrRecords", type = Test2.class)
        public List<Test2> records;

        public static class Test2 {

            @BoundString(size = "outer.nrCharacters")
            public String value;

        }

    }

    public static class Test3 {

        @BoundNumber(size = "8")
        public int nrRecords;

        @BoundNumber(size = "8")
        public int nrCharacters;

        @BoundList(size = "nrRecords", type = Test4.class)
        public List<Test4> records;

        public static class Test4 {

            /**
             * The number of extra characters to be read.
             */
            @Bound
            public byte extra;

            @BoundString(size = "outer.nrCharacters + extra")
            public String value;

        }

    }

    public static class Test5 {

        @BoundList(size="8", type = Boolean.class)
        public boolean[] booleans;

    }

    public static class Test6 {

        @BoundList(size="8", type = Boolean.class)
        public List<Boolean> booleans;

    }


}
