package controllers

import play.api.libs.json._
import javax.inject._
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}

@Singleton
class FavouriteController @Inject()(val controllerComponents: ControllerComponents) extends BaseController{


  def getAll(): Action[AnyContent] = Action{
    Ok(s"Oki")
  }
  def getById(itemId: Long) = Action{
    Ok(s"Znalezione")
  }
  def deleteById(itemId: Long) = Action{
    Ok(s"Usunięte")
  }

  def create()= Action{
    Ok(s"Stworzone")
  }
  def modifyById(itemId: Long) = Action{
    Ok(s"Zmodyfikowane")
  }

//  implicit val FavouriteJson = Json.format[FavouriteItem]
}
