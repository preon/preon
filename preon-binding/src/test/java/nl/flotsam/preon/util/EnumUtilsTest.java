package nl.flotsam.preon.util;

import java.util.Map;

import junit.framework.TestCase;
import nl.flotsam.preon.annotation.BoundEnumOption;

public class EnumUtilsTest extends TestCase {

	public void testFullyDefinedEnum() {
		Map<Long, Direction> index = EnumUtils.getBoundEnumOptionIndex(Direction.class);
		assertEquals(Direction.Left, index.get(1L));
		assertEquals(Direction.Right, index.get(2L));
		assertEquals(null, index.get(3L));
	}
	
	public void testPartlyDefinedEnum() {
		Map<Long, Hours> index = EnumUtils.getBoundEnumOptionIndex(Hours.class);
		assertEquals(Hours.Working, index.get(1L));
		assertEquals(Hours.Leisure, index.get(2L));
		assertEquals(null, index.get(3L));
		assertEquals(Hours.Sleep, index.get(null));
	}
	
	public enum Direction {
		@BoundEnumOption(1)
		Left,
		
		@BoundEnumOption(2)
		Right;
	}
	
	public enum Hours {
		
		@BoundEnumOption(1)
		Working, 
		
		@BoundEnumOption(2)
		Leisure,
		
		Sleep;
		
		
	}
	

}
