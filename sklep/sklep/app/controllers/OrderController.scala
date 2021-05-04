package controllers

import play.api.libs.json._
import javax.inject._
import models.{Order,OrderData,OrderRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class OrderController @Inject()(val repo: OrderRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


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
    	case _ => Ok(s"Usunięto zamowienie ${id}")
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
				Ok(s"Stworzono zamowienie ${order.id}")
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
    			case _ => Ok(s"Zmodyfikowano zamówienie ${id}")
				}
			}
		)  
  }
}
