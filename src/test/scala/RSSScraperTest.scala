
import java.io.File

import SourceProviderProtocol.Source
import akka.actor.ActorSystem
import akka.testkit.TestActorRef
import org.joda.time.{DateTime, DateTimeZone}
import org.jsoup.Jsoup
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar

class RSSScraperTest extends FunSuite with MockitoSugar {

  implicit val system = ActorSystem()
  private val actorRef = TestActorRef[RSSScraper]
  private val rssScraper = actorRef.underlyingActor

  test("crawlSource") {
    val source = Source("http://pitchfork.com/rss/reviews/best/albums/", 0L, new DateTime(DateTimeZone.UTC))
    val crawledSource = rssScraper.crawlSource(source)
  }

  test("getRssItems") {
    val pitchforkRssFile = new File(getClass.getResource("/pitchforkBestNewAlbumsRssFeed20170311.xml").getFile)
    val source = Jsoup.parse(pitchforkRssFile, "UTF-8")
    val rssItems = rssScraper.parseRssPageDocument(source)
    assert(rssItems.size == 25)
  }
}
