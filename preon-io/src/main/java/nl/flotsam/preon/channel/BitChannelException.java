package nl.flotsam.preon.channel;

/**
 * The exception thrown when encountering an error while writing to a {@link nl.flotsam.preon.channel.BitChannel}.
 */
public class BitChannelException extends RuntimeException {
    
    public BitChannelException(String message) {
        super(message);
    }

}
