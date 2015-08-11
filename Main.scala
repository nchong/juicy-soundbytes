import java.io.{File, IOException}
import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import java.io.ByteArrayOutputStream

object JuicySoundbyte extends App {
  val queue = new LinkedBlockingQueue[ByteArrayOutputStream]()
  val producer = new Recorder(5/*seconds*/, queue)
  val consumer = new Player(queue)
  new Thread(producer).start()
  new Thread(consumer).start()
}
