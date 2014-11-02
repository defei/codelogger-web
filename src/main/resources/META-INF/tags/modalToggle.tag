<%@ taglib uri="http://java.ftng.net/jstl/core" prefix="ftng" %>
<%@ attribute name="modalId" required="true" type="java.lang.String" %>
<%@ attribute name="styleClass" required="false" type="java.lang.String" %>
<a class="${styleClass}" data-toggle="modal" data-target="#${modalId}">
	<jsp:doBody/>
</a>