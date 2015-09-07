package org.codelogger.web.servlet;

import static org.codelogger.utils.StringUtils.isBlank;
import static org.codelogger.utils.StringUtils.isNotBlank;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelogger.utils.ArrayUtils;
import org.codelogger.utils.StringUtils;
import org.codelogger.utils.ValueUtils;
import org.codelogger.utils.beans.StorageComponent;
import org.codelogger.web.context.WebApplicationContext;
import org.codelogger.web.context.stereotype.Param;
import org.codelogger.web.context.stereotype.RequestMapping;
import org.codelogger.web.context.stereotype.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet extends HttpServlet {

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    MappingToMethod currentMappingToMethod = findMappingMethodByRequestURI(req);
    Method method = currentMappingToMethod.getMethod();
    if (isMethodAllowed(method, RequestMethod.GET)) {
      if (currentMappingToMethod != null && method != null) {
        Object controller = currentMappingToMethod.getController();
        Object data = invokeMethod(req, resp, method, controller);
        if (data != null) {
          req.getRequestDispatcher(data.toString()).forward(req, resp);
        }
      }
    } else {
      resp.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "GET method not allowed");
      return;
    }
  }

  private Boolean isMethodAllowed(final Method method, final RequestMethod requestMethod) {

    RequestMapping requestMappingOfMethod = method.getAnnotation(RequestMapping.class);
    return requestMappingOfMethod.method().length == 0
      || ArrayUtils.contains(requestMappingOfMethod.method(), requestMethod);
  }

  private MappingToMethod findMappingMethodByRequestURI(final HttpServletRequest req) {

    logger.info("Find method for uri:'{}'", req.getRequestURI());
    List<String> mappings = StringUtils.getStringsByRegex(req.getRequestURI(), MAPPING_DELIMITER);
    MappingToMethod currentMappingToMethod = mappingToMethod;
    for (String mapping : mappings) {
      currentMappingToMethod = currentMappingToMethod.get(mapping);
    }
    return currentMappingToMethod;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object invokeMethod(final HttpServletRequest req, final HttpServletResponse resp,
    final Method method, final Object controller) {

    try {
      Type[] parameterTypes = method.getGenericParameterTypes();
      if (ArrayUtils.isNotEmpty(parameterTypes)) {
        Object[] params = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
          Type parameterType = parameterTypes[i];
          if (parameterType.equals(HttpServletRequest.class)) {
            params[i] = req;
          } else if (parameterType.equals(HttpServletResponse.class)) {
            params[i] = resp;
          } else {
            Annotation[] annotations = method.getParameterAnnotations()[i];
            for (Annotation annotation : annotations) {
              if (annotation instanceof Param) {
                Param param = (Param) annotation;
                String parameterValue = req.getParameter(param.value());
                if (param.required() && parameterValue == null && isBlank(param.defaultValue())) {
                  resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Missing Param:" + param.value());
                  return null;
                } else {
                  String value = parameterValue == null ? param.defaultValue() : parameterValue;
                  if (parameterType.equals(String.class)) {
                    params[i] = value;
                  } else if (parameterType.equals(Integer.class) || parameterType.equals(int.class)) {
                    params[i] = Integer.valueOf(value);
                  } else if (parameterType.equals(Long.class) || parameterType.equals(long.class)) {
                    params[i] = Long.valueOf(value);
                  } else if (parameterType.equals(Boolean.class)
                    || parameterType.equals(boolean.class)) {
                    params[i] = Boolean.valueOf(value);
                  } else if (parameterType.equals(Double.class)
                    || parameterType.equals(double.class)) {
                    params[i] = Integer.valueOf(value);
                  } else if (parameterType.equals(Float.class) || parameterType.equals(float.class)) {
                    params[i] = Integer.valueOf(value);
                  } else if (parameterType.equals(Byte.class) || parameterType.equals(byte.class)) {
                    params[i] = Byte.valueOf(value);
                  } else if (parameterType.equals(Character.class)
                    || parameterType.equals(char.class)) {
                    params[i] = value.charAt(0);
                  } else if (((Class<?>) parameterType).isEnum()) {
                    params[i] = ValueUtils.getEnumInstance((Class) parameterType, value);
                  }
                }
              }
            }
          }
        }
        return method.invoke(controller, params);
      } else {
        return method.invoke(controller);
      }
    } catch (Exception e) {
      logger.error("invoke method[{}] failed.", method, e);
    }
    return null;
  }

  @Override
  public void init() throws ServletException {

    logger.info("init Servlet");
    ServletContext servletContext = getServletContext();
    logger.info("Real path of '/':[{}]", servletContext.getRealPath("/"));
    String contextPath = servletContext.getContextPath();
    MappingToMethod contextMappingToMethod = mappingToMethod;
    if (isNotBlank(contextPath)) {
      contextMappingToMethod = contextMappingToMethod.get(contextPath);
    }
    logger.info("getContextPath:[{}]", contextPath);
    webApplicationContext = (WebApplicationContext) servletContext
      .getAttribute("webApplicationContext");
    if (webApplicationContext != null) {
      Set<Object> controllers = webApplicationContext.getControllers();
      for (Object controller : controllers) {
        Class<? extends Object> controllerClass = controller.getClass();
        RequestMapping requestMappingOfClass = controllerClass.getAnnotation(RequestMapping.class);
        MappingToMethod classMappingToClass = contextMappingToMethod;
        String fullMappingOfClass = "";
        if (isNotBlank(requestMappingOfClass.value())) {
          String[] mappings = requestMappingOfClass.value().split("/");
          for (String mapping : mappings) {
            String fixedMapping = "/" + mapping;
            classMappingToClass = classMappingToClass.get(fixedMapping);
            fullMappingOfClass += fixedMapping;
          }
        }

        Method[] declaredMethods = controllerClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
          String fullMappingOfMethod = fullMappingOfClass;
          MappingToMethod methodMappingToMethod = classMappingToClass;
          RequestMapping requestMappingOfMethod = method.getAnnotation(RequestMapping.class);
          if (requestMappingOfMethod != null) {
            if (isNotBlank(requestMappingOfMethod.value())) {
              String[] mappings = requestMappingOfMethod.value().split("/");
              for (String mapping : mappings) {
                String fixedMapping = "/" + mapping;
                methodMappingToMethod = methodMappingToMethod.get(fixedMapping);
                fullMappingOfMethod += fixedMapping;
              }
            }
            logger.info("set mapping method {} to mapping:'{}'", method, fullMappingOfMethod);
            methodMappingToMethod.setController(controller);
            methodMappingToMethod.setMethod(method);
          }
        }
      }
    }
  }

  private static class MappingToMethod extends StorageComponent<String, MappingToMethod> {

    private Object controller;

    private Method method;

    @Override
    public MappingToMethod get(final String key) {

      MappingToMethod mappingToMethod = super.get(key);
      if (mappingToMethod == null) {
        mappingToMethod = new MappingToMethod();
        put(mappingToMethod, key);
      }
      return mappingToMethod;
    }

    public void setController(final Object controller) {

      this.controller = controller;
    }

    public void setMethod(final Method method) {

      this.method = method;
    }

    public Object getController() {

      return controller;
    }

    public Method getMethod() {

      return method;
    }

  }

  private MappingToMethod mappingToMethod = new MappingToMethod();

  private WebApplicationContext webApplicationContext;

  private static final String MAPPING_DELIMITER = "/?[^/]+";

  private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

  private static final long serialVersionUID = 3914231689096263496L;

}
