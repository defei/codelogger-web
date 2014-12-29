package org.codelogger.web.controller;

import static java.lang.String.format;

import org.codelogger.web.bean.MarvelServlet;
import org.codelogger.web.filter.MarvelFilter;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

  protected final String WELCOME_PAGE = "redirect:/";

  public static <T extends MarvelServlet> T getMarvelServlet() {

    return MarvelFilter.getMarvelServlet();
  }

  protected void addAttribute(final String key, final Object value) {

    getMarvelServlet().getRequest().setAttribute(key, value);
  }

  protected String forward(final String target) {

    return format("forward:%s", target);
  }

  protected String redirect(final String target) {

    return format("redirect:%s", target);
  }
}
