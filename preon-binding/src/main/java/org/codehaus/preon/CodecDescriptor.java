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
package org.codehaus.preon;

import nl.flotsam.pecia.Documenter;
import nl.flotsam.pecia.ParaContents;
import nl.flotsam.pecia.SimpleContents;

public interface CodecDescriptor {

    /** An enumeration with different adjectives. */
    public enum Adjective {
        A, THE, NONE;

        public String asTextPreferA(boolean startWithCapital) {
            if (startWithCapital) {
                switch (this) {
                    case A:
                        return "A ";
                    case THE:
                        return "The ";
                    default:
                        return "";
                }
            } else {
                switch (this) {
                    case A:
                        return "a ";
                    case THE:
                        return "the ";
                    default:
                        return "";
                }
            }
        }

        public String asTextPreferAn(boolean startWithCapital) {
            if (startWithCapital) {
                switch (this) {
                    case A:
                        return "An ";
                    case THE:
                        return "The ";
                    default:
                        return "";
                }
            } else {
                switch (this) {
                    case A:
                        return "an ";
                    case THE:
                        return "the ";
                    default:
                        return "";
                }
            }
        }

    }

    /**
     * Returns an object capable of writing a one-line summary of the data structure. Expect the summary to be printed
     * at the beginning of a paragraph, but make sure the paragraph is ended in such a way that more lines might be
     * appended to that paragraph, if required, by some other component. I.e. make sure you end with a dot-space. (". ")
     * Typically starts with {@link Adjective#A}. Will often rely on {@link #writeReference(ParaContents, Adjective)}
     * for its implementation.
     */
    <C extends ParaContents<?>> Documenter<C> summary();

    /**
     * Returns an object capable of rendering a short reference to the type of data for which the Codec provides the
     * decoder. This reference should <em>at least</em> include a reference to the type of data decoded by 'sub'-Codecs.
     * The {@link Adjective} argument allows the implementor to generate a correct reference, such as 'a list' instead
     * of 'an list'.
     * <p/>
     * <p> Note that implementers should assume that the particular piece of data that is going to be referenced here
     * will be detailed further along the road. Unless {@link #requiresDedicatedSection()} returns <code>true</code>,
     * that could be within the same section. </p>
     *
     * @param adjective        The adjective to use; <code>null</code> if no adjective should be used.
     * @param startWithCapital TODO
     */
    <C extends ParaContents<?>> Documenter<C> reference(Adjective adjective,
                                                        boolean startWithCapital);

    /**
     * Returns an object capable of writing detailed information on the format to the document section passed in.
     * Typically implemented by writing a (couple of) paragraph(s), and forwarding to the CodecDescriptor of a nested
     * {@linkplain Codec}. Note that - while forwarding - the descriptor has the option to replace the way the buffer is
     * referenced.
     *
     * @param bufferReference A String based human readable reference to the encoded data.
     */
    <C extends SimpleContents<?>> Documenter<C> details(String bufferReference);

    /**
     * Returns a boolean indicating if the type of data for which the Codec provides the decoder should be documented in
     * a dedicated section.
     *
     * @return A boolean indicating if the type of data for which the Codec provides the decoder should be documented in
     *         a dedicated section: <code>true</code> if it does; <code>false</code> if it doesn't.
     */
    boolean requiresDedicatedSection();

    /**
     * Returns the title of the section to be rendered, in case {@link #requiresDedicatedSection()} returns
     * <code>true</code>.
     *
     * @return The title of the section to be rendered, in case {@link #requiresDedicatedSection()} returns
     *         <code>true</code>.
     */
    String getTitle();

}
