package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Genre,GenreData,GenreRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class GenreController @Inject()(messagesAction: MessagesActionBuilder, val repo: GenreRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


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
  
  
  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.GenreView(result, form, routes.GenreController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[GenreData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.GenreView(result, formWithErrors, routes.GenreController.createWidget))
      }
    }

    val successFunction = { data: GenreData =>
      val widget = GenreData(name = data.name)
      Future(Redirect(routes.GenreController.listWidget).flashing("info" -> "Genre added!"))
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("name" -> nonEmptyText)(GenreData.apply)(GenreData.unapply))
}
