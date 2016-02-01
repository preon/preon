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
package org.codehaus.preon.sample.bmp;

import org.codehaus.preon.annotation.BoundNumber;
import org.codehaus.preon.annotation.BoundString;
import org.codehaus.preon.annotation.Purpose;

@Purpose("The block of bytes added before the bitmap format, "
        + "used internally by GDI and serves for identification.")
public class BitmapHeader {

    @Purpose("The magic number, used to identify the bitmap file.")
    @BoundString(size = "2", match = "BM")
    private String magicNumber;

    @Purpose("The size of the bitmap file.")
    @BoundNumber(size = "32")
    private long size;

    @Purpose("Reserved. Actual value depends on the application that creates the image.")
    @BoundNumber(size = "16")
    private int reservedValue1;

    @Purpose("Reserved. Actual value depends on the application that creates the image.")
    @BoundNumber(size = "16")
    private int reservedValue2;

    @Purpose("The offset of the byte where the bitmap data can be found.")
    @BoundNumber(size = "32")
    private long bitmapDataOffset;

}
