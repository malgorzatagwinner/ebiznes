package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{ShoppingBag,ShoppingBagData,ShoppingBagRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ShoppingBagController @Inject()(messagesAction: MessagesActionBuilder, val repo: ShoppingBagRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{

val nf = "Not Found"

  def getAll = Action.async{
    repo.getAll().map { ShoppingBags =>
    Ok(Json.toJson(ShoppingBags))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ shoppingBag =>
    if (ShoppingBag == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(shoppingBag))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> nf))
    	case _ => Ok(Json.obj("status"->s"Usunięto koszyk ${id}"))
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[ShoppingBagData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		shoppingBagData => {
			repo.create(shoppingBagData).map{ shoppingBag=>
				Ok(Json.obj("status"->s"Stworzono koszyk ${shoppingBag.id}"))
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[ShoppingBagData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	shoppingBagData =>{
    		repo.modifyById(id, shoppingBagData).map{
    			case 0 => NotFound(Json.obj("error" -> nf))
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano koszyk ${id}"))
				}
			}
		)  
  }  
   def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.ShoppingBagView(result, form, routes.ShoppingBagController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[ShoppingBagData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.ShoppingBagView(result, formWithErrors, routes.ShoppingBagController.createWidget))
      }
    }

    val successFunction = { data: ShoppingBagData =>
      val widget = ShoppingBagData(total_cost = data.total_cost, film_id = data.film_id)
      repo.create(widget).map{ review => Redirect(routes.ShoppingBagController.listWidget).flashing("info" -> "ShoppingBag added!")
    }
  }
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("total_cost" -> number,
  			   "film_id" -> longNumber
  		)(ShoppingBagData.apply)(ShoppingBagData.unapply))
  
  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.ShoppingBagController.listWidget).flashing("error" -> nf)
      case Some(shoppingBag) =>
        val shoppingBagData = ShoppingBagData(shoppingBag.total_cost, shoppingBag.film_id)
        Ok(views.html.ShoppingBagViewUpdate(id, form.fill(shoppingBagData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.ShoppingBagController.listWidget).flashing("error" -> nf)
      case _ =>
        Redirect(routes.ShoppingBagController.listWidget).flashing("info" -> "ShoppingBag deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[ShoppingBagData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.ShoppingBagView(result, formWithErrors, routes.ShoppingBagController.createWidget))
      }
    }

    val successFunction = { data: ShoppingBagData =>
      val widget = ShoppingBagData(total_cost = data.total_cost, film_id = data.film_id)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.ShoppingBagController.listWidget).flashing("info" -> "ShoppingBag modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }

}
