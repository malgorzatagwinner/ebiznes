package controllers

import com.mohiva.play.silhouette.api._
import com.mohiva.play.silhouette.impl.providers._
import controllers.request.SignUpRequest
import javax.inject.Inject
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, Request}
import models.UserData
import scala.concurrent.{ExecutionContext, Future}
import scala.language.postfixOps

class SignUpController @Inject()(components: DefaultSilhouetteControllerComponents)(implicit ex: ExecutionContext) extends SilhouetteController(components) {

  def signUp: Action[AnyContent] = unsecuredAction.async { implicit request: Request[AnyContent] =>
    val json = request.body.asJson.get
    val signUpRequest = json.as[SignUpRequest]
    val loginInfo = LoginInfo(CredentialsProvider.ID, signUpRequest.email)

    userRepository.retrieve(loginInfo).flatMap {
      case Some(_) =>
        Future.successful(Forbidden(Json.obj("error"->"User already exists")))
      case None =>
        val authInfo = passwordHasherRegistry.current.hash(signUpRequest.password)
        userRepository.create(
          UserData(signUpRequest.email,"","","","","","","", CredentialsProvider.ID,signUpRequest.email)
        ).flatMap { user =>
          authInfoRepository.add(loginInfo, authInfo)
            .map(_ => user)
        }.map { user =>
          Json.toJson(user)
        }.map { json =>
          Created(json)
        }
    }
  }
}
