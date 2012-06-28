package org.codehaus.preon.acceptancetests;

import org.codehaus.preon.DecodingException;
import org.codehaus.preon.annotation.BoundString;
import org.junit.Test;

import java.util.regex.Pattern;

import static org.codehaus.preon.acceptancetests.TestUtils.decodeObjectFromInput;
import static org.codehaus.preon.acceptancetests.TestUtils.generateArrayContaining;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class StringBindingFixedLength {
    @Test
    public void defaultEncodingIsUsAscii() throws NoSuchFieldException {
        final BoundString strField = ClassWithFixedLengthStringOfThreeBytes.class.getField("str").getAnnotation(BoundString.class);

        assertThat(strField.encoding(), is("US-ASCII"));
    }

    @Test
    public void canDecodeAnyLengthStrings() throws DecodingException {
        assertCanDecodeShortFixedLengthString();
        assertCanDecodeLongFixedLengthString();
    }

    @Test
    public void canDecodeStringsFromJvmSupportedCharsets() throws DecodingException {
        assertCanDecodeUTF8FixedLengthString();
        assertCanDecodeUTF16BEFixedLengthString();
    }

    @Test
    public void canDecodeStringsUsingMatchParameter() throws DecodingException {
        assertCanDecodeStringWithSuccessfulMatch();
        assertThrowsDecodingExceptionOnInvalidMatch();
    }

    private static void assertCanDecodeShortFixedLengthString() throws DecodingException {
        final byte[] shortInput = {'a','b','c'};
        final ClassWithFixedLengthStringOfThreeBytes shortResult = decodeObjectFromInput(ClassWithFixedLengthStringOfThreeBytes.class, shortInput);

        assertThat(shortResult.str, is("abc"));
    }

    private static void assertCanDecodeLongFixedLengthString() throws DecodingException {
        final int LONG_INPUT_SIZE_BYTES = 1024*1024*10; // 10mb string
        final byte[] longInput = generateArrayContaining((byte) 'a', LONG_INPUT_SIZE_BYTES); // "aaaa..." LONG_INPUT_SIZE_BYTES times

        final ClassWithFixedLengthStringOf10mb longResult = decodeObjectFromInput(ClassWithFixedLengthStringOf10mb.class, longInput);

        final String matchStr = String.format("\\Aa{%d}\\z", LONG_INPUT_SIZE_BYTES);
        assertTrue("Couldn't match string", Pattern.matches(matchStr, longResult.str));
    }

    private static void assertCanDecodeUTF8FixedLengthString() throws DecodingException {
        final byte[] input = {
                (byte)0x54,                         // 1 UTF-8 code unit: T
                (byte)0xC3, (byte)0x9F,             // 2 UTF-8 code units: ß
                (byte)0xE6, (byte)0x9D, (byte)0xB1  // 3 UTF-8 code units: 東
        };
        final ClassWithFixedLengthUTF8StringOfSixBytes result = decodeObjectFromInput(ClassWithFixedLengthUTF8StringOfSixBytes.class, input);

        assertThat(result.str, is("Tß東"));
    }

    private static void assertCanDecodeUTF16BEFixedLengthString() throws DecodingException {
        final byte[] input = {
                (byte)0x00, (byte)0x54, // 1 UTF-8 code unit: T
                (byte)0x00, (byte)0xDF, // 1 UTF-16 code unit: ß
                (byte)0x67, (byte)0x71  // 1 UTF-16 code unit: 東
        };
        final ClassWithFixedLengthUTF16BEStringOfSixBytes result = decodeObjectFromInput(ClassWithFixedLengthUTF16BEStringOfSixBytes.class, input);

        assertThat(result.str, is("Tß東"));
    }

    private static void assertCanDecodeStringWithSuccessfulMatch() throws DecodingException {
        final byte[] input = {'A','b','C'};
        ClassWithStringOfThreeBytesAndMatch2 result = decodeObjectFromInput(ClassWithStringOfThreeBytesAndMatch2.class, input);

        assertThat(result.str, is("AbC"));
    }

    private static void assertThrowsDecodingExceptionOnInvalidMatch() throws DecodingException {
        final byte[] input = {'A','b','C'};
        
        try {
            decodeObjectFromInput(ClassWithStringOfThreeBytesAndMatch1.class, input);
            fail("Expected DecodingException");
        }
        catch (DecodingException ignored) {}
    }

    public static class ClassWithStringOfThreeBytesAndMatch2 {
        @BoundString(size = "3", match = "AbC")
        public String str;
    }
    
    public static class ClassWithStringOfThreeBytesAndMatch1 {
        @BoundString(size = "3", match = "AbCd")
        public String str;
    }

    public static class ClassWithFixedLengthUTF16BEStringOfSixBytes {
        @BoundString(encoding = "UTF-16BE", size = "6")
        public String str;
    }

    public static class ClassWithFixedLengthUTF8StringOfSixBytes {
        @BoundString(encoding = "UTF-8", size = "6")
        public String str;
    }
    
    public static class ClassWithFixedLengthStringOfThreeBytes {
        @BoundString(size = "3")
        public String str;
    }

    public static class ClassWithFixedLengthStringOf10mb {
        @BoundString(size = "1024*1024*10")
        public String str;
    }
}
