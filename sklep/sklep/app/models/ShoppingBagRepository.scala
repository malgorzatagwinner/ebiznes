package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class ShoppingBagData(total_cost: Int, film_id: Long)
object ShoppingBagData {
  implicit val ShoppingBagDataFormat = Json.format[ShoppingBagData]
}

case class ShoppingBag(id: Long = 0L, total_cost: Int, film_id: Long)

object ShoppingBag {
  implicit val ShoppingBagFormat = Json.format[ShoppingBag]
}

@Singleton
class ShoppingBagRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val filmRepository: FilmRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class ShoppingBagsTable(tag: Tag) extends Table[ShoppingBag](tag, "ShoppingBags") {
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def total_cost = column[Int]("total_cost")

    def film_id = column[Long]("film_id")
    def film_fk = foreignKey("film_fk", film_id, film_)(_.id)
 
    
    
    def * = (id, total_cost, film_id) <> ((ShoppingBag.apply _).tupled, ShoppingBag.unapply)
  }

  import filmRepository.FilmsTable
  
  private val shoppingBags = TableQuery[ShoppingBagsTable]
  val film_ = TableQuery[FilmsTable]

      
  
  def getAll(): Future[Seq[ShoppingBag]] = db.run {
    shoppingBags.result
  }

  def create(shoppingBag: ShoppingBagData): Future[ShoppingBag] = db.run {
    (shoppingBags.map( s => (s.total_cost, s.film_id))
      returning shoppingBags.map(_.id)
      into {case ((  total_cost, film_id), id) => ShoppingBag(id,  total_cost, film_id)}
    ) += (( shoppingBag.total_cost, shoppingBag.film_id))
}
  def getById(id: Long): Future[Option[ShoppingBag]] = db.run {
    shoppingBags.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, shoppingBag: ShoppingBagData): Future[Int] = db.run {
    shoppingBags.filter(_.id === id).map(s => (s.total_cost, s.film_id) <> ((ShoppingBagData.apply _).tupled, ShoppingBagData.unapply)).update(shoppingBag)

  }

  def deleteById(id: Long): Future[Int] = db.run {
    shoppingBags.filter(_.id === id).delete
  }
}
