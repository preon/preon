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
package org.codehaus.preon.codec;

import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.ResolverContext;
import org.codehaus.preon.annotation.BoundList;
import org.codehaus.preon.annotation.Choices;

import java.lang.reflect.AnnotatedElement;
import java.util.List;
import java.util.Map;

public class MapCodecFactory implements CodecFactory {

    private final CodecFactory codecFactory;

    public MapCodecFactory(CodecFactory codecFactory) {
        this.codecFactory = codecFactory;
    }

    public <T> Codec<T> create(AnnotatedElement metadata, Class<T> type, ResolverContext context) {
        if (Map.class.isAssignableFrom(type)) {
            BoundList boundList = metadata.getAnnotation(BoundList.class);
            if (boundList != null && typeIsGuaranteedToBeEntry(boundList)) {
                Codec<List> listCodec =
                        codecFactory.create(metadata, List.class, context);
                if (listCodec != null) {
                    return new ListBasedMapCodec(listCodec);
                }  else {
                    return null;
                }
            }
        }
        return null;
    }

    private boolean typeIsGuaranteedToBeEntry(BoundList boundList) {
        if (boundList.type() != null && Map.Entry.class.isAssignableFrom(boundList.type())) {
            return true;
        } else if (boundList.types() != null && boundList.types().length > 0) {
            boolean allGood = true;
            for (Class<?> type : boundList.types()) {
                allGood &= (type != null && Map.Entry.class.isAssignableFrom(type));
            }
            return allGood;
        } else if (boundList.selectFrom() != null && boundList.selectFrom().alternatives().length > 0) {
            boolean allGood = true;
            for (Choices.Choice choice : boundList.selectFrom().alternatives()) {
                allGood &= (choice != null && Map.Entry.class.isAssignableFrom(choice.type()));
            }
            return allGood;
        }
        return false;
    }

}
