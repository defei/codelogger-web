package org.codelogger.web.context;

import static java.lang.String.format;

import java.lang.annotation.Annotation;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletContext;

import org.codelogger.core.context.ApplicationContext;
import org.codelogger.core.context.ApplicationContextLoader;
import org.codelogger.core.context.bean.DefaultConstructFactory;
import org.codelogger.core.context.bean.PropertiesLoader;
import org.codelogger.web.context.stereotype.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebContextLoader extends ApplicationContextLoader {

  public WebApplicationContext initWebApplicationContext(final ServletContext servletContext) {

    String contextConfigLocation = servletContext.getInitParameter(CONTEXT_CONFIG_LOCATION);
    if (contextConfigLocation == null) {
      throw new IllegalArgumentException(format("missing context-param %s.",
        CONTEXT_CONFIG_LOCATION));
    }
    ApplicationContext initApplicationContext = super.initApplicationContext(contextConfigLocation);
    WebApplicationContext webApplicationContext = new WebApplicationContext(initApplicationContext);
    servletContext.setAttribute(WEB_APPLICATION_CONTEXT, webApplicationContext);
    return webApplicationContext;
  }

  @Override
  protected ConcurrentHashMap<Class<? extends Annotation>, DefaultConstructFactory> getSupportComponentTypeToConstructFactory() {

    logger.info("Get support component type construct factories.");
    ConcurrentHashMap<Class<? extends Annotation>, DefaultConstructFactory> componentTypeToConstructFactory = new ConcurrentHashMap<Class<? extends Annotation>, DefaultConstructFactory>();
    componentTypeToConstructFactory.put(Controller.class, new DefaultConstructFactory(
      PropertiesLoader.loadProperties(contextConfigLocation)));
    return componentTypeToConstructFactory;
  }

  private String contextConfigLocation;

  private static final String WEB_APPLICATION_CONTEXT = "webApplicationContext";

  private static final String CONTEXT_CONFIG_LOCATION = "contextConfigLocation";

  private static final Logger logger = LoggerFactory.getLogger(WebContextLoader.class);
}
