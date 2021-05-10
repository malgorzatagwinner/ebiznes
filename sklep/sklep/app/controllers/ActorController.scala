package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Actor,ActorData,ActorRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ActorController @Inject()(messagesAction: MessagesActionBuilder, val repo: ActorRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController {


  def getAll = Action.async{
    repo.getAll().map { actors =>
    Ok(Json.toJson(actors))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ actor =>
    if (actor == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(actor))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto aktora ${id}")
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[ActorData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		actorData => {
			repo.create(actorData).map{ director=>
				Ok(s"Stworzono aktora ${director.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[ActorData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	actorData =>{
    		repo.modifyById(id, actorData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano aktora ${id}")
				}
			}
			)  
  }

  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.ActorView(result, form, routes.ActorController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[ActorData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.ActorView(result, formWithErrors, routes.ActorController.createWidget))
      }
    }

    val successFunction = { data: ActorData =>
      val widget = ActorData(name = data.name)
      Future(Redirect(routes.ActorController.listWidget).flashing("info" -> "Actor added!"))
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("name" -> nonEmptyText)(ActorData.apply)(ActorData.unapply))
}
