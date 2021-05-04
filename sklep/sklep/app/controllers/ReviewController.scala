package controllers

import play.api.libs.json._
import javax.inject._
import models.{Review,ReviewData,ReviewRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewController @Inject()(val repo: ReviewRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { Reviews =>
    Ok(Json.toJson(Reviews))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ Review =>
    if (Review == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(Review))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto recenzję ${id}")
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[ReviewData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		ReviewData => {
			repo.create(ReviewData).map{ Review=>
				Ok(s"Stworzono recenzję ${Review.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[ReviewData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	ReviewData =>{
    		repo.modifyById(id, ReviewData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano recenzję ${id}")
				}
			}
		)  
  }
}
