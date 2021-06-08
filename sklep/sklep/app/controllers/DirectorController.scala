package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Director,DirectorData,DirectorRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class DirectorController @Inject()(messagesAction: MessagesActionBuilder,val repo: DirectorRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{

val nf = "Not Found"

  def getAll = Action.async{
    repo.getAll().map { directors =>
    Ok(Json.toJson(directors))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ director =>
    if (director == None)
    	NotFound(Json.obj("error" -> nf))
    else
    	Ok(Json.toJson(director))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> nf))
    	case _ => Ok(Json.obj("status"->s"Usunięto reżysera ${id}"))
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
				Ok(Json.obj("status"->s"Stworzono reżysera ${director.id}"))
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
    			case 0 => NotFound(Json.obj("error" -> nf))
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano reżysera ${id}"))
				}
			}
		)  
  }
  
   def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.DirectorView(result, form, routes.DirectorController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[DirectorData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.DirectorView(result, formWithErrors, routes.DirectorController.createWidget))
      }
    }
  
    val successFunction = { data: DirectorData =>
      val widget = DirectorData(name = data.name)
       repo.create(widget).map{ director =>
            Redirect(routes.DirectorController.listWidget).flashing("info" -> "Director added!")
    }
    }
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)

  }
  val form = Form(mapping("name" -> nonEmptyText)(DirectorData.apply)(DirectorData.unapply))
  
  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.DirectorController.listWidget).flashing("error" -> nf)
      case Some(director) =>
        val directorData = DirectorData(director.name)
        Ok(views.html.DirectorViewUpdate(id, form.fill(directorData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.DirectorController.listWidget).flashing("error" -> nf)
      case _ =>
        Redirect(routes.DirectorController.listWidget).flashing("info" -> "Director deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[DirectorData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.DirectorView(result, formWithErrors, routes.DirectorController.createWidget))
      }
    }

    val successFunction = { data: DirectorData =>
      val widget = DirectorData(name = data.name)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.DirectorController.listWidget).flashing("info" -> "Director modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }

  
  
}
