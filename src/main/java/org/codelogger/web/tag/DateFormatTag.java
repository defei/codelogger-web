package org.codelogger.web.tag;

import static org.codelogger.utils.DateUtils.getCurrentDate;
import static org.codelogger.utils.DateUtils.getDateFormat;

import java.io.IOException;
import java.util.Date;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

public class DateFormatTag extends TagSupport {

  private static final long serialVersionUID = -2123253186591293149L;

  private String pattern;

  private Object date;

  public void setPattern(String pattern) {

    this.pattern = pattern;
  }

  public void setDate(Object date) {

    if (date != null) {
      this.date = date instanceof Date ? date : new Date((Long) date);
    }
  }

  @Override
  public int doStartTag() throws JspException {

    JspWriter out = pageContext.getOut();
    try {
      out.print(getDateFormat(date == null ? getCurrentDate() : (Date) date, pattern));
    } catch (IOException e) {
      e.printStackTrace();
    }
    return super.doStartTag();
  }
}
