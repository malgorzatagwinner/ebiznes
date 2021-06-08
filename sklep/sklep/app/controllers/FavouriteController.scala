package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Favourite,FavouriteData,FavouriteRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class FavouriteController @Inject()(messagesAction: MessagesActionBuilder, val repo: FavouriteRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


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
    	case _ => Ok(Json.obj("status"->s"Usunięto ulubieńca ${id}"))
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
				Ok(Json.obj("status"->s"Stworzono ulubieńca ${favourite.id}"))
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
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano ulubieńca ${id}"))
				}
			}
		)  
  }
  
    def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.FavouriteView(result, form, routes.FavouriteController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[FavouriteData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.FavouriteView(result, formWithErrors, routes.FavouriteController.createWidget))
      }
    }

    val successFunction = { data: FavouriteData =>
      val widget = FavouriteData(film_id = data.film_id, user_id = data.user_id)
      repo.create(widget).map{ favourite =>
      	Redirect(routes.FavouriteController.listWidget).flashing("info" -> "Favourite added!")
      }
    }
  
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  
  val form = Form(mapping("film_id" -> longNumber, "user_id" -> longNumber)(FavouriteData.apply)(FavouriteData.unapply))
  
  
  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.FavouriteController.listWidget).flashing("error" -> "Not found!")
      case Some(favourite) =>
        val favouriteData = FavouriteData(favourite.film_id, favourite.user_id)
        Ok(views.html.FavouriteViewUpdate(id, form.fill(favouriteData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.FavouriteController.listWidget).flashing("error" -> "Not found!")
      case _ =>
        Redirect(routes.FavouriteController.listWidget).flashing("info" -> "Favourite deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[FavouriteData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.FavouriteView(result, formWithErrors, routes.FavouriteController.createWidget))
      }
    }

    val successFunction = { data: FavouriteData =>
      val widget = FavouriteData(film_id = data.film_id, user_id = data.user_id)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.FavouriteController.listWidget).flashing("info" -> "Favourite modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }
}
