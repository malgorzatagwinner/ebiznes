package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Payment,PaymentData,PaymentRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PaymentController @Inject()(messagesAction: MessagesActionBuilder, val repo: PaymentRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


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
  
  
  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.PaymentView(result, form, routes.PaymentController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[PaymentData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.PaymentView(result, formWithErrors, routes.PaymentController.createWidget))
      }
    }

    val successFunction = { data: PaymentData =>
      val widget = PaymentData(user_id = data.user_id, amount = data.amount)
      Future(Redirect(routes.PaymentController.listWidget).flashing("info" -> "Payment added!"))
    }
  
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  val form = Form(mapping("user_id" -> longNumber,
  			   "amount" -> number)(PaymentData.apply)(PaymentData.unapply))
}
