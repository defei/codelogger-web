package org.codelogger.web.context;

import static java.lang.String.format;

import javax.servlet.ServletContext;

import org.codelogger.core.context.ApplicationContext;
import org.codelogger.core.context.ApplicationContextLoader;
import org.codelogger.core.context.bean.ComponentScanner;
import org.codelogger.core.context.stereotype.Dao;
import org.codelogger.core.context.stereotype.Service;
import org.codelogger.web.context.stereotype.Controller;

public class WebContextLoader extends ApplicationContextLoader {

  public WebApplicationContext initWebApplicationContext(final ServletContext servletContext) {

    String contextConfigLocation = servletContext.getInitParameter(CONTEXT_CONFIG_LOCATION);
    if (contextConfigLocation == null) {
      throw new IllegalArgumentException(format("missing context-param %s.",
        CONTEXT_CONFIG_LOCATION));
    }
    @SuppressWarnings("unchecked")
    ComponentScanner componentScanner = new ComponentScanner(getClass().getClassLoader(), null,
      Dao.class, Service.class, Controller.class);
    super.setComponentScanner(componentScanner);
    ApplicationContext initApplicationContext = super.initApplicationContext(contextConfigLocation);
    return new WebApplicationContext(initApplicationContext);
  }

  private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";
}
