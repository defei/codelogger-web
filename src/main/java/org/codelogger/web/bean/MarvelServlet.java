package org.codelogger.web.bean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MarvelServlet {

  private HttpServletRequest request;

  private HttpServletResponse response;

  public HttpServletRequest getRequest() {

    return request;
  }

  public void setRequest(final HttpServletRequest request) {

    this.request = request;
  }

  public HttpServletResponse getResponse() {

    return response;
  }

  public void setResponse(final HttpServletResponse response) {

    this.response = response;
  }

}
