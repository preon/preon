package nl.flotsam.preon.sample.bytecode;

import java.util.Collection;

import nl.flotsam.pecia.ParaContents;
import nl.flotsam.preon.Codec;
import nl.flotsam.preon.CodecSelector;
import nl.flotsam.preon.DecodingException;
import nl.flotsam.preon.Resolver;
import nl.flotsam.preon.buffer.BitBuffer;

public class AttributeInfoSelector implements CodecSelector {

    public void document(ParaContents<?> para) {
        // TODO Auto-generated method stub
        
    }

    public Collection<Codec<?>> getChoices() {
        return null;
    }

    public int getSize(Resolver resolver) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Codec<?> select(BitBuffer buffer, Resolver resolver) throws DecodingException {
        // TODO Auto-generated method stub
        return null;
    }

}
