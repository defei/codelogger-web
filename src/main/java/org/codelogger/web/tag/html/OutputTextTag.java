package org.codelogger.web.tag.html;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class OutputTextTag extends TagSupport {

  private static final long serialVersionUID = 3423388519504918905L;

  private String value;

  private Boolean escape = true;

  public void setValue(String value) {

    this.value = value;
  }

  public void setEscape(Boolean escape) {

    this.escape = escape;
  }

  @Override
  public int doStartTag() throws JspException {

    try {
      if (value != null) {
        JspWriter out = pageContext.getOut();
        if (escape) {
          out.print(value.replaceAll("(\r\n|\n\r|\r|\n)", "<br/>").replace(" ", "&amp;"));
        } else {
          out.print(value);
        }
      }
      return SKIP_BODY;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.doStartTag();
  }
}
