import SourceProviderProtocol._
import SourceServiceProtocol.GetAllSources
import akka.actor.{Actor, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime

import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.{Duration, _}


object SourceProviderProtocol {

  case class Source(uri: String, contentLength: Long, lastChecked: DateTime)

  case class AddSource(source: Source)

  case class QueueSources()

}

class SourceProvider extends Actor with LazyLogging {
  implicit val timeout = Timeout(5 seconds)

  private val sourceService = context.actorOf(Props[SourceService])
  private val sources = mutable.Queue[Source]()

  override def receive: Receive = {
    case AddSource(newSource) => sources.enqueue(newSource)
    case _: QueueSources => queueSources()
  }

  def getNextSource: Source = {
    val nextSource = sources.dequeue()
    sources.enqueue(nextSource)
    nextSource
  }

  def queueSources(): Unit = {
    val allSources = sourceService ? GetAllSources
    val sourcesToQueue = Await.result(allSources, Duration.Inf).asInstanceOf[Seq[Source]]
    sourcesToQueue.foreach(sources.enqueue(_))
  }
}
