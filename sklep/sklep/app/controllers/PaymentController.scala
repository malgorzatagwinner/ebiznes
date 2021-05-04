package controllers

import play.api.libs.json._
import javax.inject._
import models.{Payment,PaymentData,PaymentRepository}
import play.api.mvc.{BaseController, Action, ControllerComponents, AnyContent}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentController @Inject()(val repo: PaymentRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { payments =>
    Ok(Json.toJson(payments))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ payment =>
    if (payment == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(payment))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto płatność ${id}")
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[PaymentData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		paymentData => {
			repo.create(paymentData).map{ Payment=>
				Ok(s"Stworzono płatność ${Payment.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[PaymentData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	paymentData =>{
    		repo.modifyById(id, paymentData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano płatność ${id}")
				}
			}
		)  
  }
}
