package org.codelogger.web.context;

import java.util.concurrent.ConcurrentHashMap;

import org.codelogger.core.context.ApplicationContext;

public class WebApplicationContext extends ApplicationContext {

  public WebApplicationContext(final ApplicationContext applicationContext) {

    super(applicationContext);
  }

  protected WebApplicationContext(final ConcurrentHashMap<Class<?>, Object> typeToBean) {

    super(typeToBean);
  }

  protected WebApplicationContext(final ApplicationContext applicationContext,
    final ConcurrentHashMap<Class<?>, Object> typeToBean) {

    super(applicationContext, typeToBean);
  }

}
