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
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto koszyk ${id}")
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
				Ok(s"Stworzono koszyk ${shoppingBag.id}")
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
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano koszyk ${id}")
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
      Future(Redirect(routes.ShoppingBagController.listWidget).flashing("info" -> "ShoppingBag added!"))
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("total_cost" -> number,
  			   "film_id" -> longNumber
  		)(ShoppingBagData.apply)(ShoppingBagData.unapply))
  
}
