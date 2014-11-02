package org.codelogger.web.tag.html;

import static java.lang.String.format;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class ScriptTag extends TagSupport {

  private static final long serialVersionUID = 7380923841561845556L;

  private String src;

  public void setSrc(String src) {

    this.src = src;
  }

  @Override
  public int doStartTag() throws JspException {

    JspWriter out = pageContext.getOut();
    try {
      out.print(format("<script src=\"%s\"></script>", src));
      return SKIP_BODY;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.doStartTag();
  }
}
