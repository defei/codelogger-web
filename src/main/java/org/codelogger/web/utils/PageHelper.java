package org.codelogger.web.utils;

public class PageHelper {

  public static int pageCount(final long totalCount, final long countPerPage) {

    return (int) (totalCount % countPerPage > 0 ? totalCount / countPerPage + 1
        : totalCount / countPerPage);
  }
}
