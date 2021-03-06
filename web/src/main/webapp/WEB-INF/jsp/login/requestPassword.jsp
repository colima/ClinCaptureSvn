<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:useBean scope='request' id='userBean1' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='challengeQuestions' class='java.util.ArrayList'/>
<jsp:include page="../login-include/login-header.jsp"/>

<jsp:include page="../login-include/request-sidebar.jsp"/>
<!-- Main Content Area -->
<h1><fmt:message key="request_password_form" bundle="${resword}"/></h1>
<jsp:include page="../login-include/login-alertbox.jsp"/>
<p><fmt:message key="you_must_be_an_openClinica_member_to_receive_a_password" bundle="${resword}"/></p>

<form action="RequestPassword" method="post">
	<fmt:message key="all_fields_are_required" bundle="${resword}"/><br>
	<input type="hidden" name="action" value="confirm">

	<div style="width: 600px">
		<div class="textbox_center table_shadow_bottom">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td class="formlabel">
						<fmt:message key="user_name"  bundle="${resword}"/>:
					</td>
					<td>
						<div class="formfieldXL_BG">
							<input type="text" name="name" value="${userBean1.name}" class="formfieldXL">
						</div>
						<jsp:include page="../showMessage.jsp">
							<jsp:param name="key" value="name"/>
						</jsp:include>
					</td>
				</tr>
				<tr valign="top">
					<td class="formlabel"><fmt:message key="email" bundle="${resword}"/>:</td>
					<td>
						<div class="formfieldXL_BG">
							<input type="text" name="email" value="${userBean1.email}" class="formfieldXL">
						</div>
						<jsp:include page="../showMessage.jsp">
							<jsp:param name="key" value="email"/>
						</jsp:include>
					</td>
				</tr>
				<tr valign="top">
					<td class="formlabel">
						<fmt:message key="password_challenge_question" bundle="${resword}"/>:
					</td>
					<td>
						<div class="formfieldXL_BG">
							<select name="passwdChallengeQuestion" class="formfieldXL">
								<c:forEach var="question" items="${challengeQuestions}">
									<option value="${question.name}"
											<c:if test="${previouslySelectedPasswdChallengeQuestion ne null and previouslySelectedPasswdChallengeQuestion eq question.name}">selected</c:if>>${question.name}</option>
								</c:forEach>
							</select>
						</div>
					</td>
				</tr>
				<tr valign="top">
					<td class="formlabel"><fmt:message key="password_challenge_answer" bundle="${resword}"/>:
					</td>
					<td>
						<div class="formfieldXL_BG"><input type="text" name="passwdChallengeAnswer"
														   value="<c:out value="${userBean1.passwdChallengeAnswer}"/>"
														   class="formfieldXL"></div>
						<jsp:include page="../showMessage.jsp">
							<jsp:param name="key" value="passwdChallengeAnswer"/>
						</jsp:include>
					</td>
				</tr>
			</table>
		</div>

	</div>
	<table border="0" cellpadding="0">
		<tr>
			<td>
				<input type="submit" name="Submit"
					   value="<fmt:message key="submit_password_request" bundle="${resword}"/>" class="button_xlong">
			</td>
			<td><input type="button" name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>"
					   class="button_medium medium_cancel" onclick="javascript:window.location.href='MainMenu'"></td>
		</tr>
	</table>
</form>

</td>
</tr>
</table>
</td>
</tr>
</table>
<jsp:include page="../login-include/login-footer.jsp"/>
</body>
</html>
