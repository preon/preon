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
