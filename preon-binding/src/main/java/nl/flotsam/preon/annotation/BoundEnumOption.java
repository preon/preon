package nl.flotsam.preon.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nl.flotsam.preon.codec.EnumCodecFactory;

/**
 * An annotation used to annotate enumeration values, in order to make sure they can be
 * mapped to long values read from the {@link BitBuffer}.
 * 
 * @see EnumCodecFactory
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface BoundEnumOption {

	/**
	 * The long value read from the {@link BitBuffer}.
	 */
	long value();

}
