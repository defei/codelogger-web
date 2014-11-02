package org.codelogger.web.utils;

import static java.lang.String.format;
import static org.codelogger.utils.StringUtils.isBlank;

import org.codelogger.utils.ArrayUtils;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HttpServletHelper {

  public static String getCookieValue(final HttpServletRequest request, final String key) {

    Cookie[] cookies = request.getCookies();
    if (ArrayUtils.isEmpty(cookies)) {
      return null;
    } else {
      String value = null;
      for (Cookie cookie : cookies) {
        if (cookie.getName().equals(key)) {
          value = cookie.getValue();
          break;
        }
      }
      return value;
    }
  }

  public static void putSessionValue(final HttpServletRequest request, final String key,
      final Object value) {

    request.getSession().setAttribute(key, value);
  }

  public static <T> T getSessionValue(final HttpServletRequest request, final String key) {

    @SuppressWarnings("unchecked") T value = (T) request.getSession().getAttribute(key);
    return value;
  }

  public static boolean getBooleanValueFromSession(final HttpServletRequest request,
      final String key) {

    Boolean booleanValue = getSessionValue(request, key);
    return booleanValue == null ? false : booleanValue;
  }

  public static void setCookie(final HttpServletResponse response, final String key,
      final String value, final int expireTime, final String path) {

    Cookie cookie = new Cookie(key, value);
    cookie.setMaxAge(expireTime);
    cookie.setPath(path);
    response.addCookie(cookie);
  }

  public static String getClientIP(final HttpServletRequest request) {

    String ipAddress = request.getHeader("X-FORWARDED-FOR");
    if (ipAddress == null) {
      ipAddress = request.getRemoteAddr();
    }
    return ipAddress;
  }

  public static String getRequestURIWithParameters(HttpServletRequest request) {

    String requestURI = request.getRequestURI();
    String queryString = request.getQueryString();
    return isBlank(queryString) ? requestURI : format("%s?%s", requestURI, queryString);
  }

  public static Map<String, String> getHeaders(HttpServletRequest request) {

    Enumeration<?> headerNames = request.getHeaderNames();
    Map<String, String> headers = new HashMap<String, String>();
    while (headerNames.hasMoreElements()) {
      String nextHeader = headerNames.nextElement().toString();
      String nextHeaderValue = request.getHeader(nextHeader);
      headers.put(nextHeader, nextHeaderValue);
    }
    return headers;
  }

  public static Boolean isMobileDevices(HttpServletRequest request) {

    request.getHeader("");
    return false;
  }
}