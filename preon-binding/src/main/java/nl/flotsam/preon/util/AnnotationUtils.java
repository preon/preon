package nl.flotsam.preon.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;

public class AnnotationUtils {

    /**
     * Compares two AnnotatedElement instances to see if they basically define
     * the same data. Currently ignores array type of elements.
     * 
     * @param first
     *            The first {@link AnnotatedElement}.
     * @param second
     *            The second {@link AnnotatedElement}
     * @return A boolean indicating whether or not these are considered to be
     *         equivalent.
     */
    public static boolean equivalent(AnnotatedElement first,
            AnnotatedElement second) {
        if (first == null || second == null || first.getAnnotations() == null
                || second.getAnnotations() == null) {
            return false;
        } else {
            if (first.getAnnotations().length != second.getAnnotations().length) {
                // If the number of annotations doesn't add up, there is nothing left to compare. 
                return false;
            } else {
                for (Annotation annotation : first.getAnnotations()) {
                    Annotation other = second.getAnnotation(annotation
                            .annotationType());
                    if (!annotation.equals(other)) {
                        return false;
                    }
                }
                return true;
            }
        }
    }

    public static int calculateHashCode(AnnotatedElement metadata) {
        if (metadata == null) {
            int result = 0;
            for (Annotation annotation : metadata.getAnnotations()) {
                result += annotation.hashCode();
            }
            return result;
        } else {
            return 0;
        }
    }

}
