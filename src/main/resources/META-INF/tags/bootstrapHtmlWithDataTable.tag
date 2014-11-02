<%@ taglib prefix="ftng" uri="http://java.codelogger.org/jstl/core" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="css" fragment="true" %>
<%@ attribute name="script" fragment="true" %>
<ftng:bootstrapHtml title="${title}">
    <jsp:attribute name="css">
        <link rel="stylesheet"
			  href="http://cdn.staticfile.org/datatables/1.10.0/css/jquery.dataTables.min.css">
        <jsp:invoke fragment="css"/>
    </jsp:attribute>
    <jsp:attribute name="script">
        <ftng:script src="http://cdn.staticfile.org/datatables/1.10.0/js/jquery.dataTables.min.js"/>
        <ftng:script
				src="http://static.codelogger.org/resources/js/jquery.dataTables.bootStrap.min.js"/>
        <jsp:invoke fragment="script"/>
    </jsp:attribute>
	<jsp:body>
		<jsp:doBody/>
	</jsp:body>
</ftng:bootstrapHtml>