import RSSScraperProtocol.Url
import SourceProviderProtocol.Source
import akka.actor.{Actor, Props}
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.{DateTime, DateTimeZone}
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._

object RSSScraperProtocol {
  case class Url(title: String, link: String, description: String)
}

class RSSScraper extends Actor with LazyLogging {

  private val sourceService = context.actorOf(Props[SourceService])

  override def receive: Receive = {
    case _: Source => crawlSource(_)
  }

  def crawlSource(source: Source): Unit = {
    val response = Jsoup.connect(source.uri)
      .header("Accept",
        "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
      .header("Accept-Language", "en-US,en;q=0.5")
      .header("Connection", "keep-alive")
      .ignoreContentType(true)
      .parser(Parser.xmlParser())
      .execute()
    if (updateSource(source, response)) {
      val document = response.parse()
      parseRssPageDocument(document)
    }
  }

  def parseRssPageDocument(document: Document): Seq[Url] = {
    val rssItems = document.select("item").asScala
    for (item <- rssItems) yield {
      Url(item.select("title").asScala.head.text,
        item.select("link").first().nextSibling().toString,
        Jsoup.parse(item.select("description").asScala.head.text).text)
    }
  }

  private def updateSource(source: Source, response: Connection.Response): Boolean = {
    val cookies = response.cookies()
    val contentType = response.contentType()
    val contentLength = response.header("Content-Length").toInt
    sourceService ! Source(source.uri, contentLength, new DateTime(DateTimeZone.UTC))
    contentLength != source.contentLength
  }
}
