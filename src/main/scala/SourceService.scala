import java.sql.{Connection, DriverManager, ResultSet}

import SourceProviderProtocol.Source
import SourceServiceProtocol.{GetAllSources, UpdateSource}
import akka.actor.Actor
import com.typesafe.scalalogging.LazyLogging
import org.apache.commons.dbutils.DbUtils
import org.joda.time.DateTime

object SourceServiceProtocol {

  case class UpdateSource(source: Source)

  case class GetAllSources()

}

class SourceService extends Actor with LazyLogging {

  private val connection = getConnecton
  val SELECT_SOURCE = "SELECT sfd.sources_id, sfd.sources_loc, sfd.source_name, sfd.sources_loc_split_txt," +
    " sfd.sources_type, sfd.title_split_text, sfd.title_split_text_direction, sfd.feed_text_size, sfd.links_hashes," +
    " sfd.is_active, UNIX_TIMESTAMP(sfd.manually_updated_at) AS manually_updated_at, scl.sources_categories_id," +
    " c.high_level_cat_id, sfd.feed_ip, sfd.country_code, sfd.language_code, sfd.redirect_url, sfd.status_code," +
    " sfd.exception_name, sfd.exception_message, sfd.enable_language_detection, sfd.css_blacklist" +
    " FROM sources_for_discovery AS sfd "

  def updateSource(source: Source): Unit = {

  }

  def getAllSources: Seq[Source] = {
    val resultSet = connection.prepareStatement(SELECT_SOURCE).executeQuery()
    val sources = results(resultSet)(rs =>
      Source(rs.getString("sources_loc"), rs.getLong("???"), new DateTime(rs.getDate("updated_at"))))
    sources.toSeq
  }

  def results[T](resultSet: ResultSet)(f: ResultSet => T): Iterator[T] = {
    new Iterator[T] {
      def hasNext: Boolean = resultSet.next()

      def next(): T = f(resultSet)
    }
  }

  override def receive: Receive = {
    case UpdateSource(source) => updateSource(source)
    case GetAllSources() => sender ! getAllSources
  }

  def getConnecton: Connection = {
    val url = "jdbc:mysql://localhost:3306/test"
    val driver = "com.mysql.jdbc.Driver"
    val usr = "root"
    val pwd = "root"

    DbUtils.loadDriver(driver)
    DriverManager.getConnection(url, usr, pwd)
  }

}
