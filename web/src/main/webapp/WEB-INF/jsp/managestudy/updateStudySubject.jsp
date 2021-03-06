<%@ page contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword" />
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat" />
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow" />

<c:choose>
	<c:when test="${userBean.sysAdmin || userBean.techAdmin || userRole.manageStudy}">
		<jsp:include page="../include/managestudy-header.jsp" />
	</c:when>
	<c:otherwise>
		<jsp:include page="../include/home-header.jsp" />
	</c:otherwise>
</c:choose>

<script type="text/JavaScript" language="JavaScript">
	function showDynamicEventsSection(defaultDynGroupClassId) {
  
		var index = $(":select [name='dynamicGroupClassId'] :selected").val();
			switch (index) {
				case '0':
					$("tr[id^='dynamicGroupId']").hide();   
					if (defaultDynGroupClassId != 0){
						$("tr#defaultDynGroupName").show();
						$("tr[id='dynamicGroupId"+defaultDynGroupClassId+"']").show();
					} else {
						$("tr#defaultDynGroupName").show();
					}
					break;
				default: 
					$("tr#defaultDynGroupName").hide();
					$("tr[id^='dynamicGroupId']").hide();   
					$("tr[id='dynamicGroupId"+index+"']").show();
			}
			return true;
	}
		  
	$(document).ready(function() { 
			showDynamicEventsSection(${defaultDynGroupClassId}); 
	});
</script>

<!-- move the alert message to the sidebar-->
<jsp:include page="../include/sideAlert.jsp" />

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a> 
		<b><fmt:message key="instructions" bundle="${resword}" /></b>
		<div class="sidebar_tab_content"></div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}" /></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp" />

<body class="aka_bodywidth"
	onload="if(! detectFirefoxWindows(navigator.userAgent) && document.getElementById('centralContainer') != undefined)
				{document.getElementById('centralContainer').style.display='none'; new Effect.Appear('centralContainer', {duration:1});};
        		<c:if test='${popUpURL != ""}'> openDNoteWindow('<c:out value="${popUpURL}" />'); </c:if>">

	<h1>
		<span class="first_level_header"> <fmt:message key="update_study_subject_details" bundle="${resword}" /> </span>
	</h1>

	<c:set var="secondaryIdShow" value="${true}" />
	<fmt:message key="secondary_ID" bundle="${resword}" var="secondaryIdLabel" />
	<c:if test="${study ne null}">
		<c:set var="secondaryIdShow" value="${!(study.studyParameterConfig.secondaryIdRequired == 'not_used')}" />
		<c:set var="secondaryIdLabel" value="${study.studyParameterConfig.secondaryIdLabel}" />
		<c:set var="secondaryIdRequired" value="${study.studyParameterConfig.secondaryIdRequired == 'yes'}" />
	</c:if>

	<c:set var="enrollmentDateShow" value="${true}" />
	<fmt:message key="enrollment_date" bundle="${resword}" var="enrollmentDateLabel" />
	<c:if test="${study ne null}">
		<c:set var="enrollmentDateShow" value="${!(study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'not_used')}" />
		<c:set var="enrollmentDateLabel" value="${study.studyParameterConfig.dateOfEnrollmentForStudyLabel}" />
		<c:set var="enrollmentDateRequired" value="${study.studyParameterConfig.dateOfEnrollmentForStudyRequired == 'yes'}" />
	</c:if>

	<form action="UpdateStudySubject" method="post">
		<input type="hidden" name="action" value="confirm"> 
		<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
        <input type="hidden" name="formWithStateFlag" id="formWithStateFlag" value="${formWithStateFlag != null ? formWithStateFlag : ''}" />
		<c:choose>
			<c:when test="${userBean.techAdmin || userBean.sysAdmin || userRole.manageStudy || userRole.investigator 
    					|| (study.parentStudyId > 0 && userRole.clinicalResearchCoordinator)}">
				<div style="width: 550px">
					<div class="box_T">
						<div class="box_L">
							<div class="box_R">
								<div class="box_B">
									<div class="box_TL">
										<div class="box_TR">
											<div class="box_BL">
												<div class="box_BR">
													<br>
													<div class="tablebox_center">
														<table border="0" cellpadding="0" cellspacing="0">
															<tr valign="top">
																<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel" />
																<c:if test="${study ne null}">
																	<c:set var="studySubjectIdLabel"
																			value="${study.studyParameterConfig.studySubjectIdLabel}" />
																</c:if>
																<td class="formlabel">${studySubjectIdLabel}:</td>

																<td>
																	<div class="formfieldXL_BG">
																		<input type="text" name="label" value="<c:out value="${studySub.label}"/>" class="formfieldXL"
																				onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');"
																				${study.studyParameterConfig.subjectIdGeneration eq 'auto-non-editable' ? 'readonly' : ''}>
																	</div> 
																	<br> 
																	<jsp:include page="../showMessage.jsp">
																		<jsp:param name="key" value="label" />
																	</jsp:include>
																	<jsp:include page="../showMessage.jsp">
																		<jsp:param name="key" value="personId" />
																	</jsp:include>
																</td>
																
																<td><span class="alert">* </span><fmt:message key="indicates_required_field" bundle="${resword}" /></td>
															</tr>

															<c:if test="${secondaryIdShow}">
																<tr valign="top">
																	<td class="formlabel">${secondaryIdLabel}:</td>
																	
																	<td>
																		<div class="formfieldXL_BG">
																			<input type="text" name="secondaryLabel" value="<c:out value="${studySub.secondaryLabel}"/>" class="formfieldXL"
																					onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
																		</div>
																	</td>
																	<c:if test="${secondaryIdRequired}">
																		<td>
																			<span class="formlabel alert">*</span>
											                            </td>
											                        </c:if>
																</tr>
																<tr>
																	<td></td><td colspan="2"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="secondaryLabel"/></jsp:include></td>
																</tr>
															</c:if>

															<c:if test="${enrollmentDateShow}">
																<tr valign="top">
																	<td class="formlabel">${enrollmentDateLabel}:</td>
																	
																	<td>
																		<div class="formfieldXL_BG">
																			<cc-fmt:formatDate value="${studySub.enrollmentDate}" dateTimeZone="${userBean.userTimeZoneId}" var="enrollDateStr"/>
																			<input type="text" name="enrollmentDate" value="<c:out value="${enrollDateStr}" />" class="formfieldXL" id="enrollmentDateField"
																					onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
																		</div> 
																		<br>
																		<jsp:include page="../showMessage.jsp">
																			<jsp:param name="key" value="enrollmentDate" />
																		</jsp:include>
																	</td>
																	
																	<td valign="top">
																		<c:if test="${enrollmentDateRequired}">
											                                <span class="formlabel alert">*</span>
											                            </c:if>
																		<ui:calendarIcon onClickSelector="'#enrollmentDateField'"/>
																		<c:if test="${study.studyParameterConfig.discrepancyManagement=='true'}">
																			<c:choose>
																				<c:when test="${hasNotes}">
																					<a href="#" onClick="openDNWindow('ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date',
																							'spanAlert-enrollmentDate', '', event); return false;">
																						<img id="flag_enrollmentDate" name="flag_enrollmentDate" src="${enrollmentNoteStatus.iconFilePath}"
																								border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
																								title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" />
																					</a>
																				</c:when>
																				<c:otherwise>
																					<a href="#" onClick="openDNWindow('CreateDiscrepancyNote?stSubjectId=${studySub.id}&id=<c:out value="${studySub.id}"/>&writeToDB=1&name=studySub&field=enrollmentDate&column=enrollment_date',
																							'spanAlert-enrollmentDate', '', event); return false;">
																						<img id="flag_enrollmentDate" name="flag_enrollmentDate" src="images/icon_noNote.gif" border="0"
																								alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>"
																								title="<fmt:message key="discrepancy_note" bundle="${resword}"/>" />
																						<input type="hidden"
																								value="ViewDiscrepancyNote?writeToDB=1&stSubjectId=${studySub.id}&id=${studySub.id}&name=studySub&field=enrollmentDate&column=enrollment_date" />
																					</a>
																				</c:otherwise>
																			</c:choose>
																		</c:if>
																	</td>
																</tr>
															</c:if>

														</table>
													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div style="width: 300px">
					<div class="box_T">
						<div class="box_L">
							<div class="box_R">
								<div class="box_B">
									<div class="box_TL">
										<div class="box_TR">
											<div class="box_BL">
												<div class="box_BR">
													<div class="tablebox_center">
														<table border="0" cellpadding="0" cellspacing="0"
															width="100%">
															<tr valign="top">
																<td class="table_header_column"><fmt:message key="label" bundle="${resword}" />:</td>
																
																<td class="table_cell">
																	<input type="text" name="label" value="<c:out value="${studySub.label}"/>"
																			disabled="disabled" class="formfieldM">
																</td>
															</tr>
															<c:if test="${secondaryIdShow}">
																<tr valign="top">
																	<td class="table_header_column">${secondaryIdLabel}:</td>
																	
																	<td class="table_cell">
																		<input type="text" name="secondaryLabel"
																				value="<c:out value="${studySub.secondaryLabel}"/>"
																				disabled="disabled" class="formfieldM">
																	</td>
																</tr>
															</c:if>
															<c:if test="${enrollmentDateShow}">
																<tr valign="top">
																	<td class="table_header_column">${enrollmentDateLabel}:</td>
																	
																	<td class="table_cell">
																		<cc-fmt:formatDate value="${studySub.enrollmentDate}" dateTimeZone="${userBean.userTimeZoneId}" var="enrollDateStr"/>
																		<input type="text" name="enrollmentDate"
																				value="<c:out value="${enrollDateStr}" />"
																				disabled="disabled" class="formfieldM"
																				id="enrollmentDateField">
																	</td>
																</tr>
															</c:if>
														</table>

													</div>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>

		<br>
		<c:if test="${(!empty groups)||(!empty dynamicGroups)}">
			<br>
			<div style="width: 550px">
				<div class="box_T">
					<div class="box_L">
						<div class="box_R">
							<div class="box_B">
								<div class="box_TL">
									<div class="box_TR">
										<div class="box_BL">
											<div class="box_BR">
												<div class="textbox_center">
													<table border="0" cellpadding="0">
														<c:if test="${!empty dynamicGroups}">
															<tr valign="top">
																<td class="formlabel"><fmt:message key="dynamic_group_class" bundle="${resword}" />:</td>
																
																<td class="table_cell">
																	<table border="0" cellpadding="0">
																		<tr>
																			<td>&nbsp;</td>
																		</tr>
																		<tr valign="top">
																			<td>
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																				&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
																			</td>
																					
																			<td>
																				<div class="formfieldM_BG">
																					<c:set var="selectable" value="${study.studyParameterConfig.allowDynamicGroupsManagement == 'yes' ? '' : 'disabled'}"/> 
																					<select name="dynamicGroupClassId" class="formfieldM"
																						onChange="setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. '); 
																								showDynamicEventsSection(${defaultDynGroupClassId});"  ${selectable}>
																						<c:forEach var="dynGroup" items="${dynamicGroups}">
																							<c:choose>
																								<c:when
																									test="${dynGroup.id == defaultDynGroupClassId}">
																									<option value="${dynGroup.id}">
																										<fmt:message key="default_group" bundle="${resword}" />
																									</option>
																								</c:when>
																								<c:otherwise>
																									<c:choose>
																										<c:when test="${dynGroup.id == selectedDynGroupClassId}">
																											<option value="<c:out value="${dynGroup.id}" />" selected>
																												<c:out value="${dynGroup.name}" />
																											</option>
																										</c:when>
																										<c:otherwise>
																											<option value="<c:out value="${dynGroup.id}"/>">
																												<c:out value="${dynGroup.name}" />
																											</option>
																										</c:otherwise>
																									</c:choose>
																								</c:otherwise>
																							</c:choose>
																						</c:forEach>
																					</select>
																				</div>
																			</td>
																		</tr>
																		<tr style="display: none" id="defaultDynGroupName">
																			<td>&nbsp;<fmt:message key="name" bundle="${resword}" />:&nbsp; </td>
																			
																			<c:choose>
																				<c:when test="${defaultDynGroupClassName != ''}">
																					<td>&nbsp;&nbsp;<c:out value="${defaultDynGroupClassName}" /></td>
																				</c:when>
																				<c:otherwise>
																					<td>&nbsp;<fmt:message key="none" bundle="${resword}" />&nbsp; </td>
																				</c:otherwise>
																			</c:choose>
																		</tr>
																		<c:forEach var="dynGroup" items="${dynamicGroups}">
																			<tr style="display: none" id="dynamicGroupId${dynGroup.id}">
																				<td>&nbsp;<fmt:message key="events" bundle="${resword}" />:&nbsp; </td>
																				
																				<td>
																					<table width="75%" cellspacing="0" cellpadding="0" border="1">
																						<c:forEach var="studyEvDef" items="${dynGroup.eventDefinitions}">
																							<tr>
																								<td class="table_cell">&nbsp;&nbsp;<c:out value="${studyEvDef.name}" /></td>
																							</tr>
																						</c:forEach>
																					</table>
																				</td>
																			</tr>
																		</c:forEach>
																		<tr>
																			<td>&nbsp;</td>
																		</tr>
																	</table>
																</td>
															</tr>
														</c:if>
														<c:if test="${!empty groups}">
															<tr valign="top">
																<td class="formlabel"><fmt:message key="subject_group_class" bundle="${resword}" />:</td>
																
																<td class="table_cell"><c:set var="count" value="0" />
																	<table border="0" cellpadding="0">
																		<c:forEach var="group" items="${groups}">
																			<tr valign="top">
																				<td><b><c:out value="${group.name}" /></b></td>
																				
																				<td>
																					<div class="formfieldM_BG">
																						<select name="studyGroupId<c:out value="${count}"/>" class="formfieldM"
																								onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 
																								'Data has been entered, but not saved. ');">
																							<c:if test="${group.subjectAssignment=='Optional'}">
																								<option value="0">--</option>
																							</c:if>
																							<c:forEach var="sg" items="${group.studyGroups}">
																								<c:choose>
																									<c:when test="${group.studyGroupId == sg.id}">
																										<option value="<c:out value="${sg.id}" />"	selected>
																											<c:out value="${sg.name}" />
																										</option>
																									</c:when>
																									<c:otherwise>
																										<option value="<c:out value="${sg.id}"/>">
																											<c:out value="${sg.name}" />
																										</option>
																									</c:otherwise>
																								</c:choose>
																							</c:forEach>
																						</select>
																					</div> 
																					<c:if test="${group.subjectAssignment=='Required'}">
																						<td align="left" class="alert">*</td>
																					</c:if>
																				</td>
																			</tr>
																			<tr valign="top">
																				<td><fmt:message key="notes" bundle="${resword}" />:</td>
																				
																				<td>
																					<div class="formfieldXL_BG">
																						<input type="text" class="formfieldXL" name="notes<c:out value="${count}"/>"
																							value="<c:out value="${group.groupNotes}"/>"
																							onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 
																							'Data has been entered, but not saved. ');">
																					</div> 
																					<c:import url="../showMessage.jsp">
																						<c:param name="key" value="notes${count}" />
																					</c:import>
																				</td>
																			</tr>
																			<c:set var="count" value="${count+1}" />
																		</c:forEach>
																	</table>
																</td>
															</tr>
														</c:if>
													</table>
												</div>
											</div>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
				</div>
			</div>
		</c:if>
		<br> 
		<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back"
			onClick="formWithStateGoBackSmart('<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');" />

		<input type="submit" name="BTN_Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium medium_continue"> 
		<img src="images/icon_UnchangedData.gif" style="visibility: hidden" title="You have not changed any data in this CRF section."
				alt="Data Status" name="DataStatus_bottom">
	</form>
	<div id="testdiv1" style="position: absolute; visibility: hidden; background-color: white; layer-background-color: white;"></div>

</body>

<jsp:include page="../include/footer.jsp" />
