package org.codelogger.web.controller;

import static java.lang.String.format;

import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

  protected final String WELCOME_PAGE = "redirect:/";

  protected String forward(final String target) {

    return format("forward:%s", target);
  }

  protected String redirect(final String target) {

    return format("redirect:%s", target);
  }
}
