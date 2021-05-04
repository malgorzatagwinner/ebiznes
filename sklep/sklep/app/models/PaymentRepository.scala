package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._

case class PaymentData( user_id: Long, amount: Int)
object PaymentData {
  implicit val PaymentDataFormat = Json.format[PaymentData]
}

case class Payment(id: Long = 0L, user_id: Long, amount: Int)

object Payment {
  implicit val PaymentFormat = Json.format[Payment]
}

@Singleton
class PaymentRepository @Inject() (dbConfigProvider: DatabaseConfigProvider, val userRepository: UserRepository)(implicit ec: ExecutionContext) {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class PaymentsTable(tag: Tag) extends Table[Payment](tag, "Payments") {
    
    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def user_id = column[Long]("user_id")
    def user_fk = foreignKey("user_fk", user_id, user_)(_.id)
    def amount = column[Int]("amount")
    
    
    def * = (id, user_id, amount) <> ((Payment.apply _).tupled, Payment.unapply)
  }

  import userRepository.UsersTable
  
  private val payments = TableQuery[PaymentsTable]
  val user_ = TableQuery[UsersTable]
  
  
  def getAll(): Future[Seq[Payment]] = db.run {
    payments.result
  }

  def create(payment: PaymentData): Future[Payment] = db.run {
    (payments.map( p => (p.user_id, p.amount))
      returning payments.map(_.id)
      into {case (( user_id, amount), id) => Payment(id,  user_id, amount)}
    ) += (( payment.user_id, payment.amount))
  }

  def getById(id: Long): Future[Option[Payment]] = db.run {
    payments.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, payment: PaymentData): Future[Int] = db.run {
    payments.filter(_.id === id).map(p => (p.user_id, p.amount) <> ((PaymentData.apply _).tupled, PaymentData.unapply)).update(payment)

  }

  def deleteById(id: Long): Future[Int] = db.run {
    payments.filter(_.id === id).delete
  }
}
