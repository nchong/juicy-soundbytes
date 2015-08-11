import java.io.{File, IOException}
import java.util.concurrent.BlockingQueue
import javax.sound.sampled.{AudioFileFormat, AudioFormat, AudioInputStream}
import javax.sound.sampled.{AudioSystem, DataLine, LineUnavailableException, TargetDataLine}
import java.io.ByteArrayOutputStream

class Recorder(
    numSecondsPerPacket: Int,
    outputQueue: BlockingQueue[ByteArrayOutputStream]
) extends Runnable {

  val SAMPLE_RATE = 16000f
  var isRunning = true
  val format = new AudioFormat(/*sampleRate =*/ SAMPLE_RATE,
    /*sampleSizeInBits =*/ 16,
    /*channels =*/ 2,
    /*signed =*/ true,
    /*bigEndian =*/ true)
  val info = new DataLine.Info(classOf[TargetDataLine], format)
  val fileType = AudioFileFormat.Type.WAVE

  def stop() {
    isRunning = false
  }

  def run() {
    if (!AudioSystem.isLineSupported(info)) {
      throw new IOException("Line not supported")
    }
    val line: TargetDataLine = AudioSystem.getLine(info).asInstanceOf[TargetDataLine]
    line.open(format)
    line.start() // start capturing
    val source = new AudioInputStream(line)
    var n = 0
    while (isRunning) {
      try {
        // continuously record numSecondsPerPacket chunks of audio
        val outputPacket = new ByteArrayOutputStream()
        println("Writing " + n + " packet")
        AudioSystem.write(new AudioInputStream(source, format, numSecondsPerPacket * SAMPLE_RATE) , fileType, outputPacket)
        queue.put(outputPacket)
        n += 1
      } catch {
        case ex: LineUnavailableException => ex.printStackTrace()
        case ioe: IOException => ioe.printStackTrace()
      }
    }
  }
}
