package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class GenreData(name: String)
object GenreData {
  implicit val genreDataFormat = Json.format[GenreData]
}

case class Genre(name: String, id: Long = 0L)

object Genre {
  implicit val genreFormat = Json.format[Genre]
}

@Singleton
class GenreRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class GenresTable(tag: Tag) extends Table[Genre](tag, "Genres") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (name, id) <> ((Genre.apply _).tupled, Genre.unapply)
  }

  private val genres = TableQuery[GenresTable]

  def getAll(): Future[Seq[Genre]] = db.run {
    genres.result
  }

  def create(genre: GenreData): Future[Genre] = db.run {
    (genres.map(_.name)
      returning genres.map(_.id)
      into ((name, id) => Genre(name, id))
    ) += (genre.name)
  }

  def getById(id: Long): Future[Option[Genre]] = db.run {
    genres.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, genre: GenreData): Future[Int] = db.run {
    genres.filter(_.id === id).map(a => (a.name).mapTo[GenreData]).update(genre)
  }

  def deleteById(id: Long): Future[Int] = db.run {
    genres.filter(_.id === id).delete
  }
}
