package org.codelogger.web.controller;

import static java.lang.Boolean.TRUE;

import org.codelogger.web.utils.HttpServletHelper;

import javax.servlet.http.HttpServletRequest;

public abstract class AntiStealController extends BaseController {

  private static final String VALID_SESSION_KEY = "f_s_k";

  public abstract String getServerPath();

  protected void setThisSessionToValid(final HttpServletRequest request) {

    request.getSession().setAttribute(VALID_SESSION_KEY, TRUE);
  }

  protected boolean isValidVisit(final HttpServletRequest request) {

    Boolean isValidSession =
        HttpServletHelper.getBooleanValueFromSession(request, VALID_SESSION_KEY);
    if (isValidSession) {
      return true;
    } else {
      String referer = request.getHeader("referer");
      return referer != null && referer.indexOf(getServerPath()) == 0;
    }
  }
}
