import java.io.ByteArrayOutputStream
import java.util.concurrent.BlockingQueue

class Player(
    queue: BlockingQueue[ByteArrayOutputStream]
) extends Runnable {

  def run() {
    var n = 0
    while (true) {
      val wav = queue.take()
      println("Consuming " + n + " packet")
      n += 1
    }
  }
}
