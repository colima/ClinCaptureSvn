<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/admin-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

        <fmt:message key="confirm_unlocking_of_this_version"  bundle="${resword}"/>
        <fmt:message key="this_version_will_available_when_creating_event"  bundle="${resword}"/>

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_unlocking_crf_version"  bundle="${resword}"/>
	</span>
</h1>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="CRF_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${crf.name}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="CRF_version" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${crfVersionToUnlock.name}"/>
  </td></tr>

 <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${crfVersionToUnlock.description}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="revision_notes" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${crfVersionToUnlock.revisionNotes}"/>
   </td></tr>

  </table>
 </div>
</div></div></div></div></div></div></div></div>
 </div>
<br>
<c:choose>
<c:when test="${!empty eventSubjectsUsingVersion}">
<fmt:message key="subjects_events_using_this_version"  bundle="${resword}"/>:
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
<tr valign="top" bgcolor="#EBEBEB">
   <td class="table_header_row_left"><fmt:message key="study_subject_label" bundle="${resword}"/></td>

   <td class="table_header_row"><fmt:message key="study_name" bundle="${resword}"/></td>

   <td class="table_header_row"><fmt:message key="SE" bundle="${resword}"/></td>

  </tr>
<c:forEach var="studySubjectEvent" items="${eventSubjectsUsingVersion}">
<tr>
<td class="table_cell_left"><c:out value="${studySubjectEvent.studySubjectName}"/></td>
<td class="table_cell"><c:out value="${studySubjectEvent.studyName}"/></td>
<td class="table_cell"><c:out value="${studySubjectEvent.eventName}"/></td>
</tr>
</c:forEach>
</table>
 </div>
</div></div></div></div></div></div></div></div>
 </div>

</c:when>
<c:otherwise>
 <fmt:message key="currently_no_subject_using_this_version"  bundle="${resword}"/>.
</c:otherwise>
</c:choose>
</br>
<br>
<table border="0" cellpadding="0" cellspacing="0" width="300">
<tr>
<td>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
</td>
<td>
<form action='UnlockCRFVersion?action=confirm&id=<c:out value="${crfVersionToUnlock.id}"/>' method="POST">
<input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit">
</form>
</td>

</tr>
</table>


<jsp:include page="../include/footer.jsp"/>
