package controllers

import play.api.mvc._

class AdminController extends Controller {
  def index = Action { implicit request =>
    Ok(views.html.admin.index())
  }
}
