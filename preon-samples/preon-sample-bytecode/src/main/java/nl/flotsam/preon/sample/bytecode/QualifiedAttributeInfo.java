package nl.flotsam.preon.sample.bytecode;

import nl.flotsam.preon.annotation.BoundNumber;
import nl.flotsam.preon.buffer.ByteOrder;

/**
 * 
 * @author Wilfred Springer (wis)
 *
 */
public class QualifiedAttributeInfo {

    @BoundNumber(size = "32", byteOrder = ByteOrder.BigEndian)
    private long attributeLength;
    
}
