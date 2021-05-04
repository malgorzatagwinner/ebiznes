package controllers

import play.api.libs.json._
import javax.inject._
import models.{User,UserData,UserRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(val repo: UserRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { Users =>
    Ok(Json.toJson(Users))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ User =>
    if (User == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(User))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto reżysera ${id}")
    }
  }
  
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[UserData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		UserData => {
			repo.create(UserData).map{ User=>
				Ok(s"Stworzono reżysera ${User.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[UserData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	UserData =>{
    		repo.modifyById(id, UserData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano reżysera ${id}")
				}
			}
			)  
  }
}
