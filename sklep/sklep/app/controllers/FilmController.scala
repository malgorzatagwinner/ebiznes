package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Film,FilmData,FilmRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FilmController @Inject()(messagesAction: MessagesActionBuilder, val repo: FilmRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{

val nf = "Not Found"

  def getAll = Action.async{
    repo.getAll().map { Films =>
    Ok(Json.toJson(Films))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ Film =>
    if (Film == None)
    	NotFound(Json.obj("error" -> nf))
    else
    	Ok(Json.toJson(Film))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> nf))
    	case _ => Ok(Json.obj("status"->s"Usunięto film ${id}"))
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
				Ok(Json.obj("status"->s"Stworzono film ${Film.id}"))
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
    	filmData =>{
    		repo.modifyById(id, filmData).map{
    			case 0 => NotFound(Json.obj("error" -> nf))
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano film ${id}"))
				}
			}
		)  
  }
  
  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.FilmView(result, form, routes.FilmController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[FilmData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.FilmView(result, formWithErrors, routes.FilmController.createWidget))
      }
    }

    val successFunction = { data: FilmData =>
      val widget = FilmData(name = data.name, genre_id = data.genre_id, director_id = data.director_id, actor_id = data.actor_id)
      repo.create(widget).map{ film =>Redirect(routes.FilmController.listWidget).flashing("info" -> "Film added!")
    }
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("name" -> nonEmptyText,
  			   "genre_id" -> longNumber,
  			   "director_id" -> longNumber,
  			   "actor_id" -> longNumber
  			)(FilmData.apply)(FilmData.unapply))

  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.FilmController.listWidget).flashing("error" -> nf)
      case Some(film) =>
        val filmData = FilmData(film.name, film.genre_id, film.director_id, film.actor_id)
        Ok(views.html.FilmViewUpdate(id, form.fill(filmData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.FilmController.listWidget).flashing("error" -> nf)
      case _ =>
        Redirect(routes.FilmController.listWidget).flashing("info" -> "Film deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[FilmData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.FilmView(result, formWithErrors, routes.FilmController.createWidget))
      }
    }

    val successFunction = { data: FilmData =>
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.FilmController.listWidget).flashing("info" -> "Film modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }

}
