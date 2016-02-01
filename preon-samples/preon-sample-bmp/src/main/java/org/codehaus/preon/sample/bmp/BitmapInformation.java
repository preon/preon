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
import org.codehaus.preon.annotation.Purpose;

@Purpose("Bitmap information.")
public class BitmapInformation {

    @BoundNumber(size = "32")
    private long headerSize;

    @BoundNumber(size = "32")
    private long bitmapWidth;

    @BoundNumber(size = "32")
    private long bitmapHeight;

    @BoundNumber(size = "16")
    private int numberOfColorPlanes;

    @BoundNumber(size = "16")
    private int colorDepth;

    @BoundNumber(size = "32")
    private long compressionMethod;

    @BoundNumber(size = "32")
    private long imageSize;

    @BoundNumber(size = "32")
    private long horizontalResolution;

    @BoundNumber(size = "32")
    private long verticalResolution;

    @BoundNumber(size = "32")
    private long numberOfColors;

    @BoundNumber(size = "32")
    private long numberOfImportantColors;

    public long getHeight() {
        return bitmapHeight;
    }

    public long getWidth() {
        return bitmapWidth;
    }

}
