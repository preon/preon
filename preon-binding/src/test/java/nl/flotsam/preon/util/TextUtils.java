package nl.flotsam.preon.util;

import nl.flotsam.limbo.Descriptive;
import nl.flotsam.limbo.util.StringBuilderDocument;

/**
 * A collection of text utilities.
 * 
 * @author Wilfred Springer (wis)
 * 
 */
public class TextUtils {

    /**
     * Describes a {@link Descriptive} element by calling its
     * {@link Descriptive#document(nl.flotsam.limbo.Document)} operation, and
     * turning the results back into a String.
     * 
     * @param descriptive The object to be described.
     * @return A String.
     */
    public static String toString(Descriptive descriptive) {
        StringBuilder builder = new StringBuilder();
        descriptive.document(new StringBuilderDocument(builder));
        return builder.toString();
    }

}
