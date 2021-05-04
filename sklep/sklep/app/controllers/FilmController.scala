package controllers

import play.api.libs.json._
import javax.inject._
import models.{Film,FilmData,FilmRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FilmController @Inject()(val repo: FilmRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { Films =>
    Ok(Json.toJson(Films))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ Film =>
    if (Film == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(Film))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto film ${id}")
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[FilmData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		FilmData => {
			repo.create(FilmData).map{ Film=>
				Ok(s"Stworzono film ${Film.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[FilmData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	FilmData =>{
    		repo.modifyById(id, FilmData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano film ${id}")
				}
			}
		)  
  }
}
