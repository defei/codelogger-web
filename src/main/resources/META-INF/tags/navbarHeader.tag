<%@ taglib uri="http://java.ftng.net/jstl/core" prefix="ftng" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="href" required="false" type="java.lang.String" %>
<div class="navbar-header">
	<a class="navbar-brand" href="<ftng:getValue defaultValue='#' value='${href}' />">${title}</a>
</div>
