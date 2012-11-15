package clj_kryo.support;

import java.io.ObjectStreamException;
import java.io.Serializable;
 
public final class KryoWrapper implements Serializable
{
  private byte[] bytes;

  public KryoWrapper(Object target)
  {
    bytes = KryoSerializer.write(target);
  }

  private Object readResolve() throws ObjectStreamException
  {
    return KryoSerializer.read(bytes);
  }
}
