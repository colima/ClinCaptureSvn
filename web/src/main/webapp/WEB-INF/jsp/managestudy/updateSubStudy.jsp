<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="resnotes"/>

<jsp:include page="../include/managestudy-header.jsp"/>

<jsp:include page="../include/sidebar.jsp"/>
<jsp:useBean scope='request' id='parentStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='newStudy' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="statuses" class="java.util.ArrayList"/>
<jsp:useBean scope="session" id="parentName" class="java.lang.String"/>
<jsp:useBean scope='session' id='definitions' class='java.util.ArrayList'/>
<jsp:useBean scope='request' id='messages' class='java.util.HashMap'/>

<c:set var="startDate" value="" />
<c:set var="endDate" value="" />
<c:set var="approvalDate" value="" />
<c:set var="bioontologyURL" value="${studyToView.studyParameterConfig.defaultBioontologyURL}"/>

<c:forEach var="presetValue" items="${presetValues}">
	<c:if test='${presetValue.key == "startDate"}'>
		<c:set var="startDate" value="${presetValue.value}" />
	</c:if>
	<c:if test='${presetValue.key == "endDate"}'>
		<c:set var="endDate" value="${presetValue.value}" />
	</c:if>
    <c:if test='${presetValue.key == "approvalDate"}'>
		<c:set var="approvalDate" value="${presetValue.value}" />
	</c:if>	
</c:forEach>

<script language="JavaScript">
	<c:import url="../../../includes/js/pages/update_study.js?r=${revisionNumber}" />
</script>

<script type="text/JavaScript" language="JavaScript">
function updateVersionSelection(vsIds, index, count) {
	var s = "vs"+count;
	var mvs = document.getElementById(s);
	if(vsIds.length>0) {
		for(i=0; i<mvs.length; ++i) {
			var t = "," + mvs.options[i].value + ",";
			if(vsIds.indexOf(t)>= 0 ) {
				mvs.options[i].selected = true;
			} else {
				mvs.options[i].selected = false;
			}
		}
		mvs.options[index].selected = true;
	} else {
		for(i=0; i<mvs.length; ++i) {
			mvs.options[i].selected = true;
		}
	}
}
//make sure current chosen default version among those selected versions.
function updateThis(multiSelEle, count) {
	var s = "dv"+count;
	var currentDefault = document.getElementById(s);
	for(i=0; i<multiSelEle.length; ++i) {
		if(multiSelEle.options[i].value == currentDefault.options[currentDefault.selectedIndex].value) {
			multiSelEle.options[i].selected = true;
		}
	}
}
    function leftnavExpand(strLeftNavRowElementName){
      var objLeftNavRowElement;

      objLeftNavRowElement = MM_findObj(strLeftNavRowElementName);
      if (objLeftNavRowElement != null) {
        if (objLeftNavRowElement.style) { objLeftNavRowElement = objLeftNavRowElement.style; }
          objLeftNavRowElement.display = (objLeftNavRowElement.display == "none" ) ? "" : "none";
          objExCl = MM_findObj("excl_"+strLeftNavRowElementName);
          if(objLeftNavRowElement.display == "none"){
              objExCl.src = "images/bt_Expand.gif";
          }else{
              objExCl.src = "images/bt_Collapse.gif";
          }
        }
      }
</script>

<h1>
	<span class="first_level_header">
		<fmt:message key="update_site_details" bundle="${resword}"/>: <c:out value="${newStudy.name}"/>
	</span>
</h1>
<br><br>

<jsp:include page="../include/alertbox.jsp" />
<form action="UpdateSubStudy" method="post" id="updateSubStudyForm">
<span class="alert">* </span><fmt:message key="indicates_required_field" bundle="${resword}"/><br>
<input type="hidden" name="action" value="confirm">
<input type="hidden" id="formWithStateFlag" value=""/>

 <div class="table_title_Manage"><a href="javascript:leftnavExpand('siteProperties');">
     <img id="excl_siteProperties" src="images/bt_Collapse.gif" border="0"> <fmt:message key="update_site_properties" bundle="${resword}"/> </a></div>

<c:choose>
<c:when test="${messages == null}">
	<div id="siteProperties" style="display: none">
</c:when>
<c:otherwise>
	<div id="siteProperties" style="display: all">
</c:otherwise>
</c:choose>
<!-- These DIVs define shaded box borders -->
 <div style="width: 100%">
<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<div class="textbox_center">
<table border="0" cellpadding="0" cellspacing="0" style="width: 100%">
  <tr valign="top"><td class="formlabel"><fmt:message key="parent_study" bundle="${resword}"/>:</td><td><div class="formfieldXL_BG">
  &nbsp;<c:out value="${parentName}"/>
 </div></td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="site_name" bundle="${resword}"/>:</td><td style="width: 45%;">
  <div class="formfieldXL_BG"><input type="text" name="siteName" value="<c:out value="${newStudy.name}"/>" class="formfieldXL" ${newStudy.origin eq 'studio' ? 'maxlength="20"' : 'maxlength="100"'}></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="siteName"/></jsp:include></td><td class="alert" style="width: 14%;">*</td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#PrimaryId'); return false;"><b><fmt:message key="unique_protocol_ID" bundle="${resword}"/></b>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="protocolId" value="<c:out value="${newStudy.identifier}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="protocolId"/></jsp:include></td><td class="alert">*</td></tr>

  <tr valign="top"><td class="formlabel"><a href="http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds" target="def_win" onClick="openDefWindow('http://prsinfo.clinicaltrials.gov/definitions.html#SecondaryIds'); return false;"><b><fmt:message key="secondary_IDs" bundle="${resword}"/></b>:</a><br>(<fmt:message key="separate_by_commas" bundle="${resword}"/>)</td>
  <td><div class="formtextareaXL4_BG">
   <textarea class="formtextareaXL4" name="secondProId" rows="4" cols="50"><c:out value="${newStudy.secondaryIdentifier}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="secondProId" value="facName"/></jsp:include>
  </td></tr>


  <tr valign="top"><td class="formlabel"><fmt:message key="principal_investigator" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="principalInvestigator" value="<c:out value="${newStudy.principalInvestigator}"/>"  class="formfieldXL"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="principalInvestigator"/></jsp:include></td><td class="alert">*</td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="brief_summary" bundle="${resword}"/>:</td><td>
  <div class="formtextareaXL4_BG"><textarea class="formtextareaXL4" name="summary" rows="4" cols="50" maxlength="2000"><c:out value="${newStudy.summary}"/></textarea></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="summary"/></jsp:include></td><td class="formlabel"></td></tr>
  
  <tr valign="top"><td class="formlabel">
  	<fmt:message key="protocol_verification" bundle="${resword}"/>:
  </td><td><div class="formfieldXL_BG">
  <input type="text" name="approvalDate" value="<c:out value="${approvalDate}" />" class="formfieldXL" id="protocolDateVerificationField"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="approvalDate"/></jsp:include></td>
  <td>
	  <ui:calendarIcon onClickSelector="'#protocolDateVerificationField'"/>
  </td>
  </tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="start_date" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG">
      <input type="text" name="startDate" value="<c:out value="${startDate}" />"  class="formfieldXL" id="startDateField">
  </div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="startDate"/></jsp:include></td>
  <td>
	  <ui:calendarIcon onClickSelector="'#startDateField'"/>
  </td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="estimated_completion_date" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG">
      <input type="text" name="endDate" value="<c:out value="${endDate}" />"  class="formfieldXL" id="endDateField"></div>
  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="endDate"/></jsp:include></td>
  <td>
	  <ui:calendarIcon onClickSelector="'#endDateField'"/>
  </td></tr>

  <tr valign="top"><td class="formlabel"><fmt:message key="expected_total_enrollment" bundle="${resword}"/>:</td><td>
  <div class="formfieldXL_BG"><input type="text" name="totalEnrollment" value="<c:out value="${newStudy.expectedTotalEnrollment}"/>" class="formfieldXL"></div>
   <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="totalEnrollment"/></jsp:include>
  </td><td class="alert">*</td></tr>

  <c:forEach items="${studyFacilities}" var="studyFacility" varStatus="studyFacilitiesStatus">
	  <tr valign="top">
		  <td class="formlabel"><fmt:message key="${studyFacility.code}" bundle="${resword}"/>:</td>
		  <td>
			  <div class="formfieldXL_BG">
				  <input type="text" name="${studyFacility.name}" value="${newStudy[studyFacility.name]}" class="formfieldXL">
			  </div><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="${studyFacility.name}"/></jsp:include>
		  </td>
	  </tr>
  </c:forEach>

  <c:choose>
   <c:when test="${newStudy.parentStudyId == 0}">
      <c:set var="key" value="study_system_status"/>
   </c:when>
   <c:otherwise>
       <c:set var="key" value="site_system_status"/>
   </c:otherwise>
  </c:choose>

  <tr valign="top"><td class="formlabel"><fmt:message key="${key}" bundle="${resword}"/>:</td><td>

   <c:set var="dis" value="${parentStudy.name!='' && !parentStudy.status.available}"/>
   <c:set var="status1" value="${newStudy.status.id}"/>
   <div>
	   <div class="formfieldM_BG" style="float: left"><select name="statusId" class="formfieldM" <c:if test="${dis}">disabled="true" </c:if>>
	      <c:forEach var="status" items="${statuses}">
	       <c:choose>
	        <c:when test="${status1 == status.id}">
	         <option value="<c:out value="${status.id}"/>" selected><c:out value="${status.name}"/>
	        </c:when>
	        <c:otherwise>
	         <option value="<c:out value="${status.id}"/>"><c:out value="${status.name}"/>
	        </c:otherwise>
	       </c:choose>
	    </c:forEach>
	   </select></div>
	   <div><span class="alert">&nbsp;&nbsp; * </span></div>
   </div>

  <jsp:include page="../showMessage.jsp"><jsp:param name="key" value="statusId"/></jsp:include></td></tr>


   <c:if test="${paramsMap['collectDob'] != null}">
     <tr valign="top"><td class="formlabel"><fmt:message key="collect_subject_date_of_birth" bundle="${resword}"/>:</td><td>
       <c:choose>
         <c:when test="${paramsMap['collectDob'] == '1'}">
           <input type="radio" checked name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
           <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
           <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:when>
         <c:when test="${paramsMap['collectDob'] == '2'}">
            <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
            <input type="radio" checked name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
            <input type="radio" name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:when>
         <c:otherwise>
            <input type="radio" name="collectDob" value="1"><fmt:message key="yes" bundle="${resword}"/>
            <input type="radio" name="collectDob" value="2"><fmt:message key="only_year_of_birth" bundle="${resword}"/>
            <input type="radio" checked name="collectDob" value="3"><fmt:message key="not_used" bundle="${resword}"/>
         </c:otherwise>
      </c:choose>
      </td></tr>
   </c:if>

   <c:if test="${paramsMap['discrepancyManagement'] != null}">
		  <tr valign="top"><td class="formlabel"><fmt:message key="allow_discrepancy_management" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${paramsMap['discrepancyManagement'] == 'false'}">
		    <input type="radio" name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="discrepancyManagement" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="discrepancyManagement" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:if>

	<c:if test="${paramsMap['genderRequired'] != null}">
		  <tr valign="top"><td class="formlabel"><fmt:message key="gender_required" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${paramsMap['genderRequired'] == 'false'}">
		    <input type="radio" name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" checked name="genderRequired" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="genderRequired" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:if>
    <c:if test="${paramsMap['subjectPersonIdRequired'] != null}">
		  <tr valign="top"><td class="formlabel"><fmt:message key="subject_person_ID_required" bundle="${resword}"/>:</td>
			<td colspan="2">
				<c:set var="subjectPersonIdRequired" value=""/>
				<c:set var="subjectPersonIdOptional" value=""/>
				<c:set var="subjectPersonIdCopy" value=""/>
				<c:set var="subjectPersonIdNotUsed" value=""/>

				<c:choose>
					<c:when test="${paramsMap['subjectPersonIdRequired'] == 'required'}">
						<c:set var="subjectPersonIdRequired" value="checked"/>
					</c:when>
					<c:when test="${paramsMap['subjectPersonIdRequired'] == 'optional'}">
						<c:set var="subjectPersonIdOptional" value="checked"/>
					</c:when>
					<c:when test="${paramsMap['subjectPersonIdRequired'] == 'copyFromSSID'}">
						<c:set var="subjectPersonIdCopy" value="checked"/>
					</c:when>
					<c:otherwise>
						<c:set var="subjectPersonIdNotUsed" value="checked"/>
					</c:otherwise>
				</c:choose>

				<input type="radio" onchange="javascript:changeIcon()" ${subjectPersonIdRequired} name="subjectPersonIdRequired" value="required"><fmt:message key="required" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" ${subjectPersonIdOptional} name="subjectPersonIdRequired" value="optional"><fmt:message key="optional" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" ${subjectPersonIdCopy} name="subjectPersonIdRequired" value="copyFromSSID"><fmt:message key="copy_from_ssid" bundle="${resword}"/>
				<input type="radio" onchange="javascript:changeIcon()" ${subjectPersonIdNotUsed} name="subjectPersonIdRequired" value="not_used"><fmt:message key="not_used" bundle="${resword}"/>
			</td>
		  </tr>
	</c:if>
	<c:if test="${paramsMap['subjectIdGeneration'] != null}">
		   <tr valign="top"><td class="formlabel"><fmt:message key="how_to_generate_the_subject" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${paramsMap['subjectIdGeneration'] == 'manual'}">
		    <input type="radio" checked name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto-editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto-non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:when>
		    <c:when test="${paramsMap['subjectIdGeneration'] == 'auto-editable'}">
		    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdGeneration" value="auto-editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto-non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectIdGeneration" value="manual"><fmt:message key="manual_entry" bundle="${resword}"/>
		    <input type="radio" name="subjectIdGeneration" value="auto-editable"><fmt:message key="auto_generated_and_editable" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdGeneration" value="auto-non-editable"><fmt:message key="auto_generated_and_non_editable" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:if>
	<c:if test="${paramsMap['subjectIdPrefixSuffix'] != null}">
		   <tr valign="top"><td class="formlabel"><fmt:message key="generate_study_subject_ID_automatically" bundle="${resword}"/>:</td><td>
		   <c:choose>
		   <c:when test="${paramsMap['subjectIdPrefixSuffix'] == 'true'}">
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="subjectIdPrefixSuffix" value="false"><fmt:message key="no" bundle="${resword}"/>

		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="subjectIdPrefixSuffix" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="subjectIdPrefixSuffix" value="false"><fmt:message key="no" bundle="${resword}"/>

		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:if>
    <c:if test="${paramsMap['markImportedCRFAsCompleted'] != null}">
        <tr valign="top">
            <td class="formlabel"><fmt:message key="markImportedCRFAsCompleted" bundle="${resword}"/></td>
            <td>
                <input type="radio" <c:if test="${paramsMap['markImportedCRFAsCompleted'] == 'yes'}">checked</c:if> name="markImportedCRFAsCompleted" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                <input type="radio" <c:if test="${paramsMap['markImportedCRFAsCompleted'] == 'no'}">checked</c:if> name="markImportedCRFAsCompleted" value="no"><fmt:message key="no" bundle="${resword}"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${paramsMap['autoScheduleEventDuringImport'] != null}">
      <tr valign="top">
        <td class="formlabel"><fmt:message key="autoScheduleEventDuringImport" bundle="${resword}"/></td>
        <td>
          <input type="radio" <c:if test="${paramsMap['autoScheduleEventDuringImport'] == 'yes'}">checked</c:if> name="autoScheduleEventDuringImport" value="yes"><fmt:message key="yes" bundle="${resword}"/>
          <input type="radio" <c:if test="${paramsMap['autoScheduleEventDuringImport'] == 'no'}">checked</c:if> name="autoScheduleEventDuringImport" value="no"><fmt:message key="no" bundle="${resword}"/>
        </td>
      </tr>
    </c:if>
    <c:if test="${paramsMap['autoCreateSubjectDuringImport'] != null}">
      <tr valign="top">
        <td class="formlabel"><fmt:message key="autoCreateSubjectDuringImport" bundle="${resword}"/></td>
        <td>
          <input type="radio" <c:if test="${paramsMap['autoCreateSubjectDuringImport'] == 'yes'}">checked</c:if> name="autoCreateSubjectDuringImport" value="yes"><fmt:message key="yes" bundle="${resword}"/>
          <input type="radio" <c:if test="${paramsMap['autoCreateSubjectDuringImport'] == 'no'}">checked</c:if> name="autoCreateSubjectDuringImport" value="no"><fmt:message key="no" bundle="${resword}"/>
        </td>
      </tr>
    </c:if>
   <c:if test="${paramsMap['allowSdvWithOpenQueries'] != null}">
        <tr valign="top">
            <td class="formlabel"><fmt:message key="allowSdvWithOpenQueries" bundle="${resword}"/></td>
            <td>
                <input type="radio" <c:if test="${paramsMap['allowSdvWithOpenQueries'] == 'yes'}">checked</c:if> name="allowSdvWithOpenQueries" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                <input type="radio" <c:if test="${paramsMap['allowSdvWithOpenQueries'] == 'no'}">checked</c:if> name="allowSdvWithOpenQueries" value="no"><fmt:message key="no" bundle="${resword}"/>
            </td>
        </tr>
    </c:if>
    <c:if test="${paramsMap['useAutoTabbing'] != null}">
        <tr valign="top">
            <td class="formlabel"><fmt:message key="useAutoTabbing" bundle="${resword}"/></td>
            <td>
                <input type="radio" <c:if test="${paramsMap['useAutoTabbing'] == 'yes'}">checked</c:if> name="autoTabbing" value="yes"><fmt:message key="yes" bundle="${resword}"/>
                <input type="radio" <c:if test="${paramsMap['useAutoTabbing'] == 'no'}">checked</c:if> name="autoTabbing" value="no"><fmt:message key="no" bundle="${resword}"/>
            </td>
        </tr>
    </c:if>
   <c:if test="${not empty paramsMap['replaceExisitingDataDuringImport']}">
       <tr valign="top">
           <td class="formlabel"><fmt:message key="replaceExisitingDataDuringImport" bundle="${resword}"/></td>
           <td>
               <input type="radio" <c:if test="${paramsMap['replaceExisitingDataDuringImport'] == 'yes'}">checked</c:if> name="replaceExisitingDataDuringImport" value="yes"><fmt:message key="yes" bundle="${resword}"/>
               <input type="radio" <c:if test="${paramsMap['replaceExisitingDataDuringImport'] == 'no'}">checked</c:if> name="replaceExisitingDataDuringImport" value="no"><fmt:message key="no" bundle="${resword}"/>
           </td>
       </tr>
   </c:if>
	<c:if test="${paramsMap['interviewDateRequired'] != null}">
		  <tr valign="top">
		  	<td class="formlabel">
		  		<fmt:message key="interviewer_date_required" bundle="${resword}"/>
		  	</td>
		   <td>
				<input type="radio" ${paramsMap['interviewDateRequired'] == 'yes' ? 'checked' : ''} onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewDate" name="interviewDateRequired" value="yes">
				<fmt:message key="required" bundle="${resword}"/>
				<input type="radio" ${paramsMap['interviewDateRequired'] == 'no' ? 'checked' : ''} onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewDate" name="interviewDateRequired" value="no">
				<fmt:message key="optional" bundle="${resword}"/>
				<input type="radio" ${paramsMap['interviewDateRequired'] == 'not_used' ? 'checked' : ''} onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="interviewDate" name="interviewDateRequired" value="not_used">
				<fmt:message key="not_used" bundle="${resword}"/>
					
			</td>
		  </td>
		  </tr>
    </c:if>
	<c:if test="${paramsMap['interviewDateDefault'] != null}">
		  <tr valign="top" class="interviewDate">
		  	<td class="formlabel">
		  		<fmt:message key="interviewer_date_default_as_blank" bundle="${resword}"/>
		  	</td>
		  	<td>
		     <c:choose>
				<c:when test="${paramsMap['interviewDateDefault'] == 'blank'}">
					<input type="radio" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this); changeParameterForStudy('interviewDateEditable', 'true');" checked name="interviewDateDefault" value="blank" data-cc-action="hide" data-row-class="interviewDateEditable"><fmt:message key="blank" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this);" name="interviewDateDefault" value="pre-populated" data-cc-action="show" data-row-class="interviewDateEditable"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
				</c:when>
				<c:otherwise>
					<input type="radio" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this); changeParameterForStudy('interviewDateEditable', 'true');" name="interviewDateDefault" value="blank" data-cc-action="hide" data-row-class="interviewDateEditable"><fmt:message key="blank" bundle="${resword}"/>
					<input type="radio" onchange="javascript:changeIcon()" onclick="hideUnhideStudyParamRow(this);" checked name="interviewDateDefault" value="pre-populated" data-cc-action="show" data-row-class="interviewDateEditable"><fmt:message key="pre_populated_from_SE" bundle="${resword}"/>
				</c:otherwise>
			</c:choose>
		  </td>
		  </tr>
	 </c:if>
	 <c:if test="${paramsMap['interviewDateEditable'] != null}">
		  <tr valign="top" class="interviewDate interviewDateEditable"><td class="formlabel"><fmt:message key="interviewer_date_editable" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${paramsMap['interviewDateEditable'] == 'true'}">
		    <input type="radio" checked name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
	
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="interviewDateEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewDateEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	 </c:if>
	 <c:if test="${paramsMap['interviewerNameRequired'] != null}">
		   <tr valign="top"><td class="formlabel"><fmt:message key="when_entering_data_entry_interviewer" bundle="${resword}"/></td>
		   <td>
			  	<input type="radio" ${paramsMap['interviewerNameRequired'] == 'yes' ? 'checked' : ''} onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewer" name="interviewerNameRequired" value="yes">
				<fmt:message key="required" bundle="${resword}"/>
				<input type="radio" ${paramsMap['interviewerNameRequired'] == 'no' ? 'checked' : ''} onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewer" name="interviewerNameRequired" value="no">
				<fmt:message key="optional" bundle="${resword}"/>
				<input type="radio" ${paramsMap['interviewerNameRequired'] == 'not_used' ? 'checked' : ''} onchange="javascript:changeIcon();" onclick="hideUnhideStudyParamRow(this);" data-cc-action="hide" data-row-class="interviewer" name="interviewerNameRequired" value="not_used">
				<fmt:message key="not_used" bundle="${resword}"/>
		  </td>
		  </tr>
	</c:if>
	<c:if test="${paramsMap['interviewerNameDefault'] != null}">
		  <tr valign="top" class="interviewer"><td class="formlabel"><fmt:message key="interviewer_name_default_as_blank" bundle="${resword}"/></td><td>
			   <c:choose>
				   <c:when test="${paramsMap['interviewerNameDefault'] == 'blank'}">
					    <input type="radio" checked name="interviewerNameDefault" value="blank" onclick="hideUnhideStudyParamRow(this); changeParameterForStudy('interviewerNameEditable', 'true');" data-cc-action="hide" data-row-class="interviewerEditable"><fmt:message key="blank" bundle="${resword}"/>
					    <input type="radio" name="interviewerNameDefault" value="pre-populated" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewerEditable"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
				   </c:when>
				   <c:otherwise>
					    <input type="radio" name="interviewerNameDefault" value="blank" onclick="hideUnhideStudyParamRow(this); changeParameterForStudy('interviewerNameEditable', 'true');" data-cc-action="hide" data-row-class="interviewerEditable"><fmt:message key="blank" bundle="${resword}"/>
					    <input type="radio" checked name="interviewerNameDefault" value="pre-populated" onclick="hideUnhideStudyParamRow(this);" data-cc-action="show" data-row-class="interviewerEditable"><fmt:message key="pre_populated_from_active_user" bundle="${resword}"/>
				   </c:otherwise>
			  </c:choose>
		  </td>
		  </tr>
	</c:if>
	<c:if test="${paramsMap['interviewerNameEditable'] != null}">
		  <tr valign="top" class="interviewer interviewerEditable"><td class="formlabel"><fmt:message key="interviewer_name_editable" bundle="${resword}"/></td><td>
		   <c:choose>
		   <c:when test="${paramsMap['interviewerNameEditable'] == 'true'}">
		    <input type="radio" checked name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:when>
		   <c:otherwise>
		    <input type="radio" name="interviewerNameEditable" value="true"><fmt:message key="yes" bundle="${resword}"/>
		    <input type="radio" checked name="interviewerNameEditable" value="false"><fmt:message key="no" bundle="${resword}"/>
		   </c:otherwise>
		  </c:choose>
		  </td>
		  </tr>
	</c:if>

</table>
</div>
  </div></div></div></div></div></div></div></div>
</div>
  </div>
<br>

<c:if test = "${not empty definitions}">
	<div class="table_title_Manage"><fmt:message key="update_site_event_definitions" bundle="${resword}"/></div>
</c:if>	
	
<c:set var="defCount" value="0"/>
<c:forEach var="def" items="${definitions}">
	<c:set var="defCount" value="${defCount+1}"/>
	&nbsp&nbsp&nbsp&nbsp<b><a href="javascript:leftnavExpand('sed<c:out value="${defCount}"/>');">
    <img id="excl_sed<c:out value="${defCount}"/>" src="images/bt_Expand.gif" border="0"> <c:out value="${def.name}"/></b></a>
    
	<div id="sed<c:out value="${defCount}"/>" style="display: none">

	<!-- These DIVs define shaded box borders -->
	<div style="width: 100%">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="tablebox_center">

	<table border="0" >
		<tr><td class="table_header_column" colspan="3" width="100px">Name</td><td class="table_cell" width="400px"><c:out value="${def.name}"/></td></tr>
		<tr><td class="table_header_column" colspan="3">Description</td><td class="table_cell"><c:out value="${def.description}"/></td></tr>
	</table>

	</div>
	</div></div></div></div></div></div></div></div>
	</div>

	<div class="table_title_manage"><fmt:message key="CRFs" bundle="${resword}"/></div>
	<div style="width: 100%">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B">
	<div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center">

	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<c:set var="count" value="0"/>
		<c:forEach var="edc" items="${def.crfs}">

		<c:set var="num" value="${count}-${edc.id}" />
		<tr valign="top" bgcolor="#F5F5F5">
			<td class="table_header_column" colspan="4"><c:out value="${edc.crfName}"/></td>
		</tr>

		<c:if test="${edc.status.id==1}">
		<c:choose>
			<c:when test="${fn:length(edc.selectedVersionIds)>0}">
				<c:set var="idList" value="${edc.selectedVersionIdList}"/>
				<c:set var="selectedIds" value=",${edc.selectedVersionIds},"/>
			</c:when>
			<c:otherwise>
				<c:set var="idList" value=""/>
				<c:set var="selectedIds" value=""/>
			</c:otherwise>
		</c:choose>

		<tr valign="top">
			<td class="table_cell" colspan="2"><fmt:message key="required" bundle="${resword}"/>:
				<c:choose>
					<c:when test="${edc.requiredCRF == true}">
						<input type="checkbox" checked name="requiredCRF<c:out value="${num}"/>" value="yes">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="requiredCRF<c:out value="${num}"/>" value="yes">
					</c:otherwise>
				</c:choose>
			</td>

			<td class="table_cell"><fmt:message key="password_required" bundle="${resword}"/>:
				<c:choose>
					<c:when test="${edc.electronicSignature == true}">
						<input type="checkbox" checked name="electronicSignature<c:out value="${num}"/>" value="yes">
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="electronicSignature<c:out value="${num}"/>" value="yes">
					</c:otherwise>
				</c:choose>
			</td>

			<td class="table_cell"><fmt:message key="default_version" bundle="${resword}"/>:
				<select name="defaultVersionId<c:out value="${num}"/>" id="dv<c:out value="${num}"/>" onclick="updateVersionSelection('<c:out value="${selectedIds}"/>',document.getElementById('dv<c:out value="${num}"/>').selectedIndex, '<c:out value="${num}"/>')">
					<c:forEach var="version" items="${edc.versions}">
						<c:choose>
							<c:when test="${edc.defaultVersionId == version.id}">
								<option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
							</c:when>
							<c:otherwise>
								<option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
							</c:otherwise>
						</c:choose>
					</c:forEach>
				</select>
			</td>
		</tr>

		<tr valign="top">
			<td class="table_cell" colspan="2"><fmt:message key="version_selection" bundle="${resword}"/>&nbsp:
			<select multiple name="versionSelection<c:out value="${num}"/>" id="vs<c:out value="${num}"/>" onclick="updateThis(document.getElementById('vs<c:out value="${num}"/>'), '<c:out value="${num}"/>')" size="${fn:length(edc.versions)}">
				<c:forEach var="version" items="${edc.versions}">
					<c:choose>
					<c:when test="${fn:length(idList) > 0}">
						<c:set var="versionid" value=",${version.id},"/>
						<c:choose>
						<c:when test="${version.id == defaultVersionId}">
							<option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
						</c:when>
						<c:otherwise>
						<c:choose>
							<c:when test="${fn:contains(selectedIds,versionid)}">
								<option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
							</c:when>
							<c:otherwise>
								<option value="<c:out value="${version.id}"/>"><c:out value="${version.name}"/>
							</c:otherwise>
							</c:choose>
						</c:otherwise>
						</c:choose>
					</c:when>
					<c:otherwise>
						<option value="<c:out value="${version.id}"/>" selected><c:out value="${version.name}"/>
					</c:otherwise>
					</c:choose>
				</c:forEach>
			</select>
			</td>

			<td class="table_cell" colspan="2"></td>
		</tr>

		<tr>
			<td class="table_cell" colspan="4"><fmt:message key="hidden_crf" bundle="${resword}"/> :
				<c:choose>
					<c:when test="${!edc.hideCrf}">
						<input type="checkbox" name="hideCRF<c:out value="${num}"/>" value="yes">
					</c:when>
					<c:otherwise><input checked="checked" type="checkbox" name="hideCRF<c:out value="${num}"/>" value="yes"></c:otherwise>
				</c:choose>
			</td>
		</tr>

        <tr valign="top">
            <td class="table_cell" colspan="4">
                <fmt:message key="data_entry_quality" bundle="${resword}"/>:
                <c:set var="deQualityDE" value=""/>
                <c:set var="deQualityEvaluatedCRF" value=""/>
                <c:choose>
                    <c:when test="${edc.doubleEntry == true}">
                        <c:set var="deQualityDE" value="checked"/>
                    </c:when>
                    <c:when test="${edc.evaluatedCRF == true}">
                        <c:set var="deQualityEvaluatedCRF" value="checked"/>
                    </c:when>
                </c:choose>

				<c:choose>
					<c:when test="${study.studyParameterConfig.studyEvaluator == 'yes' || edc.evaluatedCRF == true}">
						<input type="radio" name="deQuality${num}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="dde" class="email_field_trigger uncheckable_radio" ${deQualityDE}/>
						<fmt:message key="double_data_entry" bundle="${resword}"/>

						<input type="radio" name="deQuality${num}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="evaluation" class="email_field_trigger uncheckable_radio" ${deQualityEvaluatedCRF}/>
						<fmt:message key="crf_data_evaluation" bundle="${resword}"/>
					</c:when>
					<c:otherwise>
						<input type="checkbox" name="deQuality${num}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="dde" class="email_field_trigger uncheckable_radio" ${deQualityDE}/>
						<fmt:message key="double_data_entry" bundle="${resword}"/>
					</c:otherwise>
				</c:choose>
            </td>
        </tr>

		<tr valign="top">
			<td class="table_cell" colspan="2">

				<fmt:message key="send_email_when" bundle="${resword}"/>:<br/>
				<c:choose>
					<c:when test="${edc.emailStep eq 'complete'}">
						<c:set var="emailStepComplete" value="checked"/>
					</c:when>
					<c:otherwise>
						<c:set var="emailStepComplete" value=""/>
					</c:otherwise>
				</c:choose>

				<input type="radio" name="emailOnStep<c:out value="${num}"/>" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="complete" class="email_field_trigger uncheckable_radio" ${emailStepComplete}/>
				<fmt:message key="complete" bundle="${resterm}"/>

				<c:choose>
					<c:when test="${edc.emailStep eq 'sign'}">
						<c:set var="emailStepSign" value="checked"/>
					</c:when>
					<c:otherwise>
						<c:set var="emailStepSign" value=""/>
					</c:otherwise>
				</c:choose>

				<input type="radio" name="emailOnStep<c:out value="${num}"/>" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="sign" class="email_field_trigger uncheckable_radio" ${emailStepSign}/>
				<fmt:message key="sign" bundle="${resterm}"/>
			</td>

			<td class="table_cell" colspan="2">
				<c:choose>
					<c:when test="${empty edc.emailTo}">
						<c:set var="display" value="none"/>
					</c:when>
					<c:otherwise>
						<c:set var="display" value="block"/>
					</c:otherwise>
				</c:choose>

				<span class="email_wrapper" style="display:${display}">
					<fmt:message key="email_crf_to" bundle="${resword}"/>: 
					<input type="text" name="mailTo${num}" onchange="javascript:changeIcon();" style="width:160px;margin-left:15px" class="email_to_check_field" value="${edc.emailTo}"/>
				</span>
				<span class="alert" style="display:none"><fmt:message key="enter_valid_email" bundle="${resnotes}"/></span>
			</td>
		</tr>
        <tr valign="top">
            <td class="table_cell" colspan="4">
                <fmt:message key="crfTabbingMode" bundle="${resword}"/>:
                <c:set var="leftToRightTabbingMode" value="checked"/>
                <c:set var="topToBottomTabbingMode" value=""/>
                <c:if test='${edc.tabbingMode == "topToBottom"}'>
                    <c:set var="leftToRightTabbingMode" value=""/>
                    <c:set var="topToBottomTabbingMode" value="checked"/>
                </c:if>
                <input type="radio" name="tabbingMode${num}" onchange="javascript:changeIcon();" value="leftToRight" ${leftToRightTabbingMode}/> <fmt:message key="leftToRight" bundle="${resword}"/>
                <input type="radio" name="tabbingMode${num}" onchange="javascript:changeIcon();" value="topToBottom" ${topToBottomTabbingMode}/> <fmt:message key="topToBottom" bundle="${resword}"/>
            </td>
        </tr>
		<c:set var="count" value="${count+1}"/>
		</c:if>
		<tr><td class="table_divider" colspan="8">&nbsp;</td></tr>
		</c:forEach>
	</table>
	</div>
  	</div></div></div></div></div></div></div></div>
	</div>
	</div><br>
</c:forEach>

<br><br>
  <table border="0" cellpadding="0" cellspacing="0" style="width: 100%">
      <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
             value="<fmt:message key="back" bundle="${resword}"/>"
             class="button_medium medium_back"
             onClick="javascript: formWithStateGoBackSmart('<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" />
      <input type="button" name="Submit" value="<fmt:message key="submit" bundle="${resword}"/>" class="button_medium medium_submit"
			 onClick="javascript:validateCustomFields({expectedValues: ['email'], selectors: ['.email_to_check_field'], formToSubmit: '#updateSubStudyForm'});">
  </table>
</form>
<DIV ID="testdiv1" STYLE="position:absolute;visibility:hidden;background-color:white;layer-background-color:white;"></DIV>
<jsp:include page="../include/footer.jsp"/>
