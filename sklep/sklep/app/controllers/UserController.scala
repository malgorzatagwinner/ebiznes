package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{User,UserData,UserRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class UserController @Inject()(messagesAction: MessagesActionBuilder, val repo: UserRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


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
    	case _ => Ok(Json.obj("status"->s"Usunięto reżysera ${id}"))
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
				Ok(Json.obj("status"->s"Stworzono reżysera ${User.id}"))
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
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano reżysera ${id}"))
				}
			}
			)  
  }
  
  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.UserView(result, form, routes.UserController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[UserData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.UserView(result, formWithErrors, routes.UserController.createWidget))
      }
    }

    val successFunction = { data: UserData =>
      val widget = UserData(email = data.email, password = data.password, surname = data.surname, name = data.name, address = data.address, zipcode = data.zipcode, city = data.city, country = data.country)
      repo.create(widget).map{ user =>
Redirect(routes.UserController.listWidget).flashing("info" -> "User added!")
    }
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("email" -> nonEmptyText,
  			   "password" -> nonEmptyText,
  			   "surname" -> nonEmptyText,
  			   "name" -> nonEmptyText,
  			   "address" -> nonEmptyText,
  			   "zipcode" -> nonEmptyText,
  			   "city" -> nonEmptyText,
  			   "country" -> nonEmptyText
  			)(UserData.apply)(UserData.unapply))

  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.UserController.listWidget).flashing("error" -> "Not found!")
      case Some(user) =>
        val userData = UserData(user.email, user.password, user.surname, user.name, user.address, user.zipcode, user.city, user.country)
        Ok(views.html.UserViewUpdate(id, form.fill(userData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.UserController.listWidget).flashing("error" -> "Not found!")
      case _ =>
        Redirect(routes.UserController.listWidget).flashing("info" -> "User deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[UserData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.UserView(result, formWithErrors, routes.UserController.createWidget))
      }
    }

    val successFunction = { data: UserData =>
      val widget = UserData(email = data.email, password = data.password, surname = data.surname, name = data.name, address = data.address, zipcode = data.zipcode, city = data.city, country = data.country)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.UserController.listWidget).flashing("info" -> "User modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }

}
