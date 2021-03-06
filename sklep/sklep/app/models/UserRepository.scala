package models

import javax.inject.{ Inject, Singleton }
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import scala.concurrent.{ Future, ExecutionContext }
import play.api.libs.json._
import com.mohiva.play.silhouette.api.{Identity, LoginInfo}
import com.mohiva.play.silhouette.api.services.IdentityService


case class UserData(email: String, password: String, name: String, surname: String, address: String, zipcode: String, city: String, country: String, providerId: String, providerKey: String)
object UserData {
  implicit val userDataFormat = Json.format[UserData]
}

case class User(id: Long = 0L, email: String, password: String, name: String, surname: String, address: String, zipcode: String, city: String, country: String, providerId: String, providerKey: String) extends Identity

object User {
  implicit val userFormat = Json.format[User]
}

@Singleton
class UserRepository @Inject() (dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends IdentityService[User]{
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  class UsersTable(tag: Tag) extends Table[User](tag, "Users") {

    def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
    def email = column[String]("email")
    def password = column[String]("password")
    def surname = column[String]("surname")
    def name = column[String]("name")
    def address = column[String]("address")
    def zipcode = column[String]("zipcode")
    def city = column[String]("city")
    def country = column[String]("country")
    def providerId = column[String]("providerId")
    def providerKey = column[String]("providerKey")


    def * = (id, email, password, surname, name, address, zipcode, city, country, providerId, providerKey) <> ((User.apply _).tupled, User.unapply)
  }

  private val users = TableQuery[UsersTable]

  def getAll(): Future[Seq[User]] = db.run {
    users.result
  }

  def create(user: UserData): Future[User] = db.run {
    (users.map( u => (u.email, u.password, u.surname, u.name, u.address, u.zipcode, u.city, u.country, u.providerId, u.providerKey))
      returning users.map(_.id)
      into {case ((email, password, surname, name, address, zipcode, city, country, providerId, providerKey), id) => User(id, email, password, surname, name, address, zipcode, city, country, providerId, providerKey)}
    ) += ((user.email, user.password, user.surname, user.name, user.address, user.zipcode, user.city, user.country, user.providerId, user.providerKey))
  }

  def getById(id: Long): Future[Option[User]] = db.run {
    users.filter(_.id === id).result.headOption
  }

  def modifyById(id: Long, user: UserData): Future[Int] = db.run {
    users.filter(_.id === id).map(u => (u.email, u.password, u.surname, u.name, u.address, u.zipcode, u.city, u.country, u.providerId, u.providerKey) <> ((UserData.apply _).tupled, UserData.unapply)).update(user)

  }

  def deleteById(id: Long): Future[Int] = db.run {
    users.filter(_.id === id).delete
  }
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = db.run{
    users.filter(_.providerId === loginInfo.providerID)
      .filter(_.providerKey === loginInfo.providerKey)
      .result
      .headOption
  }

}
