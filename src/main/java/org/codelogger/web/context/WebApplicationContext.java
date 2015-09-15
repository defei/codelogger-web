package org.codelogger.web.context;

import static com.google.common.collect.Sets.newHashSet;

import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codelogger.core.context.ApplicationContext;
import org.codelogger.utils.MapUtils;
import org.codelogger.web.context.stereotype.Controller;

public class WebApplicationContext extends ApplicationContext {

  public Set<Object> getControllers() {

    return controllers;
  }

  public WebApplicationContext(final ApplicationContext applicationContext) {

    super(applicationContext, applicationContext.getConfigurations());
    updateControllers();
  }

  protected WebApplicationContext(final ConcurrentHashMap<Class<?>, Object> typeToBean,
    final Properties configurations) {

    super(typeToBean, configurations);
    updateControllers();
  }

  protected WebApplicationContext(final ApplicationContext applicationContext,
    final ConcurrentHashMap<Class<?>, Object> typeToBean, final Properties configurations) {

    super(applicationContext, typeToBean, configurations);
    updateControllers();
  }

  private void updateControllers() {

    Set<Object> controllers = newHashSet();
    if (MapUtils.isNotEmpty(typeToBean)) {
      for (Entry<Class<?>, Object> typeWithBean : typeToBean.entrySet()) {
        if (typeWithBean.getKey().isAnnotationPresent(Controller.class)) {
          controllers.add(typeWithBean.getValue());
        }
      }
    }
    this.controllers = controllers;
  }

  private Set<Object> controllers;
}
