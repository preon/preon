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

import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.preon.Codec;
import org.codehaus.preon.CodecFactory;
import org.codehaus.preon.ResolverContext;


/**
 * A {@link CodecFactory} capable of generating {@link Codec Codecs} by delegating to a sorted list of other {@link
 * CodecFactory CodecFactories}.
 *
 * @author Wilfred Springer
 */
public class CompoundCodecFactory implements CodecFactory {

    /** The sorted list of {@link CodecFactory CodecFactories} to which this {@link CodecFactory} will delegate. */
    private final List<CodecFactory> factories = new ArrayList<CodecFactory>();

    public <T> Codec<T> create(AnnotatedElement overrides, Class<T> type,
                               ResolverContext context) {
        Codec<T> result = null;
        for (CodecFactory delegate : factories) {
            result = delegate.create(overrides, type, context);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    public void add(CodecFactory factory) {
        factories.add(factory);
    }

    public void remove(CodecFactory factory) {
        factories.remove(factory);
    }

}
