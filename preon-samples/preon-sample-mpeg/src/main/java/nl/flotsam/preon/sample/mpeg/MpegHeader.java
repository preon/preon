package nl.flotsam.preon.sample.mpeg;

import nl.flotsam.preon.annotation.Bound;
import nl.flotsam.preon.annotation.BoundNumber;

public class MpegHeader {

    @BoundNumber(size="11", match="0b11111111111")
    private int frameSync;
    
    @BoundNumber(size="2")
    private int mpegAudioVersionId;
    
    @BoundNumber(size="2")
    private int layerDescription;
    
    @Bound
    private boolean crcProtected;
    
    @BoundNumber(size="4")
    private int bitRateIndex;
    
    @BoundNumber(size="2")
    private int sampleRateFrequencyIndex;
    
    @Bound
    private boolean padded;
    
    @Bound
    private boolean privateBit;
    
    @BoundNumber(size="2")
    private int channelMode;
    
    @BoundNumber(size="2")
    private int modeExtension;
    
    @Bound
    private boolean copyright;
    
    @Bound
    private boolean original;
    
    @BoundNumber(size="2")
    private int emphasis;
    
}
