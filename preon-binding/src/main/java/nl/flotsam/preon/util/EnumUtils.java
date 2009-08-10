package nl.flotsam.preon.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import nl.flotsam.preon.annotation.BoundEnumOption;

import edu.emory.mathcs.backport.java.util.Collections;

public class EnumUtils {

	@SuppressWarnings("unchecked")
	public static <T> Map<Long, T> getBoundEnumOptionIndex(Class<T> enumType) {
		if (!enumType.isEnum()) {
			return Collections.emptyMap();
		}
		else {
			Map<Long, T> result = new HashMap<Long, T>();
			Field[] fields = enumType.getFields();
			for (Field field : fields) {
				if (field.isEnumConstant()) {
					try {
						field.setAccessible(true);
						BoundEnumOption annotation = field
								.getAnnotation(BoundEnumOption.class);
						if (annotation == null) {
							result.put(null, (T) field.get(null));
						}
						else {
							result.put(annotation.value(), (T) field.get(null));
						}
					}
					catch (IllegalAccessException iae) {
						iae.printStackTrace(); // Should never happen.
					}
				}
			}
			return result;
		}
	}

}
