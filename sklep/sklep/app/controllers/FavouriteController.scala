package controllers

import play.api.libs.json._
import javax.inject._
import models.{Favourite,FavouriteData,FavouriteRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FavouriteController @Inject()(val repo: FavouriteRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { Favourites =>
    Ok(Json.toJson(Favourites))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ Favourite =>
    if (Favourite == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(Favourite))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto ulubieńca ${id}")
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[FavouriteData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		FavouriteData => {
			repo.create(FavouriteData).map{ favourite=>
				Ok(s"Stworzono ulubieńca ${favourite.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[FavouriteData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	FavouriteData =>{
    		repo.modifyById(id, FavouriteData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano ulubieńca ${id}")
				}
			}
		)  
  }
}
