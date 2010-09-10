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
