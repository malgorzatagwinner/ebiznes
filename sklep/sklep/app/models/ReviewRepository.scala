package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class ReviewData(stars: Int, txt: String, user_id: Long, film_id: Long)
object ReviewData {
  implicit val ReviewDataFormat = Json.format[ReviewData]
}

case class Review(id: Long = 0L, stars: Int, txt: String, user_id: Long, film_id: Long)

object Review {
  implicit val ReviewFormat = Json.format[Review]
}

@Singleton
class ReviewRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository, val filmRepository: FilmRepository) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ReviewsTable(tag: Tag) extends Table[Review](tag, "Reviews") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def stars = column[Int]("stars")
    def txt = column[String]("txt")    
    def user_id = column[Long]("user_id")
    def user_fk = foreignKey("user_fk", user_id, user_)(_.id)
        
    def film_id = column[Long]("film_id")
    def film_fk = foreignKey("film_fk", film_id, film_)(_.id)


    def * = (id, stars, txt, user_id, film_id) <> ((Review.apply _).tupled, Review.unapply)
  }

  private val reviews = TableQuery[ReviewsTable]

  def getAll(): Future[Seq[Review]] = db.run {
    reviews.result
  }

  import userRepository.UsersTable
  import filmRepository.FilmsTable

  val review = TableQuery[ReviewsTable]
  val user_ = TableQuery[UsersTable]
  val film_ = TableQuery[FilmsTable]

  def create(review: ReviewData): Future[Review] = db.run {
    (reviews.map( o => ( o.stars, o.txt, o.user_id, o.film_id))
      returning reviews.map(_.id)
      into {case (( stars, txt, user_id, film_id), id) => Review(id, stars, txt, user_id,film_id)}
    ) += (( review.stars, review.txt,review.user_id, review.film_id))
  }

  def getById(id: Long): Future[Option[Review]] = db.run {
    reviews.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, review: ReviewData): Future[Int] = db.run {
    reviews.filter(_.id === id).map(o => ( o.stars, o.txt, o.user_id, o.film_id) <> ((ReviewData.apply _).tupled, ReviewData.unapply)).update(review)
  }

  def deleteById(id: Long): Future[Int] = db.run {
    reviews.filter(_.id === id).delete
  }
}
