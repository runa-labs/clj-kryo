package clj_kryo.support;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
 
public final class KryoSerializer
{
  private static Kryo kryo;
 
  public static void setKryo(Kryo aKryo)
  {
    kryo = aKryo;
  }

  public static byte[] write(Object obj)
  {
    ByteArrayOutputStream bos = new ByteArrayOutputStream();
    Output out = new Output(bos);
    try {
      kryo.writeClassAndObject(out, obj);
    } finally {
      out.close();
    }
    return bos.toByteArray();
  }

  public static Object read(byte[] bytes)
  {
    ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    Input in = new Input(bis);
    try {
      return kryo.readClassAndObject(in);
    } finally {
      in.close();
    }
  }
}
