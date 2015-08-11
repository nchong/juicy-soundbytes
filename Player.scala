import java.io.{File, IOException}
import java.io.FileOutputStream
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream
import java.util.concurrent.BlockingQueue
//import sun.audio.{AudioPlayer, AudioStream}

class Player(
    queue: BlockingQueue[ByteArrayOutputStream]
) extends Runnable {

  def run() {
    var n = 0
    while (true) {
      val wav = queue.take()
      println("Consuming " + n + " packet")
      val fos = new FileOutputStream(n + ".wav")
      wav.writeTo(fos)
      fos.close()
      //val audioStream = new AudioStream(new ByteArrayInputStream(wav.toByteArray()))
      //AudioPlayer.player.start(audioStream)
      n += 1
    }
  }
}
