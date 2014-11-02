package org.codelogger.web.tag;

import org.codelogger.utils.ObjectUtils;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class GetValueTag extends TagSupport {

  private static final long serialVersionUID = -8022518639705814733L;

  private Object defaultValue;

  private Object value;

  public void setDefaultValue(Object defaultValue) {

    this.defaultValue = defaultValue;
  }

  public void setValue(Object value) {

    this.value = value;
  }

  @Override
  public int doStartTag() throws JspException {

    JspWriter out = pageContext.getOut();
    try {
      out.print(value == null ? ObjectUtils.toString(defaultValue) : value.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.doStartTag();
  }
}
