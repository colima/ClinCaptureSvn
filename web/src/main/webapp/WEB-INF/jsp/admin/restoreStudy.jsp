<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<jsp:include page="../include/admin-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">

		</div>

		</td>

	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<jsp:useBean scope='request' id='studyToRestore' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='sitesToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='userRolesToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='subjectsToRestore' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='definitionsToRestore' class='java.util.ArrayList'/>

<h1>
	<span class="first_level_header">
		<fmt:message key="confirm_restore_of_study" bundle="${resword}"/>
	</span>
</h1>

<p>
<fmt:message key="you_choose_to_restore_the_following_study" bundle="${restext}"/>:
</p>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToRestore.name}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${studyToRestore.summary}"/>
  </td></tr>
 </table>

 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <span class="table_title_Admin"><fmt:message key="sites" bundle="${resword}"/>:</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
 <tr valign="top">
  <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>
  <td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>
  </tr>
  <c:forEach var="site" items="${sitesToRestore}">
  <tr valign="top">
   <td class="table_cell"><c:out value="${site.name}"/></td>
   <td class="table_cell"><c:out value="${site.status.name}"/></td>
  </tr>
  </c:forEach>
  </table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
  <br>
<span class="table_title_Admin"><fmt:message key="user_and_roles" bundle="${resword}"/>:</span>
<div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
    <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>
   <td class="table_header_column_top"><fmt:message key="role" bundle="${resword}"/></td>
  </tr>
  <c:forEach var="userRole" items="${userRolesToRestore}">
  <tr valign="top">
   <td class="table_cell">
    <c:out value="${userRole.userName}"/>
   </td>
   <td>
    <c:out value="${userRole.role.name}"/>
   </td>
  </tr>
  </c:forEach>
  </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
  <br>
 <span class="table_title_Admin"><fmt:message key="subjects" bundle="${resword}"/>:</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
  <c:if test="${study ne null}">
      <c:set var="studySubjectIdLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
  </c:if>
  <tr valign="top"><td class="table_header_column_top">${studySubjectIdLabel}</td>

   <td class="table_header_column_top"><fmt:message key="record_ID" bundle="${resword}"/></td>
  </tr>
  <c:forEach var="subject" items="${subjectsToRestore}">
   <tr>
   <td class="table_cell">
    <c:out value="${subject.label}"/>
   </td>
   <td>
    <c:out value="${subject.id}"/>
   </td>
  </tr>
  </c:forEach>
  </table>
</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 <span class="table_title_Admin"><fmt:message key="SED" bundle="${resword}"/>:</span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top">
   <td class="table_header_column_top"><fmt:message key="name" bundle="${resword}"/></td>
   <td class="table_header_column_top"><fmt:message key="description" bundle="${resword}"/></td>
  </tr>
  <c:forEach var="definition" items="${definitionsToRestore}">
   <tr>
   <td class="table_cell">
    <c:out value="${definition.name}"/>
   </td>
  <td class="table_cell">
    <c:out value="${definition.description}"/>
   </td>
  </tr>
  </c:forEach>
  </table>

</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<form action='RestoreStudy?action=submit&id=<c:out value="${studyToRestore.id}"/>' method="POST">
 <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
 <input type="submit" name="submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_long long_submit" onClick='return confirmSubmit({ message: "<fmt:message key="are_you_sure_you_want_to_restore_this_study" bundle="${restext}"/>", height: 150, width: 500, submit: this });'>
</form>

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="admin"/>
</c:import>

<jsp:include page="../include/footer.jsp"/>
