package org.codelogger.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelogger.web.bean.MarvelServlet;

public abstract class MarvelFilter<T extends MarvelServlet> implements Filter {

  private static ThreadLocal<MarvelServlet> filterThreadLocal = new ThreadLocal<MarvelServlet>();

  public static <T extends MarvelServlet> T getMarvelServelt() {

    @SuppressWarnings("unchecked")
    T marvelServlet = (T) filterThreadLocal.get();
    return marvelServlet;
  }

  public abstract T buildMarvelServelt();

  public abstract void doCustomFilter(ServletRequest request, ServletResponse response,
    FilterChain arg2) throws IOException, ServletException;

  @Override
  public void destroy() {

  }

  @Override
  public final void doFilter(ServletRequest request, ServletResponse response, FilterChain arg2)
    throws IOException, ServletException {

    MarvelServlet marvelServelt = buildMarvelServelt();
    marvelServelt.setRequest((HttpServletRequest) request);
    marvelServelt.setResponse((HttpServletResponse) response);
    filterThreadLocal.set(marvelServelt);
    doCustomFilter(request, response, arg2);
    arg2.doFilter(request, response);
  }

  @Override
  public void init(FilterConfig arg0) throws ServletException {

  }

}
