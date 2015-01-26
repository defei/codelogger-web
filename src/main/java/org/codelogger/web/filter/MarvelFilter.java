package org.codelogger.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.codelogger.web.bean.MarvelServlet;

public abstract class MarvelFilter<T extends MarvelServlet> implements Filter {

  private static ThreadLocal<MarvelServlet> filterThreadLocal = new ThreadLocal<MarvelServlet>();

  public static <T extends MarvelServlet> T getMarvelServlet() {

    @SuppressWarnings("unchecked")
    T marvelServlet = (T) filterThreadLocal.get();
    return marvelServlet;
  }

  public abstract T buildMarvelServlet();

  public abstract void doFilter(HttpServletRequest request, HttpServletResponse response,
    FilterChain filterChain) throws IOException, ServletException;

  @Override
  public final void doFilter(final ServletRequest request, final ServletResponse response,
    final FilterChain filterChain) throws IOException, ServletException {

    MarvelServlet marvelServlet = buildMarvelServlet();
    HttpServletRequest httpServletRequest = (HttpServletRequest) request;
    HttpServletResponse httpServletResponse = (HttpServletResponse) response;
    marvelServlet.setRequest(httpServletRequest);
    marvelServlet.setResponse(httpServletResponse);
    filterThreadLocal.set(marvelServlet);
    doFilter(httpServletRequest, httpServletResponse, filterChain);
  }

}
