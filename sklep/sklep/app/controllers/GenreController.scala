package controllers

import play.api.libs.json._
import javax.inject._
import models.{Genre,GenreData,GenreRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GenreController @Inject()(val repo: GenreRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { genres =>
    Ok(Json.toJson(genres))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ genre =>
    if (genre == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(genre))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto rodzaj ${id}")
    }
  }
  
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[GenreData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		genreData => {
			repo.create(genreData).map{ genre=>
				Ok(s"Stworzono rodzaj ${genre.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[GenreData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	genreData =>{
    		repo.modifyById(id, genreData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano rodzaj ${id}")
				}
			}
			)  
  }
}
