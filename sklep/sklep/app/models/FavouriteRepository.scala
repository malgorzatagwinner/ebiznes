package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class FavouriteData(film_id: Long, user_id: Long)
object FavouriteData {
  implicit val FavouriteDataFormat = Json.format[FavouriteData]
}

case class Favourite(id: Long = 0L, film_id: Long, user_id: Long)

object Favourite {
  implicit val FavouriteFormat = Json.format[Favourite]
}

@Singleton
class FavouriteRepository @Inject() (dbConfigProvider: DatabaseConfigProvider,  val filmRepository: FilmRepository, val userRepository: UserRepository)(implicit ec: ExecutionContext) { 
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

   class FavouritesTable(tag: Tag) extends Table[Favourite](tag, "Favourites") {
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    
    def film_id = column[Long]("film_id")
    def film_fk = foreignKey("film_fk", film_id, film_)(_.id)    
    
    def user_id = column[Long]("user_id")
    def user_fk = foreignKey("user_fk", user_id, user_)(_.id)
     
    def * = (id, film_id, user_id) <> ((Favourite.apply _).tupled, Favourite.unapply)
  }

  val favourites = TableQuery[FavouritesTable]
  import filmRepository.FilmsTable
  val film_ = TableQuery[FilmsTable]
  import userRepository.UsersTable
  val user_ = TableQuery[UsersTable]

  def getAll(): Future[Seq[Favourite]] = db.run {
    favourites.result
  }

  def create(favourite: FavouriteData): Future[Favourite] = db.run {
    (favourites.map(f => (f.film_id, f.user_id))
      returning favourites.map(_.id)
      into { case ((film_id, user_id), id) => Favourite(id, film_id, user_id)}
    ) += ((favourite.film_id, favourite.user_id))
  }

  def getById(id: Long): Future[Option[Favourite]] = db.run {
    favourites.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, favourite: FavouriteData): Future[Int] = db.run {
    favourites.filter(_.id === id).map(p => (p.film_id, p.user_id) <> ((FavouriteData.apply _).tupled, FavouriteData.unapply)).update(favourite)

  }

  def deleteById(id: Long): Future[Int] = db.run {
    favourites.filter(_.id === id).delete
  }
}
