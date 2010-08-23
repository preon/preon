package org.codehaus.preon.buffer;

import org.apache.commons.lang.SystemUtils;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class ByteOrderTest {

    @Test
    public void shouldReturnProperByteOrder() {
        if ("x86_64".equals(SystemUtils.OS_ARCH)) {
            assertThat(ByteOrder.Native, is(ByteOrder.LittleEndian));
        }
    }

}
