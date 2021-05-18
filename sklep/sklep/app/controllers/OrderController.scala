package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Order,OrderData,OrderRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(messagesAction: MessagesActionBuilder, val repo: OrderRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { orders =>
    Ok(Json.toJson(orders))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ order =>
    if (order == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(order))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(Json.obj("status"->s"Usunięto zamowienie ${id}"))
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[OrderData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		orderData => {
			repo.create(orderData).map{ order=>
				Ok(Json.obj("status"->s"Stworzono zamowienie ${order.id}"))
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[OrderData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	orderData =>{
    		repo.modifyById(id, orderData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(Json.obj("status"->s"Zmodyfikowano zamówienie ${id}"))
				}
			}
		)  
  }
  

  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.OrderView(result, form, routes.OrderController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[OrderData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.OrderView(result, formWithErrors, routes.OrderController.createWidget))
      }
    }

    val successFunction = { data: OrderData =>
      val widget = OrderData(user_id = data.user_id, payment_id = data.payment_id)
      repo.create(widget).map{ order =>
      Redirect(routes.OrderController.listWidget).flashing("info" -> "Order added!")
    }
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("user_id" -> longNumber,
  			   "payment_id" -> longNumber)(OrderData.apply)(OrderData.unapply))
  			   
  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.OrderController.listWidget).flashing("error" -> "Not found!")
      case Some(order) =>
        val orderData = OrderData(order.user_id, order.payment_id)
        Ok(views.html.OrderViewUpdate(id, form.fill(orderData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.OrderController.listWidget).flashing("error" -> "Not found!")
      case _ =>
        Redirect(routes.OrderController.listWidget).flashing("info" -> "Order deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[OrderData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.OrderView(result, formWithErrors, routes.OrderController.createWidget))
      }
    }

    val successFunction = { data: OrderData =>
      val widget = OrderData(user_id = data.user_id, payment_id = data.payment_id)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.OrderController.listWidget).flashing("info" -> "Order modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }
}
