<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<html>
<head>
    <title><fmt:message key="openclinica_print_study_events" bundle="${resword}"/></title>
    <link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
    <link rel="icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <ui:theme/>
</head>
<body onload="javascript:alert('<fmt:message key="alert_to_print" bundle="${restext}"/>')">

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "definitionId"}'>
		<c:set var="definitionId" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "statusId"}'>
		<c:set var="statusId" value="${presetValue.value}" />
	</c:if>

</c:forEach>
<!-- the object inside the array is StudySubjectBean-->

<h1>
	<div class="first_level_header">
		<fmt:message key="view_all_events_in" bundle="${resword}"/><c:out value="${study.name}"/>
	</div>
</h1>

<jsp:include page="../include/alertbox.jsp"/>

<p><fmt:message key="events_month_shown_default" bundle="${restext}"/></p>
<p><fmt:message key="subject_scheduled_no_DE_yellow" bundle="${restext}"/></p>
 <c:forEach var="eventView" items="${allEvents}">
      <c:choose>
        <c:when test="${userRole.manageStudy}">
         <span class="table_title_manage">
        </c:when>
        <c:otherwise>
          <span class="table_title_submit">
        </c:otherwise>
      </c:choose>
 <fmt:message key="event_name" bundle="${resword}"/>: <c:out value="${eventView.definition.name}"/></span>
	<p><b><fmt:message key="event_type" bundle="${resword}"/></b>:<c:out value="${eventView.definition.type}"/>,
	<c:choose>
     <c:when test="${eventView.definition.repeating}">
      <fmt:message key="repeating" bundle="${resword}"/>
     </c:when>
     <c:otherwise>
      <fmt:message key="non_repeating" bundle="${resword}"/>
     </c:otherwise>
    </c:choose>,
	<b><fmt:message key="category" bundle="${resword}"/></b>:
	<c:choose>
	 <c:when test="${eventView.definition.category == null || eventView.definition.category ==''}">
	  <fmt:message key="na" bundle="${resword}"/>
	 </c:when>
	 <c:otherwise>
	   <c:out value="${eventView.definition.category}"/>
	 </c:otherwise>
	</c:choose>
	<br>
	<b><fmt:message key="subjects_who_scheduled" bundle="${resword}"/></b>: <c:out value="${eventView.subjectScheduled}"/> (<fmt:message key="start_date_of_first_event" bundle="${resword}"/>:
	<c:choose>
      <c:when test="${eventView.firstScheduledStartDate== null}">
       <fmt:message key="na" bundle="${resword}"/>
      </c:when>
     <c:otherwise>
       <cc-fmt:formatDate value="${eventView.firstScheduledStartDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
     </c:otherwise>
     </c:choose>), <b><fmt:message key="completed" bundle="${resword}"/></b>: <c:out value="${eventView.subjectCompleted}"/> (<fmt:message key="completion_date_of_last_event" bundle="${resword}"/>:
   <c:choose>
   <c:when test="${eventView.lastCompletionDate== null}">
    <fmt:message key="na" bundle="${resword}"/>
   </c:when>
   <c:otherwise>
    <cc-fmt:formatDate value="${eventView.lastCompletionDate}" dateTimeZone="${userBean.userTimeZoneId}"/>
   </c:otherwise>
 </c:choose>), <b><fmt:message key="discontinued" bundle="${resword}"/></b>: <c:out value="${eventView.subjectDiscontinued}"/><br></p>
	<c:set var="events" value="${eventView.studyEvents}" scope="request" />
	<div style="width: 600px">
	 <!-- These DIVs define shaded box borders -->
  <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
  <div class="textbox_center">

   <table border="0" cellpadding="0" cellspacing="0" width="100%">
    <tr>
	 <td class="table_header_row"><fmt:message key="subject_unique_identifier" bundle="${resword}"/></td>
	 <td class="table_header_row"><fmt:message key="event_date_started" bundle="${resword}"/></td>
	 <td class="table_header_row"><fmt:message key="subject_event_status" bundle="${resword}"/></td>
	</tr>
	<c:forEach var="currRow" items="${events}">
	 <c:choose>
    <c:when test="${currRow.scheduledDatePast}">
      <tr valign="top" bgcolor="#FFFF80">
    </c:when>
    <c:otherwise>
    <tr valign="top">
   </c:otherwise>
   </c:choose>
      <td class="table_cell"><c:out value="${currRow.studySubjectLabel}"/></td>
      <td class="table_cell"><cc-fmt:formatDate value="${currRow.dateStarted}" dateTimeZone="${userBean.userTimeZoneId}"/></td>
      <td class="table_cell"><c:out value="${currRow.subjectEventStatus.name}"/></td>


   </tr>


	</c:forEach>
	</table>

</div>
</div></div></div></div></div></div></div></div>
</div>
<br><br>
</c:forEach>
</body>
<jsp:include page="../include/changeTheme.jsp"/>
</html>

