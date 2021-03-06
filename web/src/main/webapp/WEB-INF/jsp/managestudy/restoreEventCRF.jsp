<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/managestudy-header.jsp"/>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${resword}"/></b>

		<div class="sidebar_tab_content">		
       
         <fmt:message key="confirm_restore_of_this_CRF_to_event"  bundle="${resword}"/> <c:out value="${event.studyEventDefinition.name}"/> (<fmt:message key="date_started" bundle="${resword}"/>:<cc-fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>). <fmt:message key="all_data_associated_with_the_CRF_in_this_event"  bundle="${resword}"/>
       
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

<jsp:useBean scope="request" id="displayEventCRF" class="org.akaza.openclinica.bean.submit.DisplayEventCRFBean"/>
<jsp:useBean scope="request" id="event" class="org.akaza.openclinica.bean.managestudy.StudyEventBean"/>
<jsp:useBean scope="request" id="items" class="java.util.ArrayList"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>

<h1>
	<span class="first_level_header">
		<fmt:message key="restore_CRF_to_event"  bundle="${resword}"/>
	</span>
</h1>
<p><fmt:message key="confirm_restore_of_this_CRF_to_event"  bundle="${resword}"/> <c:out value="${event.studyEventDefinition.name}"/> (<fmt:message key="date_started" bundle="${resword}"/>:<cc-fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>). <fmt:message key="all_data_associated_with_the_CRF_in_this_event"  bundle="${resword}"/></p>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="event_definition_name" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.studyEventDefinition.name}"/></td></tr>
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="location" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.location}"/></td></tr>
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="visit" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.sampleOrdinal}"/></td></tr>
    
  <tr valign="top">
	  <td class="table_header_column_top">
		  <fmt:message key="date_started" bundle="${resword}"/>:
	  </td>
	  <td class="table_cell">
		  <cc-fmt:formatDate value="${event.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  </td>
  </tr>
  <tr valign="top">
	  <td class="table_header_column_top">
		  <fmt:message key="date_ended" bundle="${resword}"/>:
	  </td>
	  <td class="table_cell">
		  <cc-fmt:formatDate value="${event.dateEnded}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  </td>
  </tr>
  <tr valign="top"><td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell"><c:out value="${event.status.name}"/>
  </td></tr>

 </table>
 </div>
</div></div></div></div></div></div></div></div>

</div>
<br>
<span class="title_manage"><fmt:message key="event_CRF" bundle="${resword}"/></span>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
    <tr>
		<td class="table_header_column_top"><fmt:message key="CRF_name" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="version" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="date_interviewed" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="interviewer_name" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="owner" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>	
	 </tr> 
	<tr>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crf.name}" /></td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.crfVersion.name}" /></td>
		<td class="table_cell">
			<cc-fmt:formatDate value="${displayEventCRF.eventCRF.dateInterviewed}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
			&nbsp;
		</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.interviewerName}"/>&nbsp;</td>
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.owner.name}" /></td>	
		<td class="table_cell"><c:out value="${displayEventCRF.eventCRF.status.name}" /></td>	
	 </tr>
 
 </table>
 
</div>
</div></div></div></div></div></div></div></div>

</div>
 <c:if test="${!empty items}">
 <span class="table_title_manage"><fmt:message key="item_data" bundle="${resword}"/></span>
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%"> 
    <tr>
		<td class="table_header_column_top"><fmt:message key="Id" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="value" bundle="${resword}"/></td>
		<td class="table_header_column_top"><fmt:message key="status" bundle="${resword}"/></td>	
	 </tr>
	
  <c:forEach var="item" items="${items}"> 
	<tr>
		<td class="table_cell"><c:out value="${item.itemId}" /></td>
		<td class="table_cell"><c:out value="${item.value}" />&nbsp;</td>		
		<td class="table_cell"><c:out value="${item.status.name}" /></td>			
	
	 </tr>
	</c:forEach> 
 
 </table>

</div>
</div></div></div></div></div></div></div></div>

</div>
<br>
 </c:if>
 <c:choose>
    <c:when test="${!empty items}">
     <form action='RestoreEventCRF?action=submit&id=<c:out value="${displayEventCRF.eventCRF.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
      <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
      <input type="submit" name="BTN_Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick='return confirmSubmit({ message: "<fmt:message key="this_crf_has_data_restore" bundle="${resword}"/>", height: 150, width: 500, submit: this });'>
     </form>
    
    </c:when>
    <c:otherwise>
    	<form action='RestoreEventCRF?action=submit&id=<c:out value="${displayEventCRF.eventCRF.id}"/>&studySubId=<c:out value="${studySub.id}"/>' method="POST">
		<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium medium_back"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
     	<input type="submit" name="BTN_Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit" onClick='return confirmSubmit({ message: "<fmt:message key="are_you_sure_you_want_to_restore_it" bundle="${resword}"/>", height: 150, width: 500, submit: this });'>
     </form>    
    </c:otherwise>
   </c:choose>  

<c:import url="../include/workflow.jsp">
  <c:param name="module" value="manage"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
