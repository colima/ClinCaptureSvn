<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<jsp:useBean scope="session" id="userRole" class="org.akaza.openclinica.bean.login.StudyUserRoleBean" />
<jsp:useBean scope='request' id='eventId' class='java.lang.String'/>
<c:set var="eventId" value="${eventId}"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<c:set var="showDDEColumn" value="false"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/submit-header.jsp"/>

<!-- *JSP* submit/enterDataForStudyEvent.jsp -->
<script type="text/javascript" language="javascript">
    function checkCRFLocked(ecId, url){
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                window.location = url;
            }else{
            	alertDialog({ message:data, height: 150, width: 500 });
            }
        });
    }
    function checkCRFLockedInitial(ecId, formName){
        if(ecId==0) {formName.submit(); return;} 
        jQuery.post("CheckCRFLocked?ecId="+ ecId + "&ran="+Math.random(), function(data){
            if(data == 'true'){
                formName.submit();
            }else{
            	alertDialog({ message:data, height: 150, width: 500 });
            }
        });
    }
</script>


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
<jsp:include page="../include/eventOverviewSideInfo.jsp"/>

<jsp:useBean scope="request" id="studyEvent" class="org.akaza.openclinica.bean.managestudy.StudyEventBean" />
<jsp:useBean scope="request" id="studySubject" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean" />
<jsp:useBean scope="request" id="uncompletedEventDefinitionCRFs" class="java.util.ArrayList" />
<jsp:useBean scope="request" id="displayEventCRFs" class="java.util.ArrayList" />

<h1>
	<span class="first_level_header">
		<fmt:message key="view_event" bundle="${resword}"/>: <c:out value="${studyEvent.studyEventDefinition.name}" />
		<fmt:message key="for_subject" bundle="${resword}"/> <c:out value="${studySubject.label}"/> 
		<a href="javascript:openDocWindow('help/2_2_enrollSubject_Help.html#step2a')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a> 
	</span>
</h1>

<a name="global"><a href="javascript:leftnavExpand('globalRecord');javascript:setImage('ExpandGroup5','images/bt_Collapse.gif');"><img
  name="ExpandGroup5" src="images/bt_Expand.gif" border="0"></a></a></div>

<div id="globalRecord">
<div style="width: 350px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectLabel"/>
<c:if test="${study ne null}">
    <c:set var="studySubjectLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<div class="tablebox_center">

    <table border="0" cellpadding="0" cellspacing="0" width="335">


        <tr>
            <td valign="top">

                <!-- Table Contents -->

                <table border="0" cellpadding="0" cellspacing="0" width="100%">
                    <tr>
                       <td class="table_header_column_top">${studySubjectLabel}</td>
                       <td class="table_cell_top"><c:out value="${studySubject.label}"/>
                        <span style="float:right">
                         <a href="ViewStudySubject?id=<c:out value="${studySubject.id}"/>"
                   onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
                   onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img
                   name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>"></a>

                        </span>
                       </td>
                    </tr>
                    <tr>
                        <td class="table_header_column"><fmt:message key="SE" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studyEvent.studyEventDefinition.name}"/>&nbsp;</td>
                    </tr>
                    <c:if test="${study.studyParameterConfig.eventLocationRequired != 'not_used'}">
                      <tr>
                        <td class="table_header_column"><fmt:message key="location" bundle="${resword}"/></td>
                        <td class="table_cell">
                            <c:set var="eventLocation" value="${studyEvent.location}"/>
                            <c:if test="${studyEvent.location eq ''}">
                                <c:set var="eventLocation" value="N/A"/>
                            </c:if>
                            <span style="float:left">
                                <c:out value="${eventLocation}"/>
                            </span>
                            <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
                              <c:set var="imageFileName" value="${imageFileNameForLocation}"/>
                              <c:choose>
                                <c:when test="${numberOfLocationDNotes > 0}">
                                  <span style="float:right">
                                    <a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg','spanAlert-location', '', event); return false;">
                                      <img id="flag_location" name="flag_location"
                                        src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                                    </a>
                                  </span>
                                </c:when>
								<c:when test="${userRole.role.id eq 10}">
									<span style="float:right">
										<img id="flag_location" name="flag_location"
                                        src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
									</span>
								</c:when>
                                <c:otherwise>
                                  <span style="float:right">
                                    <a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg=','spanAlert-location', '', event); return false;">
                                      <img id="flag_location" name="flag_location"
                                        src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
                                      <input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=location&column=location&strErrMsg"/>
                                    </a>
                                  </span>
                                </c:otherwise>
                              </c:choose>
                            </c:if>
						            </td>
                      </tr>
                    </c:if>
          
                    <tr>
                        <td class="table_divider" colspan="2">&nbsp;</td>
                    </tr>
                    <c:if test="${isStartDateUsed eq true}">
                    <tr>
                        <td class="table_header_column">${startDateLabel}</td>
                        <td class="table_cell"><span style="float:left"><cc-fmt:formatDate value="${studyEvent.dateStarted}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/></span>
                        <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
							<c:set var="imageFileName" value="${imageFileNameForDateStart}"/>
							<c:choose>
								<c:when test="${numberOfDateStartDNotes > 0}">
									<span style="float:right">
										<a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=date_start&column=date_start&strErrMsg','spanAlert-date_start', '', event); return false;">
											<img id="flag_date_start" name="flag_date_start" 
												src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
										</a>
									</span>
								</c:when>
								<c:when test="${userRole.role.id eq 10}">
									<span style="float:right">
										<img id="flag_date_start" name="flag_date_start" 
												src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
									</span>
								</c:when>
								<c:otherwise>
									<span style="float:right">
										<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=date_start&column=date_start&strErrMsg=','spanAlert-date_start', '', event); return false;">
											<img id="flag_date_start" name="flag_date_start" 
												src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
											<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=date_start&column=date_start&strErrMsg"/>
										</a>
									</span>
								</c:otherwise>
							</c:choose>	
                        </c:if>
                        </td>
                    </tr>
                    </c:if>
                    <c:if test="${isEndDateUsed eq true}">
                    <tr>
                        <td class="table_header_column">${endDateLabel}</td>
                        <td class="table_cell"><span style="float:left"><cc-fmt:formatDate value="${studyEvent.dateEnded}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/></span>
                            <c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
							<c:set var="imageFileName" value="${imageFileNameForDateEnd}"/>
							<c:choose>
								<c:when test="${numberOfDateEndDNotes > 0}">
									<span style="float:right">
										<a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=date_end&column=date_end&strErrMsg','spanAlert-date_end', '', event); return false;">
											<img id="flag_date_end" name="flag_date_end" 
												src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
										</a>
									</span>
								</c:when>
								<c:when test="${userRole.role.id eq 10}">
									<span style="float:right">
										<img id="flag_date_end" name="flag_date_end" 
												src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
									</span>
								</c:when>
								<c:otherwise>
									<span style="float:right">
										<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=date_end&column=date_end&strErrMsg=','spanAlert-date_end', '', event); return false;">
											<img id="flag_date_end" name="flag_date_end" 
												src="images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"/>
											<input type="hidden" value="ViewDiscrepancyNote?writeToDB=1&id=${studyEvent.id}&stSubjectId=${studySubject.id}&name=studyEvent&field=date_end&column=date_end&strErrMsg"/>
										</a>
									</span>
								</c:otherwise>
							</c:choose>	
                            </c:if>
                        </td>
                    </tr>
                    </c:if>
                    <tr>
                        <td class="table_header_column"><fmt:message key="subject_event_status" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studyEvent.subjectEventStatus.name}"/></td>
                    </tr>
                    <tr>
                        <td class="table_header_column"><fmt:message key="last_updated_by" bundle="${resword}"/></td>
                        <td class="table_cell"><c:out value="${studyEvent.updater.name}"/> (<cc-fmt:formatDate value="${studyEvent.updatedDate}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>)</td>
                    </tr>

                </table>

                <!-- End Table Contents -->

            </td>
        </tr>
    </table>
</div>

</div></div></div></div></div></div></div></div>
</div>

</div>

<p><div class="title_manage"><fmt:message key="CRFs_in_this_study_event" bundle="${resword}"/>:</div>

<div style="width: 650px">
<!-- These DIVs define shaded box borders -->
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
<div class="tablebox_center">
<!-- Table Contents -->

<table border="0" cellpadding="0" cellspacing="0" width="100%">
<c:choose>
<c:when test="${empty uncompletedEventDefinitionCRFs && empty displayEventCRFs}">
    <tr>
        <td class="table_cell_left"><fmt:message key="there_are_no_CRF" bundle="${resword}"/></td>
    </tr>
</c:when>

<c:otherwise>
<tr>
    <td align="center" class="table_header_row_left"><fmt:message key="CRF_name" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="version" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="initial_data_entry" bundle="${resword}"/></td>
    <td align="center" class="table_header_row ddeColumnHeader"><fmt:message key="validation" bundle="${resword}"/></td>
    <td align="center" class="table_header_row"><fmt:message key="actions" bundle="${resword}"/></td>
</tr>
<c:set var="rowCount" value="${0}" />

<c:forEach var="dedc" items="${fullCrfList}">
<c:choose>
<c:when test="${dedc['class'].name eq 'org.akaza.openclinica.bean.managestudy.DisplayEventDefinitionCRFBean'}">

<c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${studyEvent.id}&subjectId=${studySubject.subjectId}&eventCRFId=${dedc.eventCRF.id}&exitTo=EnterDataForStudyEvent?eventId=${eventId}" />
<form name="startForm${studyEvent.id}${dedc.edc.crf.id}" action="InitialDataEntry?<c:out value="${getQuery}"/>" method="POST">
<tr valign="top">
	<td class="table_cell_left">
		<c:out value="${dedc.edc.crf.name}" /> 
		<c:if test="${dedc.edc.requiredCRF}">
			<span style="color: orange">*</span>
		</c:if> 
		<c:if test="${(dedc.edc.sourceDataVerification.code eq 1 or dedc.edc.sourceDataVerification.code eq 2) and (userRole.role.id eq 1 or userRole.role.id eq 2 or userRole.role.id eq 6 or userRole.role.id eq 9)}">
			<img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/>
		</c:if>
	</td>
	<td class="table_cell">
		<c:set var="versionCount" value="0"/>
		<c:set var="firstVersionId" value="0"/>
        <c:set var="versionOid" value="*"/>
		<c:forEach var="version" items="${dedc.edc.versions}">
			<c:if test="${versionCount == 0}">
				<c:set var="firstVersionId" value="${version.id}"/>
                <c:set var="versionOid" value="${version.oid}"/>
			</c:if>
			<c:set var="versionCount" value="${versionCount+1}"/>
        </c:forEach>

    <c:set var="crfVersionInputId" value="crfVersionId${firstVersionId}"/>
		<c:choose>
			<c:when test="${dedc.eventCRF.notStarted && dedc.eventCRF.id == 0}">
				<input type="hidden" id="crfVersionId${firstVersionId}" name="crfVersionId" value="<c:out value="${firstVersionId}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${firstVersionId}"/>
			</c:when>
			<c:when test="${versionCount > 1 && dedc.eventCRF.notStarted && dedc.eventCRF.id > 0}">
				<input type="hidden" id="crfVersionId${dedc.eventCRF.crfVersion.id}" name="crfVersionId" value="<c:out value="${dedc.eventCRF.crfVersion.id}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${dedc.eventCRF.crfVersion.id}"/>
			</c:when>
			<c:when test="${versionCount == 1 && dedc.eventCRF.notStarted && dedc.eventCRF.id > 0}">
				<input type="hidden" id="crfVersionId${firstVersionId}" name="crfVersionId" value="<c:out value="${firstVersionId}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${firstVersionId}"/>
			</c:when>
			<c:otherwise>
				<input type="hidden" id="crfVersionId${defaultVersionId}" name="crfVersionId" value="<c:out value="${defaultVersionId}"/>">
        <c:set var="crfVersionInputId" value="crfVersionId${defaultVersionId}"/>
			</c:otherwise>
		</c:choose>
        
        <c:choose>
        <c:when test="${versionCount<=1}">
          <c:choose>
            <c:when test="${dedc.status.locked}">
              ${dedc.eventCRF.crfVersion.name}
              <script>$('#${crfVersionInputId}').val('${dedc.eventCRF.crfVersion.id}')</script>
            </c:when>
            <c:otherwise>
              <c:forEach var="version" items="${dedc.edc.versions}">
                <c:out value="${version.name}"/>
              </c:forEach>
            </c:otherwise>
          </c:choose>
        </c:when>
        <c:when test="${dedc.eventCRF.notStarted || dedc.eventCRF.id == 0}">
        <select name="versionId" onchange="javascript:changeQuery${studyEvent.id}${dedc.edc.crf.id}();">
            <c:forEach var="version" items="${dedc.edc.versions}">
                <c:set var="getQuery" value="action=ide_s&eventDefinitionCRFId=${dedc.edc.id}&studyEventId=${currRow.bean.studyEvent.id}&subjectId=${studySub.subjectId}" />
                <c:choose>
                    <c:when test="${(dedc.edc.defaultVersionId == version.id && dedc.eventCRF.id == 0) || (dedc.eventCRF.CRFVersionId == version.id && dedc.eventCRF.notStarted)}">
                        <script>$('#${crfVersionInputId}').val('${version.id}');</script>
                        <option value="<c:out value="${version.id}"/>" selected>
                            <c:out value="${version.name}"/>
                        </option>
                    </c:when>
                    <c:otherwise>
                        <option value="<c:out value="${version.id}"/>">
                            <c:out value="${version.name}"/>
                        </option>
                    </c:otherwise>
                </c:choose>
            </c:forEach><%-- end versions --%>
        </select>
        <SCRIPT LANGUAGE="JavaScript">
            function changeQuery${studyEvent.id}${dedc.edc.crf.id}() {
                var qer = document.startForm${studyEvent.id}${dedc.edc.crf.id}.versionId.value;
                document.startForm${studyEvent.id}${dedc.edc.crf.id}.crfVersionId.value=qer;
            }
        </SCRIPT>
        </c:when>
        <c:otherwise>
            <c:out value="${dedc.eventCRF.crfVersion.name}"/>
        </c:otherwise>
        </c:choose>
</td>

    <td class="table_cell" bgcolor="#F5F5F5" align="center" style="vertical-align: middle;">
        <ui:displayEventCRFStatusIcon studySubject="${studySubject}" studyEvent="${studyEvent}"
                                      eventDefinitionCRF="${dedc.edc}" eventCrf="${dedc.eventCRF}"/>
    </td>

<td class="table_cell ddeColumn">
    <c:if test="${dedc.eventCRF != null && !dedc.eventCRF.notStarted && dedc.eventCRF.owner != null}">
        ${dedc.eventCRF.owner.name}
    </c:if>
    &nbsp;</td>

<td class="table_cell">&nbsp;</td>

<td class="table_cell" style="vertical-align: middle; width:200px;">
            <c:choose>

                <c:when test="${dedc.status.locked || studyEvent.subjectEventStatus.locked || studyEvent.subjectEventStatus.skipped  || studyEvent.subjectEventStatus.stopped}">
                    <%--<c:when test="${dedc.status.name=='locked'}">--%>
					<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4"/>
                </c:when>

                <c:when test="${!studySubject.status.deleted && study.status.available && !studyEvent.status.deleted && userRole.role.id ne 6 and userRole.role.id ne 9 and userRole.id ne 10}">
                    <ui:dataEntryLink object="${dedc}" rowCount="${rowCount}" actionQueryTail="${studyEvent.id}${dedc.edc.crf.id}" onClickFunction="checkCRFLockedInitial"/>
                </c:when>

                <c:otherwise>
					<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
				</c:otherwise>
            </c:choose>
				<ui:viewDataEntryLink object="${dedc}" onClick="viewCrfByVersion('${dedc.edc.id}', '${studySubject.id}', $('#${crfVersionInputId}').val(), '${eventId}', 1);"/>
				<ui:printEventCRFLink crfVersionOid="${versionOid}" dedc="${dedc}" hspace="4"/>
</td>

</tr>
</form>

<c:set var="rowCount" value="${rowCount + 1}" />

<!-- end of for each for dedc, uncompleted event crfs, started CRFs below -->
</c:when>
<c:when test="${dedc['class'].name eq 'org.akaza.openclinica.bean.submit.DisplayEventCRFBean'}">
<c:set var="dec" value="${dedc}"/>

<tr>
<td class="table_cell"><c:out value="${dec.eventCRF.crf.name}" /> <c:if test="${dec.eventDefinitionCRF.requiredCRF}"><span style="color: orange">*</span></c:if> <c:if test="${(dec.eventDefinitionCRF.sourceDataVerification.code eq 1 or dec.eventDefinitionCRF.sourceDataVerification.code eq 2) and (userRole.role.id eq 1 or userRole.role.id eq 2 or userRole.role.id eq 6 or userRole.role.id eq 9)}"><img src="images/sdv.png" style="border: none; margin: 0px; padding: 0px;"/></c:if></td>
<td class="table_cell"><c:out value="${dec.eventCRF.crfVersion.name}" />&nbsp;</td>
<td class="table_cell" bgcolor="#F5F5F5" align="center">
	<ui:displayEventCRFStatusIcon studySubject="${studySubject}" studyEvent="${studyEvent}"
								  eventDefinitionCRF="${dec.eventDefinitionCRF}" eventCrf="${dec.eventCRF}"/>
</td>
<td class="table_cell"><c:out value="${dec.eventCRF.owner.name}" />&nbsp;</td>
<td class="table_cell ddeColumn">
    <c:choose>
        <c:when test="${!dec.eventDefinitionCRF.doubleEntry && !dec.eventDefinitionCRF.evaluatedCRF}">
            n/a
        </c:when>
        <c:otherwise>
            <c:set var="showDDEColumn" value="true"/>
            <c:choose>
                <c:when test="${dec.stage.doubleDE || dec.stage.doubleDE_Complete || dec.stage.admin_Editing || dec.stage.locked}">
                    <c:out value="${dec.eventCRF.doubleDataOwner.name}" />&nbsp;
                </c:when>
                <c:otherwise>
                    &nbsp;
                </c:otherwise>
            </c:choose>
        </c:otherwise>
    </c:choose>
</td>
<td class="table_cell" style="width:215px;text-align:center;">
    <div class="<c:if test='${dec.eventDefinitionCRF.hideCrf and study.parentStudyId > 0}'>hidden</c:if>">

    <c:set var="allowDataEntry" value="${(study.status.available && dec.continueInitialDataEntryPermitted) || (study.status.available && (dec.startDoubleDataEntryPermitted || dec.continueDoubleDataEntryPermitted)) || ((study.status.available || study.status.frozen) && dec.performAdministrativeEditingPermitted)}" />

    <c:choose>
        <c:when test='${!allowDataEntry && dec.stage.name == "invalid" }'>
			<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
			<ui:viewDataEntryLink object="${dec}" queryTail="&eventId=${eventId}" name="bt_View${rowCount}"/>

			<ui:printEventCRFLink studyOid="${parentStudyOid}" subjectOid="${studySubject.oid}" studyEvent="${studyEvent}" dec="${dec}" hspace="4"/>

            <c:if test="${userRole.id ne 4 and userRole.id ne 5 and userRole.id ne 6 and userRole.role.id ne 9 and userRole.id ne 10 and (!studySubject.status.deleted) && (study.status.available)}">
                <ui:restoreEventCRFLink object="${dec}" subjectId="${studySubject.id}"/>
				<ui:deleteEventCRFLink object="${dec}" subjectId="${studySubject.id}" hspace="4"/>
            </c:if>

        </c:when>

        <c:when test='${!allowDataEntry}'>
			<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
			<ui:viewDataEntryLink object="${dec}" queryTail="&eventId=${eventId}"/>
			<ui:printEventCRFLink studyOid="${parentStudyOid}" subjectOid="${studySubject.oid}" studyEvent="${studyEvent}" dec="${dec}" hspace="4"/>
        </c:when>
        <c:otherwise>
			<c:choose>
            <c:when test="${!studySubject.status.deleted && userRole.role.id ne 6 and userRole.role.id ne 9 and userRole.id ne 10}">
                <ui:dataEntryLink object="${dec}" actionQueryTail="?eventCRFId=${dec.eventCRF.id}"/>
                <c:if test="${dec.locked || dec.eventCRF.status.locked || dec.stage.locked || currRow.bean.studyEvent.subjectEventStatus.locked}">
				    <img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
                </c:if>
            </c:when>
			<c:otherwise>
				<img name="itemForSpace" src="images/bt_EnterData.gif" border="0" style="visibility:hidden"  align="left" hspace="4">
			</c:otherwise>
			</c:choose>

			<ui:viewDataEntryLink object="${dec}" queryTail="&eventId=${eventId}" name="bt_View${rowCount}"/>
			<ui:printEventCRFLink studyOid="${parentStudyOid}" subjectOid="${studySubject.oid}" studyEvent="${studyEvent}" dec="${dec}" hspace="4"/>

            <c:if test="${(userRole.studyDirector || userBean.sysAdmin) && (study.status.available) && userRole.id ne 10 && !userRole.studyMonitor && !userRole.siteMonitor}">
				<ui:removeEventCRFLink object="${dec}" subjectId="${studySubject.id}"/>
            </c:if>

            <c:if test="${userBean.sysAdmin && (study.status.available) && userRole.id ne 10 && !userRole.studyMonitor && !userRole.siteMonitor}">
                <ui:deleteEventCRFLink object="${dec}" subjectId="${studySubject.id}" hspace="4"/>
            </c:if>

            <c:if test="${dec.eventCRF.id > 0 && !dec.eventCRF.notStarted && !dec.locked && !dec.stage.locked && (userRole.sysAdmin || userRole.studyAdministrator) && (study.status.available || study.status.pending) && !(studyEvent.subjectEventStatus.removed || studyEvent.subjectEventStatus.locked || studyEvent.subjectEventStatus.stopped || studyEvent.subjectEventStatus.skipped) && dec.eventDefinitionCRF.versions != null && fn:length(dec.eventDefinitionCRF.versions) > 1}">
				<ui:choseCRFVersionIcon dec="${dec}" subjectBean="${studySubject}" hspace="4"/>
			</c:if>
        </c:otherwise>
    </c:choose>
    </div>
</td>
</tr>
<c:set var="rowCount" value="${rowCount + 1}" />
</c:when>
</c:choose>
</c:forEach>

</c:otherwise>
</c:choose>
</table>

<!-- End Table Contents -->

</div>
</div></div></div></div></div></div></div></div>
</div> 
<br> 
<form method="POST" action="ViewStudySubject">
    <input type="hidden" name="id" value="<c:out value="${studySubject.id}"/>" />
    <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');"/> 
    <input type="button" name="BTN_BackToSV" id="GoToSV" value="<fmt:message key="view_subject_record2" bundle="${resword}"/>" class="button_long" onClick="window.location.href = 'ViewStudySubject?id=${studySubject.id}';"/>
    <c:if test="${!hideScheduleEventButton and userRole.id ne 6 and userRole.id ne 9 and userRole.id ne 10}">
    	<input type="button" name="BTN_Schedule" id="ScheduleEvent" value="<fmt:message key="schedule_event" bundle="${resword}"/>" class="button_long" onClick="javascript: window.location.href=('CreateNewStudyEvent?studySubjectId=<c:out value="${studySubject.id}"/>&studyEventDefinition=<c:out value="${studyEvent.studyEventDefinition.id}"/>');">
    </c:if>
</form>
<br>

<c:import url="instructionsEnterData.jsp">
    <c:param name="currStep" value="eventOverview" />
</c:import>

<c:if test="${!showDDEColumn}">
    <script>
        jQuery(".ddeColumn").css("display", "none");
        jQuery(".ddeColumnHeader").css("display", "none");
    </script>
</c:if>

<jsp:include page="../include/footer.jsp"/>
