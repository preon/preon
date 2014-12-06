/**
 * LabSET 2014
 */
package org.codehaus.preon.codec;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.codehaus.preon.*;
import org.codehaus.preon.annotation.Inject;
import org.codehaus.preon.buffer.BitBuffer;
import org.codehaus.preon.channel.BitChannel;
import org.codehaus.preon.el.Expression;

/**
 * A decorator that will inspect all fields on the object constructed by the
 * {@link Codec} to be decorated, and create a decorated Codec that will inject
 * previously decoded components from the {@link ResolverContext} into the
 * fields with {@link Inject} annotation
 * 
 * @author hasnaer
 */
public class InjectCodecDecorator implements CodecDecorator {

  @Override
  public <T> Codec<T> decorate(Codec<T> codec, AnnotatedElement metadata,
      Class<T> type, ResolverContext context) {
    List<Field> fields = new ArrayList<>();
    for (Field field : type.getDeclaredFields()) {
      if (field.isAnnotationPresent(Inject.class)) {
        field.setAccessible(true);
        fields.add(field);
      }
    }
    return new InjectCodec<T>(codec, fields);
  }

  private class InjectCodec<T> implements Codec<T> {

    private Codec<T> codec;
    private List<Field> fields;
    private ResolverContext context;

    public InjectCodec(Codec<T> pCodec, List<Field> pFields) {
      codec = pCodec;
      fields = pFields;
    }

    @Override
    public T decode(BitBuffer buffer, Resolver resolver, Builder builder)
        throws DecodingException {
      final T result = codec.decode(buffer, resolver, builder);
      if (result != null) {
        for (Field field : fields) {
          try {
            field.set(result,
              resolver.get(field.getAnnotation(Inject.class).name()));
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
      return result;
    }

    @Override
    public void encode(T value, BitChannel channel, Resolver resolver)
        throws IOException {
      codec.encode(value, channel, resolver);
    }

    @Override
    public Expression<Integer, Resolver> getSize() {
      return codec.getSize();
    }

    @Override
    public CodecDescriptor getCodecDescriptor() {
      return codec.getCodecDescriptor();
    }

    @Override
    public Class<?>[] getTypes() {
      return codec.getTypes();
    }

    @Override
    public Class<?> getType() {
      return codec.getType();
    }

  }

}