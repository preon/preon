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
