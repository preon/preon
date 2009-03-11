/*
 * Copyright (C) 2008 Wilfred Springer
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
 * Preon; see the file COPYING. If not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
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

package nl.flotsam.preon;

import nl.flotsam.pecia.Contents;
import nl.flotsam.pecia.ParaContents;

/**
 * The interface to be implemented by objects supporting the creation of
 * documentation of a {@link Codec}.
 * 
 * 
 * @author Wilfred Springer
 * 
 */
public interface CodecDescriptor {

    /**
     * Write a reference to the data captured by this {@link Codec} to the
     * object passed in. The reference typically starts with "a ..." or
     * "the ...".
     * 
     * @param <T>
     *            The type of document element holding this {@link ParaContents}
     *            object.
     * @param contents
     *            The object receiving the content generated.
     */
    <T> void writeReference(ParaContents<T> contents);

    /**
     * Returns a short label of the data decoded. Typically used for generating
     * headings.
     * 
     * @return A short label of the data decoded.
     */
    String getLabel();

    /**
     * Generates a short description of the data decoded using the {@link Codec}
     * .
     * 
     * @param <T>
     * @param <V>
     * @param para
     *            The paragraph in which the one line description should be
     *            generated.
     * @return The same object as the one passed in.
     */
    <T, V extends ParaContents<T>> V putOneLiner(V para);

    /**
     * Returns a boolean allowing the Codec to indicate whether or not the Codec
     * expects it needs a dedicated top level section in the output document. If
     * this method returns <code>true</code>, then the
     * {@link #putOneLiner(ParaContents)} content is expected to generate a
     * reference to that dedicated section. (Basically, if
     * {@link Codecs#document(Codec, nl.flotsam.pecia.builder.ArticleDocument)}
     * encounters a Codec returning <code>true</code> in this method, then it
     * will generate a new section, use the result of {@link #getLabel()} as the
     * section's heading, and {@link #putFullDescription(Contents)} as the body
     * of that section.)
     * 
     * @return A boolean indicating whether the Codec demands its own dedicated
     *         section.
     */
    boolean hasFullDescription();

    /**
     * Generates the full description of the data decoded.
     * 
     * @param <T>
     * @param contents
     *            The object receiving that description.
     * @return The same object as the one passed in.
     */
    <T> Contents<T> putFullDescription(Contents<T> contents);

}
