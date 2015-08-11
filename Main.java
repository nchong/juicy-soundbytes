import java.io.*;
import java.util.concurrent.*;

public class Main {
  public static void main(String[] args) {
    BlockingQueue<ByteArrayOutputStream> queue = new LinkedBlockingQueue<ByteArrayOutputStream>();
    RecordStage producer = new RecordStage(5/*seconds*/, queue);
    //Runnable consumer = new Transcriber(queue);
    Runnable consumer = new ToWavStage(queue);
    new Thread(producer).start();
    new Thread(consumer).start();
  }
}