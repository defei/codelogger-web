<%@ taglib uri="http://java.codelogger.org/jstl/core" prefix="ftng" %>
<%@ attribute name="navClass" required="false" type="java.lang.String" %>
<nav class='navbar <ftng:getValue defaultValue="navbar-default" value="${navClass}" />'>
	<div class="container-fluid">
		<jsp:doBody/>
	</div>
</nav>