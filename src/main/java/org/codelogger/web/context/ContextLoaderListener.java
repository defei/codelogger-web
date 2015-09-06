package org.codelogger.web.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextLoaderListener extends WebContextLoader implements ServletContextListener {

  private static final Logger logger = LoggerFactory.getLogger(ContextLoaderListener.class);

  @Override
  public void contextInitialized(final ServletContextEvent sce) {

    logger
      .debug("execut org.codelogger.web.context.ContextLoaderListener.contextInitialized(ServletContextEvent)");
    WebApplicationContext webApplicationContext = initWebApplicationContext(sce.getServletContext());
    sce.getServletContext().setAttribute("webApplicationContext", webApplicationContext);
  }

  @Override
  public void contextDestroyed(final ServletContextEvent sce) {

    logger
      .debug("execut org.codelogger.web.context.ContextLoaderListener.contextDestroyed(ServletContextEvent)");
  }

}
