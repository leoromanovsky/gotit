package controllers

import play.api.mvc._
import play.api.libs.json._


class ApiController extends Controller {
  def index = Action { implicit request =>
    val results = Seq(1, 2, 3)
    Ok(Json.toJson(results))
  }
}
