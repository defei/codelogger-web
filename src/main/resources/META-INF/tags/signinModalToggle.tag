<%@ taglib uri="http://java.codelogger.org/jstl/core" prefix="ftng" %>
<%@ attribute name="label" required="false" type="java.lang.String" %>

<ftng:modalToggle modalId="loginModal" styleClass="btn-link">
	<span class="glyphicon glyphicon-log-in"></span>
    <span>
	    <ftng:getValue defaultValue="Sign in" value="${label}"/>
    </span>
</ftng:modalToggle>