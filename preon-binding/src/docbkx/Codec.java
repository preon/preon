public interface Codec<T> {
    T decode(BitBuffer buffer, Resolver resolver, Builder builder) 
        throws DecodingException;
    int getSize(Resolver resolver);
    Expression<Integer, Resolver> getSize();
    CodecDescriptor getCodecDescriptor();
    Class<?>[] getTypes();
    Class<?> getType();
}
