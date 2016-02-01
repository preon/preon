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
package org.codehaus.preon.buffer;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import junit.framework.TestCase;


public class SlicedBitBufferTest extends TestCase {

    private BitBuffer delegate;

    public void setUp() {
        delegate = createMock(BitBuffer.class);
    }

    public void testReading() {
        expect(delegate.getBitBufBitSize()).andReturn(32L).anyTimes();
        expect(delegate.getBitPos()).andReturn(0L).times(2);
        expect(delegate.readAsBoolean()).andReturn(true);
        expect(delegate.getBitPos()).andReturn(1L);
        expect(delegate.readAsBoolean()).andReturn(false);
        expect(delegate.getBitPos()).andReturn(2L);
        expect(delegate.readAsBoolean()).andReturn(true);
        expect(delegate.getBitPos()).andReturn(3L);
        expect(delegate.readAsBoolean()).andReturn(false);
        expect(delegate.getBitPos()).andReturn(4L);
        expect(delegate.readAsBoolean()).andReturn(true);
        expect(delegate.getBitPos()).andReturn(5L).times(2);
        replay(delegate);
        BitBuffer slice = new SlicedBitBuffer(delegate, 5);
        assertTrue(slice.readAsBoolean());
        assertFalse(slice.readAsBoolean());
        assertTrue(slice.readAsBoolean());
        assertFalse(slice.readAsBoolean());
        assertTrue(slice.readAsBoolean());
        try {
            slice.readAsBoolean();
            fail();
        } catch (BitBufferUnderflowException bbue) {

        }
        verify(delegate);
    }

}
