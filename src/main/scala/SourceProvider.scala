import SourceProviderProtocol.{Fail, OK}
import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, DateTimeZone}

case class Source(uri: String, contentLength: Long, lastChecked: DateTime)

object SourceProviderProtocol {

  abstract class Response() {
    val message: String
    val discoveredUrlsCount: Int
    val source: Source
  }

  case class OK(message: String, discoveredUrlsCount: Int)

  case class Fail(message: String, discoveredUrlsCount: Int, source: Source) extends Response

}

class SourceProvider extends Actor with LazyLogging {

  def getNextSource: Source = {
    Source("http://pitchfork.com/rss/reviews/best/albums/", 0L, new DateTime(DateTimeZone.UTC))
  }

  override def receive: Receive = {
    case _: OK => processSuccessfullyCrawledSource(_)
    case Fail => logger.error("Something went wrong!")
    case _ => logger.error("Unexpected Message received")
  }

  private def processSuccessfullyCrawledSource(response: OK) = {
    logger.info("Source Processed OK")
  }
}
