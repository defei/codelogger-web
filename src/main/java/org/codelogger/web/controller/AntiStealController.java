package org.codelogger.web.controller;

import static java.lang.Boolean.TRUE;

import javax.servlet.http.HttpServletRequest;

import org.codelogger.web.utils.HttpServletHelper;

public abstract class AntiStealController {

  private static final String VALID_SESSION_KEY = "f_s_k";

  public abstract String getServerPath();

  protected void setThisSessionToValid(final HttpServletRequest request) {

    request.getSession().setAttribute(VALID_SESSION_KEY, TRUE);
  }

  protected boolean isValidVisit(final HttpServletRequest request) {

    Boolean isValidSession = HttpServletHelper.getBooleanValueFromSession(request,
      VALID_SESSION_KEY);
    if (isValidSession) {
      return true;
    } else {
      String referer = request.getHeader("referer");
      return referer != null && referer.indexOf(getServerPath()) == 0;
    }
  }
}
