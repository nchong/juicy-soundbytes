import java.io.*;
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class RecordStage implements Runnable {
  private int numSecondsPerPacket;
  private BlockingQueue<ByteArrayOutputStream> queue;
  private final float SAMPLE_RATE = 16000f;
  private boolean isRunning = true;
  private AudioFormat format;
  private DataLine.Info info;
  private final AudioFileFormat.Type FILE_TYPE = AudioFileFormat.Type.WAVE;

  public void RecordStage(int numSecondsPerPacket, BlockingQueue<ByteArrayOutputStream> queue) {
    this.numSecondsPerPacket = numSecondsPerPacket;
    this.queue = queue;
    this.format = new AudioFormat(/*sampleRate =*/ SAMPLE_RATE,
      /*sampleSizeInBits =*/ 16,
      /*channels =*/ 2,
      /*signed =*/ true,
      /*bigEndian =*/ true);
    this.info = new DataLine.Info(TargetDataLine.class, format);
  }

  // @override
  public void run() {
    if (!AudioSystem.isLineSupported(info)) {
      System.out.println("ERROR: Line not supported!");
      return;
    }
    TargetDataLine line = null;
    try {
      line = (TargetDataLine) AudioSystem.getLine(info);
      line.open(format);
      line.start();
      AudioInputStream source = new AudioInputStream(line);
      int n = 0;
      // continuously record numSecondsPerPacket chunks of audio
      while (isRunning) {
        ByteArrayOutputStream outputPacket = new ByteArrayOutputStream();
        System.out.println("Producing " + n + " packet");
        AudioSystem.write(
          new AudioInputStream(source, format, (long)(numSecondsPerPacket * SAMPLE_RATE)),
          FILE_TYPE,
          outputPacket);
        queue.put(outputPacket);
        n++;
      }
    } catch (IOException e) {
      System.out.println("ERROR: IOException!");
      return;
    } catch (InterruptedException e) {
      System.out.println("ERROR: InterruptedException!");
      return;
    } catch (LineUnavailableException e) {
      System.out.println("ERROR: LineUnavailableException!");
      return;
    } finally {
      if (line != null) line.close();
    }
  }
}
