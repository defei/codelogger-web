<%@ taglib uri="http://java.ftng.net/jstl/core" prefix="ftng" %>
<%@ attribute name="id" required="true" type="java.lang.String" %>
<%@ attribute name="title" required="true" type="java.lang.String" %>
<div class="modal fade" id="${id}" tabindex="-1" role="dialog">
	<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header text-center">
				<button type="button" class="close" data-dismiss="modal"
						aria-hidden="true">&times;</button>
				<h4 class="modal-title">${title}</h4>
			</div>
			<div class="modal-body clearfix">
				<jsp:doBody/>
			</div>
		</div>
	</div>
</div>