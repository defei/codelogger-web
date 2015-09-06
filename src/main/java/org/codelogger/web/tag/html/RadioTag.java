package org.codelogger.web.tag.html;

import static java.lang.String.format;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Objects;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.codelogger.utils.ArrayUtils;
import org.codelogger.utils.ClassUtils;

public class RadioTag extends TagSupport {

  private static final long serialVersionUID = 6351629965825001300L;

  private String name;

  private String value;

  private Object element;

  private Iterable<?> elements;

  public void setName(final String name) {

    this.name = name;
  }

  public void setValue(final String value) {

    this.value = value;
  }

  public void setElement(final Object element) {

    this.element = element;
  }

  public void setElements(final Object elements) {

    if (elements != null) {
      if (elements instanceof Iterable) {
        this.elements = (Iterable<?>) elements;
      } else if (ArrayUtils.isArray(elements)) {
        this.elements = ArrayUtils.toList(elements, ClassUtils.getComponentClass(elements));
      }
    }
  }

  @Override
  public int doStartTag() throws JspException {

    JspWriter out = pageContext.getOut();
    try {
      Boolean elementContainsTarget = false;
      if (elements != null) {
        for (Object e : elements) {
          if (Objects.equals(e, element)) {
            elementContainsTarget = true;
            break;
          } else if (e instanceof Number && element instanceof Number) {
            BigDecimal currentElement = new BigDecimal(e.toString());
            BigDecimal target = new BigDecimal(element.toString());
            if (Objects.equals(currentElement, target)) {
              elementContainsTarget = true;
              break;
            }
          }
        }
      }
      if (elementContainsTarget) {
        out.print(format("<input name=\"%s\" value=\"%s\" type=\"radio\" checked=\"checked\" >",
          name, value));
      } else {
        out.print(format("<input name=\"%s\" value=\"%s\" type=\"radio\" >", name, value));
      }
      return SKIP_BODY;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.doStartTag();
  }
}
