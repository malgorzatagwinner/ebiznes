package controllers

import play.api.libs.json._
import javax.inject._
import models.{Director,DirectorData,DirectorRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectorController @Inject()(val repo: DirectorRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { directors =>
    Ok(Json.toJson(directors))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ director =>
    if (director == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(director))
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
		request.body.validate[DirectorData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		directorData => {
			repo.create(directorData).map{ director=>
				Ok(s"Stworzono reżysera ${director.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[DirectorData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	directorData =>{
    		repo.modifyById(id, directorData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano reżysera ${id}")
				}
			}
		)  
  }
}
