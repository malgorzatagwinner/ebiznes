package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class ActorData(name: String)
object ActorData {
  implicit val ActorDataFormat = Json.format[ActorData]
}

case class Actor(name: String, id: Long = 0L)

object Actor {
  implicit val ActorFormat = Json.format[Actor]
}

@Singleton
class ActorRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

   class ActorsTable(tag: Tag) extends Table[Actor](tag, "Actors") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")

    def * = (name, id) <> ((Actor.apply _).tupled, Actor.unapply)
  }

  private val actors = TableQuery[ActorsTable]

  def getAll(): Future[Seq[Actor]] = db.run {
    actors.result
  }

  def create(actor: ActorData): Future[Actor] = db.run {
    (actors.map(_.name)
      returning actors.map(_.id)
      into ((name, id) => Actor(name, id))
    ) += (actor.name)
  }

  def getById(id: Long): Future[Option[Actor]] = db.run {
    actors.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, actor: ActorData): Future[Int] = db.run {
    actors.filter(_.id === id).map(a => (a.name).mapTo[ActorData]).update(actor)
  }

  def deleteById(id: Long): Future[Int] = db.run {
    actors.filter(_.id === id).delete
  }
}
