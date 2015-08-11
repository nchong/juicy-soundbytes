import java.io.*;
import java.util.concurrent.*;
import org.apache.commons.io.IOUtils;

public class TestLocal {
  public static void main(String[] args) throws Exception {
    Transcriber consumer = new Transcriber(null);
    FileInputStream fis = new FileInputStream("0.wav");
    ByteArrayOutputStream wav = new ByteArrayOutputStream();
    IOUtils.copy(fis, wav);
    consumer.process(wav);
  }
}