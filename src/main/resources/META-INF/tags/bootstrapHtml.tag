<!DOCTYPE html>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<%@ attribute name="css" fragment="true" %>
<%@ attribute name="script" fragment="true" %>
<html>
<head>
	<title>${title}</title>
	<meta name="viewport" content="width=device-width,initial-scale=1,user-scalable=yes"/>
	<base href="${pageContext.request.contextPath}/">
	<link rel="icon" href="favicon.ico"/>
	<link rel="stylesheet"
		  href="http://cdn.staticfile.org/twitter-bootstrap/3.2.0/css/bootstrap.min.css">
	<jsp:invoke fragment="css"/>
</head>
<body>
<jsp:doBody/>
</body>
<script src="http://cdn.staticfile.org/jquery/1.11.1/jquery.min.js"></script>
<script src="http://cdn.staticfile.org/twitter-bootstrap/3.2.0/js/bootstrap.min.js"></script>
<jsp:invoke fragment="script"/>
</html>