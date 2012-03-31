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

public class StringBindingNullTerminated {
    @Test
    public void defaultEncodingIsUsAscii() throws NoSuchFieldException {
        final BoundString strField = ClassWithNullTerminatedString.class.getField("str").getAnnotation(BoundString.class);

        assertThat(strField.encoding(), is("US-ASCII"));
    }

    @Test
    public void canDecodeAnyLengthStrings() throws DecodingException {
        assertCanDecodeShortNullTerminatedString();
        assertCanDecodeLongNullTerminatedString();
    }

    @Test
    public void canDecodeStringsFromJvmSupportedCharsets() throws DecodingException {
        assertCanDecodeUTF8NullTerminatedString();
        assertCanDecodeUTF16BENullTerminatedString();
    }

    private static void assertCanDecodeShortNullTerminatedString() throws DecodingException {
        final byte[] shortInput = {'a','b','c','\0'};
        final ClassWithNullTerminatedString shortResult = decodeObjectFromInput(ClassWithNullTerminatedString.class, shortInput);

        assertThat(shortResult.str, is("abc"));
    }

    private static void assertCanDecodeLongNullTerminatedString() throws DecodingException {
        final int LONG_INPUT_SIZE_BYTES = 1024*1024*10; // 10mb string
        final byte[] longInput = generateArrayContaining((byte)'a', LONG_INPUT_SIZE_BYTES); // "aaaa..." LONG_INPUT_SIZE_BYTES times
        longInput[LONG_INPUT_SIZE_BYTES-1] = '\0'; // Null termination character

        final ClassWithNullTerminatedString longResult = decodeObjectFromInput(ClassWithNullTerminatedString.class, longInput);

        final String matchStr = String.format("\\Aa{%d}\\z", LONG_INPUT_SIZE_BYTES - 1);
        assertTrue("Couldn't match string", Pattern.matches(matchStr, longResult.str));
    }

    private static void assertCanDecodeUTF8NullTerminatedString() throws DecodingException {
        final byte[] input = {
                (byte)0x54,                         // 1 UTF-8 code unit: T
                (byte)0xC3, (byte)0x9F,             // 2 UTF-8 code units: ß
                (byte)0xE6, (byte)0x9D, (byte)0xB1, // 3 UTF-8 code units: 東
                (byte)0x00                          // Null termination
        };
        final ClassWithNullTerminatedUTF8String result = decodeObjectFromInput(ClassWithNullTerminatedUTF8String.class, input);

        assertThat(result.str, is("Tß東"));
    }

    private static void assertCanDecodeUTF16BENullTerminatedString() throws DecodingException {
        final byte[] input = {
                (byte)0x00, (byte)0x54, // 1 UTF-16 code unit: T
                (byte)0x00, (byte)0xDF, // 1 UTF-16 code unit: ß
                (byte)0x67, (byte)0x71, // 1 UTF-16 code unit: 東
                (byte)0x00, (byte)0x00  // Null termination
        };
        final ClassWithNullTerminatedUTF16BEString result = decodeObjectFromInput(ClassWithNullTerminatedUTF16BEString.class, input);

        assertThat(result.str, is("Tß東"));
    }

    public static class ClassWithNullTerminatedUTF16BEString {
        @BoundString(encoding = "UTF-16BE")
        public String str;
    }

    public static class ClassWithNullTerminatedUTF8String {
        @BoundString(encoding = "UTF-8")
        public String str;
    }

    public static class ClassWithNullTerminatedString {
        @BoundString
        public String str;
    }
}
