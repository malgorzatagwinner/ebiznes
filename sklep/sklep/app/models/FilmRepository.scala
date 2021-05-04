package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class FilmData(name: String, genre_id: Long, director_id: Long, actor_id: Long)
object FilmData {
  implicit val FilmDataFormat = Json.format[FilmData]
}

case class Film(id: Long = 0L, name: String, genre_id: Long, director_id: Long, actor_id: Long)

object Film {
  implicit val FilmFormat = Json.format[Film]
}

@Singleton
class FilmRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val actorRepository: ActorRepository, val directorRepository: DirectorRepository, val genreRepository: GenreRepository )(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class FilmsTable(tag: Tag) extends Table[Film](tag, "Films") {
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def name = column[String]("name")
    
    def actor_id = column[Long]("actor_id")
    def actor_fk = foreignKey("actor_fk", actor_id, actor_)(_.id)
    
    def director_id = column[Long]("director_id")
    def director_fk = foreignKey("director_fk", director_id, director_)(_.id)
    
    def genre_id = column[Long]("genre_id")
    def genre_fk = foreignKey("genre_fk", genre_id, genre_)(_.id)

    
    
    def * = (id, name, genre_id, director_id, actor_id) <> ((Film.apply _).tupled, Film.unapply)
  }

  import actorRepository.ActorsTable
  import directorRepository.DirectorsTable
  import genreRepository.GenresTable
  
  val films = TableQuery[FilmsTable]
  val actor_ = TableQuery[ActorsTable]
  val director_ = TableQuery[DirectorsTable]
  val genre_ = TableQuery[GenresTable]
      
  
  def getAll(): Future[Seq[Film]] = db.run {
    films.result
  }

  def create(film: FilmData): Future[Film] = db.run {
    (films.map( f => (f.name, f.genre_id, f.director_id, f.actor_id))
      returning films.map(_.id)
      into {case (( name, genre_id, director_id, actor_id), id) => Film(id, name, genre_id, director_id, actor_id)}
    ) += (( film.name, film.genre_id, film.director_id, film.actor_id))
}
  def getById(id: Long): Future[Option[Film]] = db.run {
    films.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, film: FilmData): Future[Int] = db.run {
    films.filter(_.id === id).map(f => (f.name, f.genre_id, f.director_id, f.actor_id) <> ((FilmData.apply _).tupled, FilmData.unapply)).update(film)

  }

  def deleteById(id: Long): Future[Int] = db.run {
    films.filter(_.id === id).delete
  }
}
