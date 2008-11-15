public interface CodecFactory {
    <T> Codec<T> create(AnnotatedElement metadata, 
                        Class<T> type,
                        ResolverContext context);
}
