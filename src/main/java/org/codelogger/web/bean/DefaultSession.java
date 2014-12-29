package org.codelogger.web.bean;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionContext;

import org.codelogger.utils.MD5Utils;

@SuppressWarnings("deprecation")
public class DefaultSession extends HashMap<String, Object> implements HttpSession {

  private static final long serialVersionUID = 8051026116794308716L;

  private Long creationTime;

  private Long lastAccessedTime;

  public DefaultSession() {

    creationTime = System.currentTimeMillis();
  }

  @Override
  public long getCreationTime() {

    return creationTime;
  }

  @Override
  public String getId() {

    return MD5Utils.getMD5(creationTime.toString());
  }

  @Override
  public long getLastAccessedTime() {

    return lastAccessedTime;
  }

  @Override
  public ServletContext getServletContext() {

    return null;
  }

  @Override
  public void setMaxInactiveInterval(final int interval) {

  }

  @Override
  public int getMaxInactiveInterval() {

    return 0;
  }

  @Override
  public HttpSessionContext getSessionContext() {

    return null;
  }

  @Override
  public Object getAttribute(final String name) {

    return get(name);
  }

  @Override
  public Object getValue(final String name) {

    return get(name);
  }

  @Override
  public Enumeration<java.util.Map.Entry<String, Object>> getAttributeNames() {

    Enumeration<java.util.Map.Entry<String, Object>> enumeration = new Enumeration<java.util.Map.Entry<String, Object>>() {

      Iterator<java.util.Map.Entry<String, Object>> iterator = entrySet().iterator();

      @Override
      public boolean hasMoreElements() {

        return iterator.hasNext();
      }

      @Override
      public java.util.Map.Entry<String, Object> nextElement() {

        return iterator.next();
      }
    };
    return enumeration;
  }

  @Override
  public String[] getValueNames() {

    return keySet().toArray(new String[0]);
  }

  @Override
  public void setAttribute(final String name, final Object value) {

    put(name, value);
  }

  @Override
  public void putValue(final String name, final Object value) {

    put(name, value);
  }

  @Override
  public void removeAttribute(final String name) {

    remove(name);
  }

  @Override
  public void removeValue(final String name) {

    remove(name);
  }

  @Override
  public void invalidate() {

  }

  @Override
  public boolean isNew() {

    return false;
  }

}
