<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<jsp:useBean scope='request' id='siteToView' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope="request" id="parentName" class="java.lang.String"/>
<jsp:useBean scope='session' id='fromListSite' class="java.lang.String"/>
<jsp:useBean scope='request' id='definitions' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='idToSort' class="java.lang.String"/>


<c:choose>
  <c:when test="${fromListSite=='yes'}">
    <c:import url="../include/managestudy-header.jsp"/>
  </c:when>
  <c:otherwise>
    <c:choose>
      <c:when test="${isAdminServlet == 'admin' && userBean.sysAdmin}">
          <c:import url="../include/admin-header.jsp"/>
      </c:when>
      <c:otherwise>
        <c:import url="../include/home-header.jsp"/>
      </c:otherwise>
    </c:choose>

  </c:otherwise>
 </c:choose>

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

<script language="JavaScript">
    function runCasebooks() {
        if($("#sidebar_Alerts_open").css("display") == 'none') {
            leftnavExpand('sidebar_Alerts_open');
            leftnavExpand('sidebar_Alerts_closed');
        }
        $("#sidebar_Alerts_open .sidebar_tab_content").html('').append("<div class='alert'><fmt:message key="site_level_casebooks_in_progress" bundle="${resword}"/></div>");
        document.body.scrollTop = document.documentElement.scrollTop = 0;
        $.ajax({
            type: "POST",
            url: "${pageContext.request.contextPath}/pages/generateSiteCasebooks",
            data: {
                siteId: "${siteToView.id}"
            },
            success: function() {
                //$("#sidebar_Alerts_open .sidebar_tab_content").html('');
            },
            error: function(e) {
                console.log("Error: " + e);
            }
        });
    }

    function leftnavExpand(strLeftNavRowElementName){
      var objLeftNavRowElement;
      objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
      if (objLeftNavRowElement != null) {
          if (objLeftNavRowElement.style) {
              objLeftNavRowElement = objLeftNavRowElement.style;
          }
          objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";

          objExCl = MM_findObj("excl_" + strLeftNavRowElementName);
          if (typeof objExCl != 'undefined' && objExCl != null) {
              if (objLeftNavRowElement.display == "none") {
                  objExCl.src = "images/bt_Expand.gif";
              } else {
                  objExCl.src = "images/bt_Collapse.gif";
              }
          }
        }
      }
</script>
<h1>
	<span class="first_level_header">
		<c:choose>
			<c:when test="${fromListSite=='yes'}">
				<fmt:message key="view_site_details" bundle="${resworkflow}"/>: <c:out value="${siteToView.name}"/>
			</c:when>
			<c:otherwise>
				<fmt:message key="view_site_details" bundle="${resworkflow}"/>
			</c:otherwise>
		</c:choose>
	</span>
</h1>
<strong><fmt:message key="download_oid_in_odm_format_by_click" bundle="${restext}"/>
 <a href="javascript:openDocWindow('DownloadStudyMetadata?studyId=${siteToView.id}');"><fmt:message key="here" bundle="${restext}"/></a>.</strong>
<fmt:message key="get_subject_oid_from_matrix_show_more" bundle="${restext}"/>
<br><br>

<div class="table_title_Manage"><a href="javascript:leftnavExpand('siteProperties');">
    <img id="excl_siteProperties" src="images/bt_Collapse.gif" border="0"> <fmt:message key="view_site_properties" bundle="${resword}"/> </a></div>
<c:choose>
<c:when test="${idToSort>0}">
	<div id="siteProperties" style="display: none">
</c:when>
<c:otherwise>
	<div id="siteProperties" style="display: all">
</c:otherwise>
</c:choose>

<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="parent_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${parentName}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="site_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.name}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="unique_protocol_ID" bundle="${resword}"/>: </td><td class="table_cell">
  <c:out value="${siteToView.identifier}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="secondary_IDs" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${siteToView.secondaryIdentifier}"/>
   </td></tr>

    <tr valign="top"><td class="table_header_column"><fmt:message key="OID" bundle="${resword}"/>:</td><td class="table_cell">
   <c:out value="${siteToView.oid}"/>
   </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.principalInvestigator}"/>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.summary}"/>
  </td></tr>

	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="protocol_verification" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<cc-fmt:formatDate value="${siteToView.protocolDateVerification}" dateTimeZone="${userBean.userTimeZoneId}"/>
		</td>
	</tr>

	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="start_date" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<cc-fmt:formatDate value="${siteToView.datePlannedStart}" dateTimeZone="${userBean.userTimeZoneId}"/>&nbsp;
		</td>
	</tr>

	<tr valign="top">
		<td class="table_header_column"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:
		</td>
		<td class="table_cell">
			<cc-fmt:formatDate value="${siteToView.datePlannedEnd}" dateTimeZone="${userBean.userTimeZoneId}"/>&nbsp;
		</td>
	</tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="expected_total_enrollment" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.expectedTotalEnrollment}"/>&nbsp;
  </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityName}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_city" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityCity}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_state_province" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityState}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_ZIP" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityZip}"/>&nbsp;
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_country" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityCountry}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactName}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_degree" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactDegree}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_phone" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactPhone}"/>&nbsp;
 </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="facility_contact_email" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${siteToView.facilityContactEmail}"/>&nbsp;
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="status" bundle="${resword}"/>:</td><td class="table_cell">
   <c:choose>
    <c:when test="${siteToView.status.locked}">
        <strong><c:out value="${siteToView.status.name}"/></strong>
    </c:when>
    <c:otherwise>
        <c:out value="${siteToView.status.name}"/>
    </c:otherwise>
  </c:choose>
   </td></tr>

  <c:forEach var="config" items="${siteToView.studyParameters}">
   <c:choose>
    <c:when test="${config.parameter.handle=='markImportedCRFAsCompleted'}">
       <tr valign="top">
           <td class="table_header_column"><fmt:message key="markImportedCRFAsCompleted" bundle="${resword}"/></td>
           <td class="table_cell">
               <c:choose>
                   <c:when test="${config.value.value== 'yes'}">
                       <fmt:message key="yes" bundle="${resword}"/>
                   </c:when>
                   <c:otherwise>
                       <fmt:message key="no" bundle="${resword}"/>
                   </c:otherwise>
               </c:choose>
           </td>
       </tr>
   </c:when>
   <c:when test="${config.parameter.handle=='autoScheduleEventDuringImport'}">
     <tr valign="top">
       <td class="table_header_column"><fmt:message key="autoScheduleEventDuringImport" bundle="${resword}"/></td>
       <td class="table_cell">
         <c:choose>
           <c:when test="${config.value.value== 'yes'}">
             <fmt:message key="yes" bundle="${resword}"/>
           </c:when>
           <c:otherwise>
             <fmt:message key="no" bundle="${resword}"/>
           </c:otherwise>
         </c:choose>
       </td>
     </tr>
   </c:when>
     <c:when test="${config.parameter.handle=='autoCreateSubjectDuringImport'}">
       <tr valign="top">
         <td class="table_header_column"><fmt:message key="autoCreateSubjectDuringImport" bundle="${resword}"/></td>
         <td class="table_cell">
           <c:choose>
             <c:when test="${config.value.value== 'yes'}">
               <fmt:message key="yes" bundle="${resword}"/>
             </c:when>
             <c:otherwise>
               <fmt:message key="no" bundle="${resword}"/>
             </c:otherwise>
           </c:choose>
         </td>
       </tr>
     </c:when>
   <c:when test="${config.parameter.handle=='allowSdvWithOpenQueries'}">
       <tr valign="top">
           <td class="table_header_column"><fmt:message key="allowSdvWithOpenQueries" bundle="${resword}"/></td>
           <td class="table_cell">
               <c:choose>
                   <c:when test="${config.value.value== 'yes'}">
                       <fmt:message key="yes" bundle="${resword}"/>
                   </c:when>
                   <c:otherwise>
                       <fmt:message key="no" bundle="${resword}"/>
                   </c:otherwise>
               </c:choose>
           </td>
       </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='autoTabbing'}">
        <tr valign="top">
            <td class="table_header_column"><fmt:message key="useAutoTabbing" bundle="${resword}"/></td>
            <td class="table_cell">
                <c:choose>
                    <c:when test="${config.value.value== 'yes'}">
                        <fmt:message key="yes" bundle="${resword}"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:message key="no" bundle="${resword}"/>
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
    </c:when>
    <c:when test="${config.parameter.handle=='allowDynamicGroupsManagement'}">
    	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="allowDynamicGroupsManagement" bundle="${resword}" />
		</td>
		<td class="table_cell">
			<c:choose>
				<c:when test="${config.value.value == 'yes'}">
					<fmt:message key="yes" bundle="${resword}" />
				</c:when>
				<c:otherwise>
					<fmt:message key="no" bundle="${resword}" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
    </c:when>
    <c:when test="${config.parameter.handle=='allowDiscrepancyCorrectionForms'}">
    	<tr valign="top">
		<td class="table_header_column">
			<fmt:message key="dcf_allow_discrepancy_correction_forms" bundle="${resword}" />
		</td>
		<td class="table_cell">
			<c:choose>
				<c:when test="${config.value.value == 'yes'}">
					<fmt:message key="yes" bundle="${resword}" />
				</c:when>
				<c:otherwise>
					<fmt:message key="no" bundle="${resword}" />
				</c:otherwise>
			</c:choose>
		</td>
	</tr>
    </c:when>
   <c:when test="${config.parameter.handle=='replaceExisitingDataDuringImport'}">
       <tr valign="top">
           <td class="table_header_column"><fmt:message key="replaceExisitingDataDuringImport" bundle="${resword}"/></td>
           <td class="table_cell">
               <c:choose>
                   <c:when test="${config.value.value== 'yes'}">
                       <fmt:message key="yes" bundle="${resword}"/>
                   </c:when>
                   <c:otherwise>
                       <fmt:message key="no" bundle="${resword}"/>
                   </c:otherwise>
               </c:choose>
           </td>
       </tr>
   </c:when>
	<c:when test="${config.parameter.handle=='interviewerNameRequired'}">
		   <tr valign="top"><td class="table_header_column"><fmt:message key="when_entering_data" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'yes'}">
		   <fmt:message key="yes" bundle="${resword}"/>
		   </c:when>
           <c:when test="${config.value.value== 'no'}">
           <fmt:message key="no" bundle="${resword}"/>
           </c:when>
		   <c:otherwise>
		   <fmt:message key="not_used" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameDefault'}">
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		    <fmt:message key="blank" bundle="${resword}"/>

		   </c:when>
		   <c:otherwise>
		   <fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewerNameEditable'}">
		<tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_name_editable" bundle="${resword}"/></td><td class="table_cell">
			   <c:choose>
				   <c:when test="${config.value.value== 'true'}">
					   <fmt:message key="yes" bundle="${resword}"/>
				   </c:when>
				   <c:when test="${config.value.value== 'false'}">
					   <fmt:message key="no" bundle="${resword}"/>
				   </c:when>
			   </c:choose>
		   </td>
		</tr>
	</c:when>
	<c:when test="${config.parameter.handle=='interviewDateRequired'}">
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_required" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'yes'}">
		   <fmt:message key="yes" bundle="${resword}"/>
		   </c:when>
           <c:when test="${config.value.value== 'no'}">
           <fmt:message key="no" bundle="${resword}"/>
           </c:when>
		   <c:otherwise>
		    <fmt:message key="not_used" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
    </c:when>
	<c:when test="${config.parameter.handle=='interviewDateEditable'}">
		<tr valign="top"><td class="table_header_column"><fmt:message key="interviewer_date_editable" bundle="${resword}"/></td><td class="table_cell">
			   <c:choose>
				   <c:when test="${config.value.value== 'true'}">
					   <fmt:message key="yes" bundle="${resword}"/>
				   </c:when>
				   <c:when test="${config.value.value== 'false'}">
					   <fmt:message key="no" bundle="${resword}"/>
				   </c:when>
			   </c:choose>
		   </td>
		</tr>
	</c:when>

	<c:when test="${config.parameter.handle=='interviewDateDefault'}">
		  <tr valign="top"><td class="table_header_column"><fmt:message key="interview_date_default_as_blank" bundle="${resword}"/></td><td class="table_cell">
		   <c:choose>
		   <c:when test="${config.value.value== 'blank'}">
		   <fmt:message key="blank" bundle="${resword}"/>

		   </c:when>
		   <c:otherwise>

		   <fmt:message key="pre_populated_from_study_event" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	 </c:when>

	<c:when test="${config.parameter.handle=='subjectPersonIdRequired'}">
		<tr valign="top">
			<td class="table_header_column"><fmt:message key="subject_person_ID_required" bundle="${resword}"/></td>
			<td class="table_cell">
				<c:choose>
					<c:when test="${config.value.value == 'copyFromSSID'}">
						<fmt:message key="copy_from_ssid" bundle="${resword}"/>
					</c:when>

					<c:when test="${config.value.value == 'required'}">
						<fmt:message key="required" bundle="${resword}"/>
					</c:when>

					<c:when test="${config.value.value == 'optional'}">
						<fmt:message key="optional" bundle="${resword}"/>
					</c:when>
	
					<c:otherwise>
						<fmt:message key="not_used" bundle="${resword}"/>
					</c:otherwise>
				</c:choose>
			</td>
		</tr>
	 </c:when>
</c:choose>
</c:forEach>

</table>

</div>
</div></div></div></div></div></div></div></div>

</div>

<br>
<c:if test = "${not empty definitions}">
	<div class="table_title_Manage" style="width:300px;float:left"><fmt:message key="view_site_event_definitions" bundle="${resword}"/></div>
</c:if>
<div style="clear:both"></div>


<c:set var="defCount" value="0"/>
<c:forEach var="definition" items="${definitions}">
<c:set var="defCount" value="${defCount+1}"/>
	&nbsp&nbsp&nbsp&nbsp<a href="javascript:leftnavExpand('sed${defCount}');">
        <img id="excl_sed${defCount}" src="images/bt_Expand.gif" border="0"><b>${definition.name}</b></a>
		<c:choose>
		<c:when test="${idToSort>0 && idToSort == definition.id}">
			<div id="sed<c:out value="${defCount}"/>" style="display: all">
		</c:when>
		<c:otherwise>
			<div id="sed<c:out value="${defCount}"/>" style="display: none">
		</c:otherwise>
	</c:choose>
<div style="width: 600px">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr valign="top"><td class="table_header_column"><fmt:message key="name" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${definition.name}"/>
   </td></tr>
    <tr valign="top"><td class="table_header_column"><fmt:message key="oid" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${definition.oid}"/>
   </td></tr>
  <tr valign="top"><td class="table_header_column"><fmt:message key="description" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${definition.description}"/>&nbsp;
  </td></tr>

 <tr valign="top"><td class="table_header_column"><fmt:message key="repeating" bundle="${resword}"/>:</td><td class="table_cell">
  <c:choose>
   <c:when test="${definition.repeating == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
   <c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
  </c:choose>
  </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="type" bundle="${resword}"/>:</td><td class="table_cell">
    <c:out value="${definition.type}"/>
   </td></tr>

  <tr valign="top"><td class="table_header_column"><fmt:message key="category" bundle="${resword}"/>:</td><td class="table_cell">
  <c:out value="${definition.category}"/>&nbsp;
  </td></tr>
  </table>
  </div>
</div></div></div></div></div></div></div></div>

</div>

<c:set var="eventDefinitionCRFs" value="${definition.crfs}"/>
<c:if test="${!empty eventDefinitionCRFs}">
<div class="table_title_manage">
  <fmt:message key="CRFs" bundle="${resword}"/>
</div>
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">


<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">
	<tr valign="top">
		<td width="90px" class="table_header_row"><fmt:message key="name" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="required" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="double_data_entry" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="password_required" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="default_version" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="hidden_crf" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="selected_verions" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="sdv_option" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="status" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="send_email_when" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="email_crf_to" bundle="${resword}"/></td>
		<c:if test="${study.studyParameterConfig.studyEvaluator == 'yes' || crf.evaluatedCRF == true}">
			<td valign="top" class="table_header_row"><fmt:message key="evaluated_crf" bundle="${resword}"/></td>
		</c:if>
        <td valign="top" class="table_header_row"><fmt:message key="crfTabbingMode" bundle="${resword}"/></td>
		<td valign="top" class="table_header_row"><fmt:message key="actions" bundle="${resword}"/></td>
	</tr>

	<c:set var="prevCrf" value=""/>
	<c:set var="count" value="0"/>
	<c:set var="defSize" value="${fn:length(eventDefinitionCRFs)}"/>
	<c:set var="last" value="${defSize-1}"/>

	<c:forEach var ="crf" items="${eventDefinitionCRFs}" varStatus="status">
	<c:choose>
		<c:when test="${count == last}">
			<c:set var="nextCrf" value="${eventDefinitionCRFs[count]}"/>
		</c:when>
		<c:otherwise>
			<c:set var="nextCrf" value="${eventDefinitionCRFs[count+1]}"/>
		</c:otherwise>
	</c:choose>

	<tr valign="top">
		<td class="table_cell" width="90px"><c:out value="${crf.crfName}"/></td>

		<c:choose>
			<c:when test="${fn:length(crf.selectedVersionIds)>0}">
				<c:set var="selectedVersionNames" value="${crf.selectedVersionNames}"/>
			</c:when>
			<c:otherwise>
				<c:set var="selectedVersionNames" value=""/>
				<c:forEach var="v" items="${crf.versions}">
					<c:set var="selectedVersionNames" value="${selectedVersionNames}${v.name},"/>
				</c:forEach>
				<c:set var="selectedVersionNames" value="${fn:substring(selectedVersionNames,0,fn:length(selectedVersionNames)-1)}"/>
			</c:otherwise>
		</c:choose>

		<td class="table_cell">
			<c:choose>
				<c:when test="${crf.requiredCRF == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
				<c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
			</c:choose>
		</td>

		<td class="table_cell">
			<c:choose>
				<c:when test="${crf.doubleEntry == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
				<c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
			</c:choose>
		</td>

		<td class="table_cell">
			<c:choose>
				<c:when test="${crf.electronicSignature == true}"> <fmt:message key="yes" bundle="${resword}"/> </c:when>
				<c:otherwise> <fmt:message key="no" bundle="${resword}"/> </c:otherwise>
			</c:choose>
		</td>

		<td class="table_cell">
			<c:out value="${crf.defaultVersionName}"/>
		</td>

		<td class="table_cell">
			<c:out value="${crf.hideCrf}"/>
		</td>

		<td class="table_cell">
			<c:out value="${selectedVersionNames}"/>
		</td>

		<td class="table_cell">
			<fmt:message key="${crf.sourceDataVerification.description}" bundle="${resterm}"/>
		</td>

		<td class="table_cell">
			<c:out value="${crf.status.name}"/>
		</td>

		<td class="table_cell">
			<c:out value="${crf.emailStep}"/>&nbsp;
		</td>

		<td class="table_cell">
			<c:out value="${crf.emailTo}"/>&nbsp;
		</td>


		<c:if test="${study.studyParameterConfig.studyEvaluator == 'yes' || edc.evaluatedCRF == true}">
			<td class="table_cell">
				<c:choose>
					<c:when test="${crf.evaluatedCRF == true}">
						<fmt:message key="yes" bundle="${resword}"/>
					</c:when>
					<c:otherwise>
						<fmt:message key="no" bundle="${resword}"/>
					</c:otherwise>
				</c:choose>
			</td>
		</c:if>

        <td class="table_cell">
            <c:choose>
                <c:when test='${crf.tabbingMode == "leftToRight"}'>
                    <fmt:message key="leftToRight" bundle="${resword}"/>
                </c:when>
                <c:otherwise>
                    <fmt:message key="topToBottom" bundle="${resword}"/>
                </c:otherwise>
            </c:choose>
        </td>

		<td class="table_cell">
			<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<td>
						<a href="ViewCRF?crfId=<c:out value="${crf.crfId}"/>"
								onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
								onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"><img
								name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" 
								bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
					</td>
				</tr>
			</table>
		</td>
	</tr>

	<c:set var="prevCrf" value="${crf}"/>
	<c:set var="count" value="${count+1}"/>
	</c:forEach>

</table>
</div>
</div></div></div></div></div></div></div></div>

</div>
</c:if>
</div><br>
</c:forEach>
</div>
<br><br>
<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
	value="<fmt:message key="back" bundle="${resword}"/>"
	class="button_medium medium_back"
	onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
	
<c:if test = "${not empty definitions}">
	<input type="button" name="BTN_Print" 
		value="<fmt:message key="site_crfs" bundle="${resword}"/>" 
		class="button_long" 
		onclick="javascript:openDocWindow('${pageContext.request.contextPath}/print/metadata/html/print/${parentStudyOid}/*/*')"/>
	<c:if test = "${showCasebookButton and study.studyParameterConfig.subjectCasebookInPDF != 'no'}">
        <input type="button" name="BTN_Print"
               value="<fmt:message key="print_site_casebooks" bundle="${resword}"/>"
               class="button_long"
               onclick="javascript:runCasebooks()"/>
	</c:if>
</c:if>
 <br><br>

<jsp:include page="../include/footer.jsp"/>
