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

val nf = "Not Found"

  def getAll = Action.async{
    repo.getAll().map { actors =>
    Ok(Json.toJson(actors))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ actor =>
    if (actor == None)
    	NotFound(Json.obj("error" -> nf))
    else
    	Ok(Json.toJson(actor))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> nf))
    	case _ => Ok(Json.obj("status"->s"Usunięto aktora ${id}"))
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[ActorData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		actorData => {
			repo.create(actorData).map{ actor=>
				Ok(Json.obj("status"->s"Stworzono aktora ${actor.id}"))
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
    			case 0 => NotFound(Json.obj("error" -> nf))
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano aktora ${id}"))
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
      repo.create(widget).map{ actor =>
      	Redirect(routes.ActorController.listWidget).flashing("info" -> "Actor added!")
      }
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("name" -> nonEmptyText)(ActorData.apply)(ActorData.unapply))

  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.ActorController.listWidget).flashing("error" -> nf)
      case Some(actor) =>
        val actorData = ActorData(actor.name)
        Ok(views.html.ActorViewUpdate(id, form.fill(actorData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.ActorController.listWidget).flashing("error" -> nf)
      case _ =>
        Redirect(routes.ActorController.listWidget).flashing("info" -> "Actor deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[ActorData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.ActorView(result, formWithErrors, routes.ActorController.createWidget))
      }
    }

    val successFunction = { data: ActorData =>
      val widget = ActorData(name = data.name)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.ActorController.listWidget).flashing("info" -> "Actor modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }

}
