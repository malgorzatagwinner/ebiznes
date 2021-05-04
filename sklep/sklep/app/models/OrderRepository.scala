package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class OrderData(user_id: Long, payment_id: Long)
object OrderData {
  implicit val OrderDataFormat = Json.format[OrderData]
}

case class Order(id: Long = 0L, user_id: Long, payment_id: Long)

object Order {
  implicit val OrderFormat = Json.format[Order]
}

@Singleton
class OrderRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository, val paymentRepository: PaymentRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class OrdersTable(tag: Tag) extends Table[Order](tag, "Orders") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    
    def user_id = column[Long]("user_id")
    def user_fk = foreignKey("user_fk", user_id, user_)(_.id)
     
    def payment_id = column[Long]("payment_id")
    def payment_fk = foreignKey("payment_id_fk", payment_id, payment_)(_.id)

    def * = (id, user_id, payment_id) <> ((Order.apply _).tupled, Order.unapply)
  }

  private val orders = TableQuery[OrdersTable]

  def getAll(): Future[Seq[Order]] = db.run {
    orders.result
  }

  import userRepository.UsersTable
  import paymentRepository.PaymentsTable
  
  val order = TableQuery[OrdersTable]
  val user_ = TableQuery[UsersTable]
  val payment_ = TableQuery[PaymentsTable]

  def create(order: OrderData): Future[Order] = db.run {
    (orders.map( o => (o.user_id, o.payment_id))
      returning orders.map(_.id)
      into {case ((user_id, payment_id), id) => Order(id, user_id, payment_id)}
    ) += ((order.user_id, order.payment_id))
  }

  def getById(id: Long): Future[Option[Order]] = db.run {
    orders.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, order: OrderData): Future[Int] = db.run {
    orders.filter(_.id === id).map(o => (o.user_id, o.payment_id) <> ((OrderData.apply _).tupled, OrderData.unapply)).update(order)
  }

  def deleteById(id: Long): Future[Int] = db.run {
    orders.filter(_.id === id).delete
  }
}
