package org.codelogger.web.servlet;

import static com.google.common.collect.Maps.newHashMap;
import static org.codelogger.utils.StringUtils.isBlank;
import static org.codelogger.utils.StringUtils.isNotBlank;
import static org.codelogger.utils.lang.CharacterEncoding.UTF_8;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.http.entity.ContentType;
import org.codelogger.core.bean.tuple.TwoTuple;
import org.codelogger.core.utils.JsonUtils;
import org.codelogger.utils.ArrayUtils;
import org.codelogger.utils.CollectionUtils;
import org.codelogger.utils.IOUtils;
import org.codelogger.utils.PathUtils;
import org.codelogger.utils.StringUtils;
import org.codelogger.utils.ValueUtils;
import org.codelogger.utils.beans.StorageComponent;
import org.codelogger.web.bean.MemoryFile;
import org.codelogger.web.context.WebApplicationContext;
import org.codelogger.web.context.stereotype.Param;
import org.codelogger.web.context.stereotype.PathVariable;
import org.codelogger.web.context.stereotype.RequestAttribute;
import org.codelogger.web.context.stereotype.RequestMapping;
import org.codelogger.web.context.stereotype.RequestMethod;
import org.codelogger.web.context.stereotype.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DispatcherServlet extends HttpServlet {

  @Override
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.GET);
  }

  @Override
  protected void doHead(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.HEAD);
  }

  @Override
  protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.POST);
  }

  @Override
  protected void doPut(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.PUT);
  }

  @Override
  protected void doDelete(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.DELETE);
  }

  @Override
  protected void doOptions(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.OPTIONS);
  }

  @Override
  protected void doTrace(final HttpServletRequest req, final HttpServletResponse resp)
    throws ServletException, IOException {

    dispatch(req, resp, RequestMethod.TRACE);
  }

  private void dispatch(final HttpServletRequest req, final HttpServletResponse resp,
    final RequestMethod requestMethod) throws ServletException, IOException {

    req.setAttribute(isDispatchedKey, true);
    MethodAndPathVariables currentMappingToMethod = findMappingMethodByRequestURI(requestMethod,
      req);
    Method method = currentMappingToMethod.getMethod();
    if (method != null) {
      logger.debug("Found matched method, attempt to invoke this method.");
      Object data = invokeMethod(req, resp, currentMappingToMethod);
      if (data != null) {
        Object responseBody = methodToResponseBody.get(method);
        if (responseBody == null) {
          String targetPath = data.toString();
          if (targetPath.startsWith("redirect")) {
            resp.sendRedirect(targetPath.substring(9));
          } else {
            String targetJsp = fixedTargetJsp(targetPath);
            req.getRequestDispatcher(targetJsp).forward(req, resp);
          }
        } else {
          String json = JsonUtils.toJson(data);
          resp.setContentType(ContentType.APPLICATION_JSON.getMimeType());
          resp.setCharacterEncoding(UTF_8.getCharsetName());
          resp.getWriter().write(json);
        }
      }
    } else {
      String fixedRequestURI = req.getRequestURI().replaceFirst(req.getContextPath(), "");
      Boolean matchedStaticResources = false;
      for (String viewResource : viewResources) {
        if (fixedRequestURI.startsWith(viewResource)) {
          matchedStaticResources = true;
          break;
        }
      }
      if (matchedStaticResources) {
        FileInputStream sourceInputStream = null;
        try {
          String fixedResourcePath = req.getRealPath(fixedRequestURI);// getFixedResourcePath(fixedRequestURI);
          logger.info("Try to load resource {}", fixedResourcePath);
          sourceInputStream = new FileInputStream(fixedResourcePath);
          IOUtils.write(sourceInputStream, resp.getOutputStream());
        } catch (FileNotFoundException e) {
          logger.info("Load resource failed for URI:{}", fixedRequestURI, e);
          resp.sendError(HttpServletResponse.SC_NOT_FOUND, fixedRequestURI + " NOT FOUND!");
        } finally {
          if (sourceInputStream != null) {
            try {
              sourceInputStream.close();
            } catch (Exception e) {
            }
          }
        }
      } else {
        logger.info("Not found any resource or mapping for URI:{}", fixedRequestURI);
        resp.sendError(HttpServletResponse.SC_NOT_FOUND, fixedRequestURI + " NOT FOUND!");
      }
    }
  }

  private String fixedTargetJsp(final String targetJsp) {

    String fixedTargetJsp = targetJsp.startsWith("/") ? targetJsp : viewPrefix + targetJsp;
    fixedTargetJsp = fixedTargetJsp.endsWith(".jsp") ? fixedTargetJsp : fixedTargetJsp + viewSuffix;
    return fixedTargetJsp;
  }

  private MethodAndPathVariables findMappingMethodByRequestURI(final RequestMethod requestMethod,
    final HttpServletRequest req) {

    logger.info("Find method for uri:'{}'", req.getRequestURI());
    List<String> mappings = StringUtils.getStringsByRegex(req.getRequestURI(), MAPPING_DELIMITER);
    List<String> pathVariables = new ArrayList<String>(mappings.size());
    MappingToMethod targetMappingToMethod = mappingToMethod;
    for (String mapping : mappings) {
      if (targetMappingToMethod == null) {
        break;
      }
      MappingToMethod currentMappingToMethod = targetMappingToMethod.get(mapping);
      if (!currentMappingToMethod.hasMore()
        && currentMappingToMethod.getMethod(requestMethod) == null) {
        currentMappingToMethod = targetMappingToMethod.get("/*");
        pathVariables.add(mapping.substring(1));
      }
      targetMappingToMethod = currentMappingToMethod;
    }
    return new MethodAndPathVariables(targetMappingToMethod.getMethod(requestMethod),
      targetMappingToMethod.getController(), pathVariables);
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  private Object invokeMethod(final HttpServletRequest req, final HttpServletResponse resp,
    final MethodAndPathVariables methodAndPathVariables) {

    Method method = methodAndPathVariables.getMethod();
    Object controller = methodAndPathVariables.getController();
    if (!controllerClassToRequestAttribute.isEmptyMethodAndAttributeKeyPairs(controller.getClass())) {
      List<TwoTuple<Method, String>> methodAndAttributeKeyPaires = controllerClassToRequestAttribute
        .get(controller.getClass());
      for (TwoTuple<Method, String> methodAndAttributeKeyPaire : methodAndAttributeKeyPaires) {
        try {
          logger.debug("Invoke method {} of controller {}.", method, controller);
          Object invokeResult = methodAndAttributeKeyPaire.first.invoke(controller);
          req.setAttribute(methodAndAttributeKeyPaire.second, invokeResult);
        } catch (Exception e) {
          logger.warn("Set request attribute {} failed.", methodAndAttributeKeyPaire.second, e);
        }
      }
    }
    try {
      Type[] parameterTypes = method.getGenericParameterTypes();
      if (ArrayUtils.isNotEmpty(parameterTypes)) {
        List<String> pathVariables = methodAndPathVariables.getPathVariables();
        int pathVariableIndex = 0;
        Object[] params = new Object[parameterTypes.length];
        boolean isMultipartContent = ServletFileUpload.isMultipartContent(req);
        Map<String, Object> requestDataOfThisForm = null;
        if (isMultipartContent) {
          requestDataOfThisForm = newHashMap();
          try {
            List<FileItem> items = new ServletFileUpload(new DiskFileItemFactory())
              .parseRequest(req);
            for (FileItem item : items) {
              if (item.isFormField()) {
                String fieldName = item.getFieldName();
                String fieldValue = item.getString(requestEncoding);
                requestDataOfThisForm.put(fieldName, fieldValue);
              } else {
                String fieldName = item.getFieldName();
                String fileName = FilenameUtils.getName(item.getName());
                byte[] bytes = IOUtils.getBytes(item.getInputStream());
                MemoryFile value = new MemoryFile(fileName, bytes);
                requestDataOfThisForm.put(fieldName, value);
              }
            }
          } catch (FileUploadException e) {
            throw new ServletException("Cannot parse multipart request.", e);
          }
        }
        for (int i = 0; i < parameterTypes.length; i++) {
          Type parameterType = parameterTypes[i];
          if (parameterType.equals(HttpServletRequest.class)) {
            params[i] = req;
          } else if (parameterType.equals(HttpServletResponse.class)) {
            params[i] = resp;
          } else {
            Annotation[] annotations = method.getParameterAnnotations()[i];
            for (Annotation annotation : annotations) {
              Object value = null;
              if (annotation instanceof Param) {
                Param param = (Param) annotation;
                Object parameterValue = isMultipartContent ? requestDataOfThisForm.get(param
                  .value()) : req.getParameter(param.value());
                if (param.required() && parameterValue == null && isBlank(param.defaultValue())) {
                  resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Missing Param:" + param.value());
                  return null;
                } else {
                  value = parameterValue == null ? param.defaultValue() : parameterValue;
                }
              } else if (annotation instanceof PathVariable) {
                value = pathVariables.get(pathVariableIndex++);
              }
              if (value != null) {
                if (parameterType.equals(String.class)) {
                  params[i] = encodingString(value.toString());
                } else if (parameterType.equals(Integer.class) || parameterType.equals(int.class)) {
                  params[i] = Integer.valueOf(value.toString());
                } else if (parameterType.equals(Long.class) || parameterType.equals(long.class)) {
                  params[i] = Long.valueOf(value.toString());
                } else if (parameterType.equals(Boolean.class)
                  || parameterType.equals(boolean.class)) {
                  params[i] = Boolean.valueOf(value.toString());
                } else if (parameterType.equals(Double.class) || parameterType.equals(double.class)) {
                  params[i] = Integer.valueOf(value.toString());
                } else if (parameterType.equals(Float.class) || parameterType.equals(float.class)) {
                  params[i] = Integer.valueOf(value.toString());
                } else if (parameterType.equals(Byte.class) || parameterType.equals(byte.class)) {
                  params[i] = Byte.valueOf(value.toString());
                } else if (parameterType.equals(Character.class)
                  || parameterType.equals(char.class)) {
                  params[i] = value.toString().charAt(0);
                } else if (((Class<?>) parameterType).isEnum()) {
                  params[i] = ValueUtils.getEnumInstance((Class) parameterType, value.toString());
                } else if (parameterType.equals(MemoryFile.class)) {
                  params[i] = value;
                }
              }
            }
          }
        }
        logger.debug("Invoke method {} of controller {} with params {}.", method, controller,
          params);
        return method.invoke(controller, params);
      } else {
        logger.debug("Invoke method {} of controller {}.", method, controller);
        return method.invoke(controller);
      }
    } catch (Exception e) {
      logger.error("invoke method[{}] failed.", method, e);
    }
    return null;
  }

  private String encodingString(final String originValue) {

    try {
      return new String(originValue.getBytes(DEFAULT_CHARSET), requestEncoding);
    } catch (UnsupportedEncodingException e) {
      return originValue;
    }
  }

  @Override
  public void init() throws ServletException {

    logger.info("init Servlet");
    ServletContext servletContext = getServletContext();
    String contextPath = servletContext.getContextPath();
    MappingToMethod contextMappingToMethod = mappingToMethod;
    if (isNotBlank(contextPath)) {
      contextMappingToMethod = contextMappingToMethod.get(contextPath);
    }
    logger.info("getContextPath:[{}]", contextPath);
    webApplicationContext = (WebApplicationContext) servletContext
      .getAttribute("webApplicationContext");
    if (webApplicationContext != null) {
      viewPrefix = webApplicationContext.getConfigurations().getProperty("view-prefix");
      viewSuffix = webApplicationContext.getConfigurations().getProperty("view-suffix");
      requestEncoding = webApplicationContext.getConfigurations().getProperty("request-encoding");
      String viewResourcesString = webApplicationContext.getConfigurations().getProperty(
        "view-resources");
      viewResources = viewResourcesString == null ? null : viewResourcesString.split(" ");
      logger.info("Resource views path:[{}]", ArrayUtils.join(viewResources, ","));
      Set<Object> controllers = webApplicationContext.getControllers();
      for (Object controller : controllers) {
        Class<? extends Object> controllerClass = controller.getClass();
        RequestMapping requestMappingsOfClass = controllerClass.getAnnotation(RequestMapping.class);
        String fullMappingOfClass = "";
        MappingToMethod classMappingToClass = contextMappingToMethod;
        ResponseBody responseBodyOfClass = controllerClass.getAnnotation(ResponseBody.class);
        if (requestMappingsOfClass != null) {
          if (ArrayUtils.isNotEmpty(requestMappingsOfClass.value())) {
            for (String requestMappingOfClass : requestMappingsOfClass.value()) {
              if (isNotBlank(requestMappingOfClass)) {
                String[] mappings = requestMappingOfClass.split("/");
                for (String mapping : mappings) {
                  String fixedMapping = mapping.startsWith("{") && mapping.endsWith("}") ? "/*"
                    : "/" + mapping;
                  classMappingToClass = classMappingToClass.get(fixedMapping);
                  fullMappingOfClass += "/" + mapping;
                }
              }
            }
          }
        }

        Method[] declaredMethods = controllerClass.getDeclaredMethods();
        for (Method method : declaredMethods) {
          String fullMappingOfMethod = fullMappingOfClass;
          MappingToMethod methodMappingToMethod = classMappingToClass;
          RequestMapping requestMappingsOfMethod = method.getAnnotation(RequestMapping.class);
          if (requestMappingsOfMethod != null) {
            if (ArrayUtils.isNotEmpty(requestMappingsOfMethod.value())) {
              for (String requestMappingOfMethod : requestMappingsOfMethod.value()) {
                String[] mappings = requestMappingOfMethod.split("/");
                for (String mapping : mappings) {
                  String fixedMapping = mapping.startsWith("{") && mapping.endsWith("}") ? "/*"
                    : "/" + mapping;
                  methodMappingToMethod = methodMappingToMethod.get(fixedMapping);
                  fullMappingOfMethod += "/" + mapping;
                }
              }
            }
            logger.info("set mapping method {} to mapping:'{}'", method, fullMappingOfMethod);
            methodMappingToMethod.setController(controller);
            RequestMethod[] supportedMethods = requestMappingsOfMethod.method();
            if (supportedMethods == null) {
              supportedMethods = RequestMethod.values();
            }
            for (RequestMethod supportedMethod : supportedMethods) {
              methodMappingToMethod.setMethod(supportedMethod, method);
            }
            ResponseBody responseBodyOfMethod = method.getAnnotation(ResponseBody.class);
            methodToResponseBody.put(method, responseBodyOfMethod == null ? responseBodyOfClass
              : responseBodyOfMethod);
          }
          RequestAttribute requestAttribute = method.getAnnotation(RequestAttribute.class);
          if (requestAttribute != null) {
            List<TwoTuple<Method, String>> methodAndAttributeKeyPair = controllerClassToRequestAttribute
              .get(controllerClass);
            methodAndAttributeKeyPair.add(TwoTuple.newTwoTuple(method, requestAttribute.value()));
          }
        }
      }
    }
  }

  private static class MappingToMethod extends StorageComponent<String, MappingToMethod> {

    private Object controller;

    private Map<RequestMethod, Method> requestMethodToMethod;

    @Override
    public MappingToMethod get(final String key) {

      MappingToMethod mappingToMethod = super.get(key);
      if (mappingToMethod == null) {
        mappingToMethod = new MappingToMethod();
        put(mappingToMethod, key);
      }
      return mappingToMethod;
    }

    public Boolean hasMore() {

      return size() > 0;
    }

    public void setController(final Object controller) {

      this.controller = controller;
    }

    public void setMethod(final RequestMethod requestMethod, final Method method) {

      if (requestMethodToMethod == null) {
        synchronized (MappingToMethod.class) {
          if (requestMethodToMethod == null) {
            requestMethodToMethod = newHashMap();
          }
        }
      }
      requestMethodToMethod.put(requestMethod, method);
    }

    public Object getController() {

      return controller;
    }

    public Method getMethod(final RequestMethod requestMethod) {

      return requestMethodToMethod.get(requestMethod);
    }

  }

  private static class MethodAndPathVariables {

    private Method method;

    private Object controller;

    List<String> pathVariables;

    public MethodAndPathVariables(final Method method, final Object controller,
      final List<String> pathVariables) {

      this.method = method;
      this.controller = controller;
      this.pathVariables = pathVariables;
    }

    public Object getController() {

      return controller;
    }

    public Method getMethod() {

      return method;
    }

    public List<String> getPathVariables() {

      return pathVariables;
    }

  }

  private static class ControllerClassToRequestAttribute extends
    HashMap<Class<?>, List<TwoTuple<Method, String>>> {

    private static final long serialVersionUID = 1210375561646014915L;

    @Override
    public List<TwoTuple<Method, String>> get(final Object key) {

      List<TwoTuple<Method, String>> methodAndAttributeKeyPairs = super.get(key);
      if (methodAndAttributeKeyPairs == null) {
        synchronized (ControllerClassToRequestAttribute.class) {
          methodAndAttributeKeyPairs = new ArrayList<TwoTuple<Method, String>>();
          put((Class<?>) key, methodAndAttributeKeyPairs);
        }
      }
      return methodAndAttributeKeyPairs;
    }

    public Boolean isEmptyMethodAndAttributeKeyPairs(final Class<?> key) {

      return CollectionUtils.isEmpty(super.get(key));
    }

  }

  private String webRootPath = PathUtils.getWebProjectPath(this);

  private String requestEncoding = "UTF-8";

  private String viewPrefix;

  private String viewSuffix;

  private String[] viewResources;

  private Map<Method, Object> methodToResponseBody = newHashMap();

  private MappingToMethod mappingToMethod = new MappingToMethod();

  private ControllerClassToRequestAttribute controllerClassToRequestAttribute = new ControllerClassToRequestAttribute();

  private WebApplicationContext webApplicationContext;

  public static final String DEFAULT_CHARSET = "ISO-8859-1";

  private static final String isDispatchedKey = "_CL_DS_D_";

  private static final String MAPPING_DELIMITER = "/?[^/]+";

  private static final Logger logger = LoggerFactory.getLogger(DispatcherServlet.class);

  private static final long serialVersionUID = 3914231689096263496L;

}
