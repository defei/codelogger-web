package org.codelogger.web.bean;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;


public class MarvelServlet {

  private HttpServletRequest request;
  
  private HttpServletResponse response;
  
  
  public HttpServletRequest getRequest() {
    
    return request;
  }

  
  public void setRequest(HttpServletRequest request) {
  
    this.request = request;
  }

  
  public HttpServletResponse getResponse() {
  
    return response;
  }

  
  public void setResponse(HttpServletResponse response) {
  
    this.response = response;
  }
  
  public Locale getLocale(){
    
    return LocaleContextHolder.getLocale();
  }
  
}
