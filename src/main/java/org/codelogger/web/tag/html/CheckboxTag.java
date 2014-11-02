package org.codelogger.web.tag.html;

import static java.lang.String.format;

import org.codelogger.utils.JudgeUtils;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class CheckboxTag extends TagSupport {

  private static final long serialVersionUID = -2123253186591293149L;

  private String name;

  private String value;

  private Object element;

  private Iterable<? extends Object> elements;

  public void setName(String name) {

    this.name = name;
  }

  public void setValue(String value) {

    this.value = value;
  }

  public void setElement(Object element) {

    this.element = element;
  }

  public void setElements(Iterable<? extends Object> elements) {

    this.elements = elements;
  }

  @Override
  public int doStartTag() throws JspException {

    JspWriter out = pageContext.getOut();
    try {
      Boolean elementContainsTarget = false;
      if (elements != null) {
        for (Object e : elements) {
          if (JudgeUtils.equals(e, element)) {
            elementContainsTarget = true;
            break;
          }
        }
      }
      if (elementContainsTarget) {
        out.print(
            format("<input name=\"%s\" value=\"%s\" type=\"checkbox\" checked=\"checked\" >", name,
                value));
      } else {
        out.print(format("<input name=\"%s\" value=\"%s\" type=\"checkbox\" >", name, value));
      }
      return SKIP_BODY;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.doStartTag();
  }
}
