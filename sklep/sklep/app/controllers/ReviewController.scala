package controllers

import play.api.data.Forms._
import play.api.data.Form
import play.api.libs.json._
import javax.inject._
import models.{Review,ReviewData,ReviewRepository}
import play.api.mvc._
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class ReviewController @Inject()(messagesAction: MessagesActionBuilder, val repo: ReviewRepository, val controllerComponents: ControllerComponents)(implicit ec: ExecutionContext) extends BaseController{


  def getAll = Action.async{
    repo.getAll().map { Reviews =>
    Ok(Json.toJson(Reviews))
    }
  }
  def getById(id: Long) = Action.async{
    repo.getById(id).map{ Review =>
    if (Review == None)
    	NotFound(Json.obj("error" -> "Not Found"))
    else
    	Ok(Json.toJson(Review))
  	}
  }
  
  def deleteById(id: Long) = Action.async{
    repo.deleteById(id).map{
    	case 0 => NotFound(Json.obj("error" -> "Not Found"))
    	case _ => Ok(s"Usunięto recenzję ${id}")
    }
  }
  def create()= Action(parse.json).async{
		implicit request =>
		request.body.validate[ReviewData].fold(
      errors => {
        Future(BadRequest(Json.obj("error" -> JsError.toJson(errors))))
      },
		ReviewData => {
			repo.create(ReviewData).map{ Review=>
				Ok(s"Stworzono recenzję ${Review.id}")
			}
		}
		)
		
  }
  
  def modifyById(id: Long) = Action(parse.json).async{
    implicit request =>
    val result = request.body.validate[ReviewData]
    result.fold( errors =>{
    	Future(BadRequest(Json.obj("error" -> "Invalid Json")))
    	},
    	ReviewData =>{
    		repo.modifyById(id, ReviewData).map{
    			case 0 => NotFound(Json.obj("error" -> "Not Found"))
    			case _ => Ok(s"Zmodyfikowano recenzję ${id}")
				}
			}
		)  
  }
  
  def listWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getAll().map{result => 
      Ok(views.html.ReviewView(result, form, routes.ReviewController.createWidget))
    }
  }
  
  def createWidget = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[ReviewData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.ReviewView(result, formWithErrors, routes.ReviewController.createWidget))
      }
    }

    val successFunction = { data: ReviewData =>
      val widget = ReviewData(stars = data.stars, txt = data.txt, user_id = data.user_id)
      repo.create(widget).map{ review => Redirect(routes.ReviewController.listWidget).flashing("info" -> "Review added!")
    }
  }
    val formValidationResult = form.bindFromRequest()
    formValidationResult.fold(errorFunction, successFunction)
  }
  
  val form = Form(mapping("stars" -> number,
	   		   "txt" -> text,
	   		   "user_id" -> longNumber)
  	(ReviewData.apply)(ReviewData.unapply))
  	
  def getWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.getById(id).map{
      case None =>
        Redirect(routes.ReviewController.listWidget).flashing("error" -> "Not found!")
      case Some(review) =>
        val reviewData = ReviewData(review.stars, review.txt, review.user_id)
        Ok(views.html.ReviewViewUpdate(id, form.fill(reviewData)))
    }
  }
  
  def deleteWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    repo.deleteById(id).map{
      case 0 =>
        Redirect(routes.ReviewController.listWidget).flashing("error" -> "Not found!")
      case _ =>
        Redirect(routes.ReviewController.listWidget).flashing("info" -> "Review deleted!")
    }
  }

  def updateWidget(id: Long) = messagesAction.async{ implicit request: MessagesRequest[AnyContent] =>
    val errorFunction = { formWithErrors: Form[ReviewData] =>
      repo.getAll().map{result =>
        BadRequest(views.html.ReviewView(result, formWithErrors, routes.ReviewController.createWidget))
      }
    }

    val successFunction = { data: ReviewData =>
      val widget = ReviewData(stars = data.stars, txt = data.txt, user_id = data.user_id)
      repo.modifyById(id, data).map{ a=>
        Redirect(routes.ReviewController.listWidget).flashing("info" -> "Review modified!")
      }
    }
    form.bindFromRequest().fold(errorFunction, successFunction)
  }

}
