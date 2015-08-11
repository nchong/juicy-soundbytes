import java.util.concurrent.{BlockingQueue, LinkedBlockingQueue}
import java.io.ByteArrayOutputStream

object JuicySoundbyte extends App {
  val queue = new LinkedBlockingQueue[ByteArrayOutputStream]()
  val producer = new Recorder(5/*seconds*/, queue)
  new Thread(producer).start()
}
