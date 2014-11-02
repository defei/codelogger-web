<%@ taglib uri="http://java.codelogger.org/jstl/core" prefix="ftng" %>
<%@ attribute name="action" required="true" type="java.lang.String" %>
<div class="modal fade" id="loginModal" tabindex="-1" role="dialog">
	<div class="modal-dialog"
		 style="top:0;right:0;bottom:0;left:0;margin:auto;position:fixed;padding:0;height:340px;">
		<div class="modal-content">
			<div class="modal-body">
				<form role="form" action="signin" method='POST'>
					<div class="form-group">
						<label for="account">Account</label> <input type="text" name="account"
																	class="form-control"
																	id="account"
																	placeholder="Enter Account">
					</div>
					<div class="form-group">
						<label for="password">Password</label> <input type="password"
																	  name="password"
																	  class="form-control"
																	  id="password"
																	  placeholder="Enter Password">
					</div>
					<div class="form-group">
						<div class="checkbox">
							<label> <input type="checkbox"> Remember me
							</label>
						</div>
					</div>
					<div class="form-group">
						<button type="submit" name="submit" class="btn btn-default">Sign in</button>
						<div class="text-right pull-right">
							<a class="btn btn-link" href="http://security.codelogger.org/signup"
							   target="_blank">Sign up?</a>
						</div>
					</div>
				</form>
			</div>
		</div>
	</div>
</div>