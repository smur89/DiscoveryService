import SourceServiceProtocol.UpdateSource
import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging

object SourceServiceProtocol {

  case class UpdateSource(source: Source)

}

class SourceService extends Actor with LazyLogging {

  def updateSource(source: Source): Unit = {}

  override def receive: Receive = {
    case UpdateSource(source) => updateSource(source)
  }


}
