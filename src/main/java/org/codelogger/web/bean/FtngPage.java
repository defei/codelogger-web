package org.codelogger.web.bean;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static org.codelogger.utils.CollectionUtils.isNotEmpty;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.domain.Page;

import java.util.List;

public class FtngPage {

  private final String pageName;

  @JsonIgnore
  private List<Object> navigatorPages;

  private List<?> content;

  @JsonIgnore
  private Boolean showPreviousOmissionSymbol;

  @JsonIgnore
  private Boolean showNextOmissionSymbol;

  @JsonIgnore
  private Boolean showLastPage;

  private Boolean hasContent;

  private Boolean hasPreviousPage;

  private Boolean hasNextPage;

  private Integer currentPage;

  private Integer totalPage;

  private Long totalElements;

  public FtngPage(final String pageName, final Page<?> page, final Integer specifyTotalPage) {

    this.pageName = pageName;
    if (page != null) {
      content = page.getContent();
      hasContent = isNotEmpty(content);
      hasPreviousPage = page.hasPreviousPage();
      totalPage = specifyTotalPage == null ? page.getTotalPages() : specifyTotalPage;
      currentPage = page.getNumber() + 1;
      hasNextPage = currentPage < totalPage;
      totalElements = page.getTotalElements();

      if (specifyTotalPage != null) {
        totalPage = Math.min(page.getTotalPages(), specifyTotalPage);
        totalElements =
            Math.min(page.getTotalElements(), specifyTotalPage * page.getNumberOfElements());
      }

      Integer pageNavigatorFrom = min(max(totalPage - 4, 2), max(2, currentPage - 2));
      Integer pageNavigatorTo = min(max(currentPage + 2, 5), totalPage);
      navigatorPages = newArrayList();
      for (int pageIndex = pageNavigatorFrom; pageIndex <= pageNavigatorTo; pageIndex++) {
        navigatorPages.add(pageIndex);
      }

      showPreviousOmissionSymbol = pageNavigatorFrom > 2;
      showNextOmissionSymbol = pageNavigatorTo < totalPage - 1;
      showLastPage = pageNavigatorTo < totalPage;
    }
  }

  public void setContent(final List<?> content) {

    this.content = content;
    this.hasContent = isNotEmpty(content);
  }

  @Override
  public int hashCode() {

    int prime = 31;
    int result = 1;
    result = prime * result + ((currentPage == null) ? 0 : currentPage.hashCode());
    result = prime * result + ((pageName == null) ? 0 : pageName.hashCode());
    result = prime * result + ((totalPage == null) ? 0 : totalPage.hashCode());
    return result;
  }

  @Override
  public boolean equals(final Object obj) {

    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    FtngPage other = (FtngPage) obj;
    if (currentPage == null) {
      if (other.currentPage != null) {
        return false;
      }
    } else if (!currentPage.equals(other.currentPage)) {
      return false;
    }
    if (pageName == null) {
      if (other.pageName != null) {
        return false;
      }
    } else if (!pageName.equals(other.pageName)) {
      return false;
    }
    if (totalPage == null) {
      if (other.totalPage != null) {
        return false;
      }
    } else if (!totalPage.equals(other.totalPage)) {
      return false;
    }
    return true;
  }

  public List<?> getContent() {

    return content;
  }

  public String getPageName() {

    return pageName;
  }

  public List<Object> getNavigatorPages() {

    return navigatorPages;
  }

  public Boolean getShowPreviousOmissionSymbol() {

    return showPreviousOmissionSymbol;
  }

  public Boolean getShowNextOmissionSymbol() {

    return showNextOmissionSymbol;
  }

  public Boolean getHasContent() {

    return hasContent;
  }

  public Boolean getHasPreviousPage() {

    return hasPreviousPage;
  }

  public Boolean getHasNextPage() {

    return hasNextPage;
  }

  public Integer getCurrentPage() {

    return currentPage;
  }

  public Integer getTotalPage() {

    return totalPage;
  }

  public Long getTotalElements() {

    return totalElements;
  }

  public Boolean getShowLastPage() {

    return showLastPage;
  }

}
