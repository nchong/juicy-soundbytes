import java.io.*;
import java.util.concurrent.BlockingQueue;

public class ToWavStage implements Runnable {
  private BlockingQueue<ByteArrayOutputStream> queue;
  public void ToWavStage(BlockingQueue<ByteArrayOutputStream> queue) {
    this.queue = queue;
  }

  // @override
  public void run() {
    int n = 0;
    try {
      while (true) {
        ByteArrayOutputStream wav = queue.take();
        System.out.println("Consuming " + n + " packet");
        FileOutputStream fos = new FileOutputStream(n + ".wav");
        wav.writeTo(fos);
        fos.close();
        n++;
      }
    } catch (IOException e) {
      System.out.println("ERROR: IOException!");
      return;
    } catch (InterruptedException e) {
      System.out.println("ERROR: InterruptedException!");
      return;
    }
  }
}
