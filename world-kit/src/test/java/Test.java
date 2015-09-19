import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Test
{
  // private static final int COUNT = 2000000;
  
  public static void main(String[] args)
  {
    new Test();
  }
  
  public Test()
  {
    String text = "Hallo Welt";
    
    Phase phase = () ->
    {
      System.out.println(text);
      
      return null;
    };
    
    byte[] bytes = null;
    
    // serialize the object
    try
    {
      ByteArrayOutputStream bo = new ByteArrayOutputStream();
      ObjectOutputStream so = new ObjectOutputStream(bo);
      so.writeObject(phase);
      so.flush();
      bytes = bo.toByteArray();
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
    
    // deserialize the object
    try
    
    {
      ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
      ObjectInputStream si = new ObjectInputStream(bi);
      Phase obj = (Phase) si.readObject();
      
      obj.get();
    }
    catch (Exception e)
    {
      System.out.println(e);
    }
  }
  
  protected interface Phase extends Serializable
  {
    Phase get() throws InterruptedException;
  }
  
}
