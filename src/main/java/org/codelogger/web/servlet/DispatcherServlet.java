package org.codelogger.web.servlet;

import static org.codelogger.utils.StringUtils.isNotBlank;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
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
import org.codelogger.utils.beans.StorageComponent;
import org.codelogger.web.context.WebApplicationContext;
import org.codelogger.web.context.stereotype.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet extends HttpServlet {

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    logger.info("Received {} GET request by URI.", req.getRequestURI());
    if (req.getAttribute("processed") == null) {
      logger.info("改变请求的URI");
      String regex = "/?[^/]+";
      List<String> mappings = StringUtils.getStringsByRegex(req.getRequestURI(), regex);
      MappingToMethod currentMappingToMethod = mappingToMethod;
      for (String mapping : mappings) {
        currentMappingToMethod = currentMappingToMethod.get(mapping);
      }
      Method method = currentMappingToMethod.getMethod();
      if (currentMappingToMethod != null && method != null) {
        Object controller = currentMappingToMethod.getController();
        logger.info("Found controller:[{}] method[{}] for '{}'", controller, method,
          req.getRequestURI());
        try {
          Type[] parameterTypes = method.getGenericParameterTypes();
          if (ArrayUtils.isNotEmpty(parameterTypes)) {
            Object[] params = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {
              Type parameterType = parameterTypes[i];
              Parameter parameter = method.getParameters()[0];
              logger.info("param typeName: {}", parameter.getName());
              if (parameterType instanceof HttpServletRequest) {
                params[i] = req;
              } else if (parameterType instanceof HttpServletRequest) {
                params[i] = resp;
              } else {
                params[i] = req.getParameter(parameterType.getTypeName());
              }
            }
            method.invoke(controller, params);
          } else {
            method.invoke(controller);
          }
        } catch (Exception e) {
          logger.error("invoke method[{}] failed.", method, e);
        }
      } else {
        logger.info("Not mapping found for '{}'", req.getRequestURI());
      }
      req.setAttribute("processed", true);
      req.getRequestDispatcher("/index.jsp").forward(req, resp);
    } else {
      logger.info("怎么又进来了!!!!!!!!!");
    }
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
        if (isNotBlank(requestMappingOfClass.value())) {
          String[] mappings = requestMappingOfClass.value().split("/");
          for (String mapping : mappings) {
            classMappingToClass = classMappingToClass.get("/" + mapping);
          }
        }

        Method[] declaredMethods = controllerClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
          MappingToMethod methodMappingToMethod = classMappingToClass;
          RequestMapping requestMappingOfMethod = method.getAnnotation(RequestMapping.class);
          if (requestMappingOfMethod != null) {
            if (isNotBlank(requestMappingOfMethod.value())) {
              String[] mappings = requestMappingOfMethod.value().split("/");
              for (String mapping : mappings) {
                methodMappingToMethod = contextMappingToMethod.get("/" + mapping);
              }
            }
            logger.info("set mapping method {}", method);
            methodMappingToMethod.setController(controller);
            methodMappingToMethod.setMethod(method);
          }
        }

        logger.info("mapping:{}");
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

  private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

  private static final long serialVersionUID = 3914231689096263496L;

}
