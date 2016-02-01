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
#set($symbol_pound='#')
        #set($symbol_dollar='$')
        #set($symbol_escape='\' )
        package ${packageInPathFormat};

import org.junit.Test;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import org.codehaus.preon.Codecs;
import org.codehaus.preon.Codec;
import org.codehaus.preon.DecodingException;
import ${packageInPathFormat}.Image;

public class ImageTest {

    @Test
    public void shouldDecodeCorrectly() throws DecodingException {
        Codec<Image> codec = Codecs.create(Image.class);
        byte[] buffer = new byte[]{
                0, 0, 0, 1,
                0, 0, 0, 1,
                1, 2, 3
        };
        Image image = Codecs.decode(codec, buffer);
        assertThat(image.getHeight(), is(1));
        assertThat(image.getWidth(), is(1));
        assertThat(image.getPixels().length, is(not(nullValue())));
        assertThat(image.getPixels().length, is(1));
        assertThat(image.getPixels()[0].getRed(), is((byte) 1));
        assertThat(image.getPixels()[0].getGreen(), is((byte) 2));
        assertThat(image.getPixels()[0].getBlue(), is((byte) 3));
    }

}
