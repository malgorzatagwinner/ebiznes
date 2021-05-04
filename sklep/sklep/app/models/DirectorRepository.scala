package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class DirectorData(name: String)
object DirectorData {
  implicit val directorDataFormat = Json.format[DirectorData]
}

case class Director(name: String, id: Long = 0L)

object Director {
  implicit val directorFormat = Json.format[Director]
}

@Singleton
class DirectorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class DirectorsTable(tag: Tag) extends Table[Director](tag, "Directors") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (name, id) <> ((Director.apply _).tupled, Director.unapply)
  }

  private val directors = TableQuery[DirectorsTable]

  def getAll(): Future[Seq[Director]] = db.run {
    directors.result
  }

  def create(director: DirectorData): Future[Director] = db.run {
    (directors.map(_.name)
      returning directors.map(_.id)
      into ((name, id) => Director(name, id))
    ) += (director.name)
  }

  def getById(id: Long): Future[Option[Director]] = db.run {
    directors.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, director: DirectorData): Future[Int] = db.run {
    directors.filter(_.id === id).map(a => (a.name).mapTo[DirectorData]).update(director)
  }

  def deleteById(id: Long): Future[Int] = db.run {
    directors.filter(_.id === id).delete
  }
}
