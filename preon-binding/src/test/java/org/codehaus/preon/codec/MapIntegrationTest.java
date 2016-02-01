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
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;

public class MapIntegrationTest {

    @Test
    public void shouldDecodeMapCorrectly() throws DecodingException, FileNotFoundException {
        Codec<Sample> codec = Codecs.create(Sample.class);
        Sample object =
                Codecs.decode(codec, (byte) 0x02, (byte) (0xff & 'a'), (byte) (0xff & 'b'), (byte) 0x03,
                                     (byte) 0x02, (byte) (0xff & 'c'), (byte) (0xff & 'd'), (byte) 0x04);
        assertThat(object.data.size(), is(2));
        assertThat(object.data, hasEntry("ab", 3));
        assertThat(object.data, hasEntry("cd", 4));
    }

    public static class Sample {

        @BoundList(type= SampleEntry.class)
        public Map<String,Integer> data;

    }

    public static class SampleEntry implements Map.Entry<String,Integer> {

        @BoundNumber(size="8")
        private int keyLength;

        @BoundString(size = "keyLength")
        private String key;

        @BoundNumber(size="8")
        private Integer value;

        public String getKey() {
            return key;
        }

        public Integer getValue() {
            return value;
        }

        public Integer setValue(Integer value) {
            Integer originalValue = value;
            this.value = value;
            return originalValue;
        }
    }

}
