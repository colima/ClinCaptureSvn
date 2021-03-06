<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="resnote"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterms"/>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<jsp:include page="../include/managestudy-header.jsp"/>
<jsp:include page="/includes/js/pages/update_event_definition.js.jsp" />

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="A_study_event_definition_describes_a_type_of_study_event"  bundle="${resword}"/> <fmt:message key="please_consult_the_ODM"  bundle="${resword}"/>
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
<jsp:useBean scope='session' id='definition' class='org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean'/>
<jsp:useBean scope='session' id='eventDefinitionCRFs' class='java.util.ArrayList'/>
<script type="text/JavaScript" language="JavaScript" src="includes/js/pages/study_event_definition.js"></script>

<h1>
	<span class="first_level_header">
		<fmt:message key="update_SED" bundle="${resword}"/>
	</span>
</h1>
<ol>
	<fmt:message key="list_create_SED_for"  bundle="${resword}"/>
</ol>

<P><span class="alert">* </span> <fmt:message key="indicates_required_field" bundle="${resword}"/></P>

<form action="UpdateEventDefinition" method="post" name="UpdateEventDefinition" id="updateEventDefinition">
	<input type="hidden" name="formWithStateFlag" id="formWithStateFlag" value="${formWithStateFlag != null ? formWithStateFlag : ''}" />
	<input type="hidden" name="action" value="confirm">
	<input type="hidden" name="edcToConfigure" value="">
	<input type="hidden" name="propagateChange" value="${propagateChange}" id="propagateChange">
	<div style="width: 600px">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="textbox_center">

		<table border="0" cellpadding="0" cellspacing="0">
			<tr valign="top">
				<td class="formlabel"><fmt:message key="name" bundle="${resword}"/>:</td>
				<td>
					<table>
						<tr><td>
							<div class="formfieldXL_BG"><input type="text" name="name" onchange="javascript:changeIcon();" value="<c:out value="${definition.name}"/>" class="formfieldXL"></div>
						</td><td class="formlabel alert">*</td>
						<td><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="name"/></jsp:include></td></tr>
					</table>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel"><fmt:message key="description" bundle="${resword}"/>:</td><td>
					<div class="formtextareaXL4_BG">
						<textarea onchange="javascript:changeIcon();" class="formtextareaXL4" name="description" rows="4" cols="50"><c:out value="${definition.description}"/></textarea>
					</div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="description"/></jsp:include>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel"><fmt:message key="type" bundle="${resword}"/>:</td>
				<td>
					<div class="formfieldXL_BG">
						<select name="type" onchange="javascript:changeIcon();" class="formfieldXL">
							<c:choose>
							<c:when test="${definition.type == 'common'}">
								<c:set var="common_selected" value="selected"/>
							</c:when>
							<c:when test="${definition.type == 'unscheduled'}">
								<c:set var="unscheduled_selected" value="selected"/>
							</c:when>
							<c:when test="${definition.type == 'calendared_visit'}">
								<c:set var="calendared_selected" value="selected"/>
							</c:when>
							<c:when test="${definition.type == 'scheduled'}">
								<c:set var="scheduled_selected" value="selected"/>
							</c:when>
							</c:choose>
							<option value="scheduled" ${scheduled_selected}><fmt:message key="scheduled" bundle="${resword}"/>
							<option value="unscheduled" ${unscheduled_selected}><fmt:message key="unscheduled" bundle="${resword}"/>
							<option value="common" ${common_selected}><fmt:message key="common" bundle="${resword}"/>
							<c:if test="${study.studyParameterConfig.calendaredVisits != 'no' || definition.type == 'calendared_visit'}">
								<option value="calendared_visit" ${calendared_selected}><fmt:message key="calendared_visit" bundle="${resword}"/>
							</c:if>
						</select>
					</div>
				</td>
			</tr>

			<tr valign="top" name="repeating" style="height: 25px;">
				<td align="right"><fmt:message key="repeating" bundle="${resword}"/>:</td>
				<td align="left">
					<c:choose>
						<c:when test="${definition.repeating == true}">
							<input type="radio" onchange="javascript:changeIcon();" checked name="repeating" value="true"><fmt:message key="yes" bundle="${resword}"/>
							<input type="radio" onchange="javascript:changeIcon();" name="repeating" value="false"><fmt:message key="no" bundle="${resword}"/>
						</c:when>
						<c:otherwise>
							<input type="radio" onchange="javascript:changeIcon();" name="repeating" value="true"><fmt:message key="yes" bundle="${resword}"/>
							<input type="radio" onchange="javascript:changeIcon();" checked name="repeating" value="false"><fmt:message key="no" bundle="${resword}"/>
						</c:otherwise>
					</c:choose>
				</td>
			</tr>

			<tr valign="top">
				<td class="formlabel"><fmt:message key="category" bundle="${resword}"/>:</td>
				<td>
					<div class="formfieldXL_BG"><input type="text" onchange="javascript:changeIcon();" name="category" value="<c:out value="${definition.category}"/>" class="formfieldXL"></div>
					<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="category"/></jsp:include>
				</td>
			</tr>

			<tr valign="top" class='showHide'>
				<td style="white-space: nowrap;"><fmt:message key="reference_event" bundle="${resword}"/>:</td>
				<td>
					<c:choose>
						<c:when test="${definition.referenceVisit == 'true'}">
							<input type="checkbox" value="true" checked name="isReference"/>
						</c:when>
						<c:otherwise>
							<input type="checkbox" value="true" name="isReference"/>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${referenceVisitAlreadyExist == 'true'}">
							<span class="alert"><fmt:message key="reference_visit_already_exist" bundle="${resword}"/></span>
						</c:when>
					</c:choose>
				</td>
			</tr>

			<tr valign="top" class='showHide'>
				<td class="formlabel" style="padding-top:10px"><fmt:message key="day_schedule" bundle="${resword}"/>:</td>
				<td>
					<table width=480px>
						<tr>
							<td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${definition.scheduleDay}"/>" type="text" size="3" name="schDay"/></div></td>
							<td style="padding-bottom:20px" class="alert">*</td>
							<td width="250px"><fmt:message key="after_the_reference_visit" bundle="${resword}"/></td>
							<td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="schDay"/></jsp:include></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr valign="top" class='showHide'>
				<td class="formlabel" style="padding-top:15px"><fmt:message key="day_max" bundle="${resword}"/>:</td>
				<td>
					<table width=480px>
						<tr>
							<td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${definition.maxDay}"/>" type="text" size="3" name="maxDay"/></div></td>
							<td style="padding-bottom:20px" class="alert">*</td><td width="250px"><fmt:message key="the_maximum_day_that_an_event" bundle="${resword}"/></td>
							<td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="maxDay"/></jsp:include></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr valign="top" class='showHide'>
				<td class="formlabel" style="padding-top:15px"><fmt:message key="day_min" bundle="${resword}"/>:</td>
				<td>
					<table width=480px>
						<tr>
							<td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${definition.minDay}"/>" type="text" size="3" name="minDay"/></div></td>
							<td style="padding-bottom:20px" class="alert">*</td>
							<td width="250px"><fmt:message key="the_minimum_day_that_an_event" bundle="${resword}"/></td>
							<td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="minDay"/></jsp:include></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr valign="top" class='showHide'>
				<td class="formlabel" style="padding-top:15px"><fmt:message key="day_email" bundle="${resword}"/>:</td>
				<td>
					<table width=480px>
						<tr>
							<td><div class="formfieldL_BG_cf"><input class="formfieldL_cf" value="<c:out value="${definition.emailDay}"/>" type="text" size="3" name="emailDay"/></div></td>
							<td style="padding-bottom:20px" class="alert">*</td>
							<td width="250px"><fmt:message key="the_day_a_reminder_email_is" bundle="${resword}"/></td>
							<td width=290px><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="emailDay"/></jsp:include></td>
						</tr>
					</table>
				</td>
			</tr>

			<tr valign="top" class='showHide' name='email'>
				<td class="formlabel"><fmt:message key="user_name" bundle="${resword}"/>:</td>
				<td>
					<table>
						<tr>
							<td><div class="formfieldXL_BG" ><input class="formfieldXL" value="<c:out value="${userNameInsteadEmail}"/>" type="text" size="3" name="emailUser"/></div></td>
							<td style="padding-bottom:20px" class="alert">*</td>
						</tr>
					</table>
				</td>
			</tr>

			<tr class='showHide' name='email'>
				<td>&nbsp</td>
				<td><fmt:message key="use_only_a_valid_user_name" bundle="${resword}"/>
			</tr>

			<tr class='showHide' name='email'>
				<td>&nbsp</td>
				<td width="250px"><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="emailUser"/></jsp:include></td>
			</tr>
		</table>

	</div>
	</div></div></div></div></div></div></div></div>
	</div>
	<br>

	<div class="table_title_manage"><fmt:message key="CRFs" bundle="${resword}"/></div>

	<div style="width: 600px">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="tablebox_center">

	<table border="0" cellpadding="0" cellspacing="0" width="100%">
		<c:set var="count" value="0"/>
		
		<c:forEach var="edc" items="${eventDefinitionCRFs}" varStatus="edcStatus">
			<%-- above added by tbh, 102007 --%>

			<input type="hidden" name="id<c:out value="${count}"/>" value="<c:out value="${edc.id}"/>">
			<input type="hidden" name="crfId<c:out value="${count}"/>" value="<c:out value="${edc.crfId}"/>">
			<c:set var="status" value="0"/>

			<c:if test="${edc.status.id==1}"> <c:set var="status" value="1"/> </c:if>
			<tr valign="top" bgcolor="#F5F5F5">
			    <td class="table_header_column" colspan="3"><c:out value="${edc.crfName}"/></td>
			    <td class="table_cell">
			        <table border="0" cellpadding="0" cellspacing="0">
			            <tr>
			                <c:choose>
			                    <c:when test="${status==1}">
			                        <td><a href="RemoveCRFFromDefinition?id=<c:out value="${edc.crfId}"/>"
			                               onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
			                               onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"><img
			                                name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
			                        </td>
			                    </c:when>
			                    <c:otherwise>
									<c:choose>
										<c:when test="${edc.crf.status.available}">
											<td><a href="RestoreCRFFromDefinition?id=<c:out value="${edc.crfId}"/>"
												   onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
												   onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"><img
													name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
											</td>
										</c:when>
										<c:otherwise>
											<img src="images/bt_Transparent.gif" border="0" align="left" hspace="6"/>
										</c:otherwise>
									</c:choose>
			                    </c:otherwise>
			                </c:choose>
							<c:if test="${edc.id != 0}">
							<td>
								<a href="pages/deleteEventDefinitionCRF?id=${edc.id}&edId=${definition.id}">
									<img name="bt_delete" class="bt_delete_${edcStatus.index}" src="images/bt_Delete.gif" border="0"
										 alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>"
										 align="left" hspace="6">
								</a>
							</td>
							</c:if>
			            </tr>
			        </table>
			    </td>
			</tr>

			<c:if test="${status==1}">
			<tr valign="top">
			    <td class="table_cell"><fmt:message key="required" bundle="${resword}"/>:
			        <c:choose>
			            <c:when test="${edc.requiredCRF == true}">
			                <input type="checkbox" onchange="javascript:changeIcon();" checked name="requiredCRF<c:out value="${count}"/>" value="yes">
			            </c:when>
			            <c:otherwise>
			                <input type="checkbox" onchange="javascript:changeIcon();" name="requiredCRF<c:out value="${count}"/>" value="yes">
			            </c:otherwise>
			        </c:choose>
			    </td>

			    <td class="table_cell">&nbsp;</td>

			    <td class="table_cell"><fmt:message key="password_required" bundle="${resword}"/>:
			        <c:choose>
			            <c:when test="${edc.electronicSignature == true}">
			                <input type="checkbox" onchange="javascript:changeIcon();" checked name="electronicSignature<c:out value="${count}"/>" value="yes">
			            </c:when>
			            <c:otherwise>
			                <input type="checkbox" onchange="javascript:changeIcon();" name="electronicSignature<c:out value="${count}"/>" value="yes">
			            </c:otherwise>
			        </c:choose>
			    </td>

			    <td class="table_cell" colspan="2"><fmt:message key="default_version" bundle="${resword}"/>:
			        <select name="defaultVersionId<c:out value="${count}"/>" onchange="javascript:changeIcon();">
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
				<td class="table_cell" colspan="2">
					<fmt:message key="hidden_crf" bundle="${resword}"/> :
					<c:choose>
						<c:when test="${! edc.hideCrf}">
							<input type="checkbox" onchange="javascript:changeIcon();" name="hideCRF<c:out value="${count}"/>" value="yes">
						</c:when>
						<c:otherwise>
							<input onchange="javascript:changeIcon();" checked="checked" type="checkbox" name="hideCRF<c:out value="${count}"/>" value="yes">
						</c:otherwise>
					</c:choose>
				</td>

				<td class="table_cell" colspan="2"><fmt:message key="sdv_option" bundle="${resword}"/>:
					<select onchange="javascript:changeIcon(); checkItemLevelSDV(this);" name="sdvOption<c:out value="${count}"/>" initial-value="${edcSDVMap[edc.id]}">
						<c:forEach var="sdv" items="${sdvStates}">
							<option value="${sdv.code}" ${edc.sourceDataVerification.code == sdv.code ? "selected" : ""}><fmt:message key="${sdv.description}" bundle="${resterms}"/></option>
						</c:forEach>
					</select>

					<c:if test="${edc.id != 0 && study.studyParameterConfig.itemLevelSDV == 'yes'}">
						<c:set var="display" value="${edc.sourceDataVerification.code != 2 ? 'none' : 'inline-block'}"/>
						<img name="bt_edit_edc_sdv" class="bt_edit_edc_sdv_${edcStatus.index}" src="images/bt_Edit.gif" border="0"
							 alt="<fmt:message key="configure_items_for_sdv" bundle="${resword}"/>"
							 title="<fmt:message key="configure_items_for_sdv" bundle="${resword}"/>"
							 onclick="configureItemLevelSDV(${edc.id});"
							 hspace="6" style="vertical-align: sub; cursor: pointer; display: ${display}"/>
					</c:if>
				</td>
			</tr>

			<tr valign="top">
				<td class="table_cell" colspan="4">
					<fmt:message key="acceptNewCrfVersions" bundle="${resword}"/> :
					<c:choose>
						<c:when test="${!edc.acceptNewCrfVersions}"><input type="checkbox" name="acceptNewCrfVersions<c:out value="${count}"/>" value="yes"></c:when>
						<c:otherwise><input checked="checked" type="checkbox" name="acceptNewCrfVersions<c:out value="${count}"/>" value="yes"></c:otherwise>
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
							<input type="radio" name="deQuality${count}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="dde" class="email_field_trigger uncheckable_radio" ${deQualityDE}/>
							<fmt:message key="double_data_entry" bundle="${resword}"/>

							<input type="radio" name="deQuality${count}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="evaluation" class="email_field_trigger uncheckable_radio" ${deQualityEvaluatedCRF}/>
							<fmt:message key="crf_data_evaluation" bundle="${resword}"/>
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="deQuality${count}" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="dde" class="email_field_trigger uncheckable_radio" ${deQualityDE}/>
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

					<input type="radio" name="emailOnStep<c:out value="${count}"/>" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="complete" class="email_field_trigger uncheckable_radio" ${emailStepComplete}/>
					<fmt:message key="completed" bundle="${resword}"/>

					<c:choose>
						<c:when test="${edc.emailStep eq 'sign'}">
							<c:set var="emailStepSign" value="checked"/>
						</c:when>
						<c:otherwise>
							<c:set var="emailStepSign" value=""/>
						</c:otherwise>
					</c:choose>
					
					<input type="radio" name="emailOnStep<c:out value="${count}"/>" onclick="javascript:showEmailField(this);" onchange="javascript:changeIcon();" value="sign" class="email_field_trigger uncheckable_radio" ${emailStepSign}/>
					<fmt:message key="Signed" bundle="${resword}"/>
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
						<input type="text" name="mailTo${count}" onchange="javascript:changeIcon();" style="width:160px;margin-left:15px" class="email_to_check_field" value="${edc.emailTo}"/>
					</span>
					<span class="alert" style="display:none"><fmt:message key="enter_valid_email" bundle="${resnote}"/></span>
				</td>
			</tr>

            <tr>
                <td class="table_cell" colspan="4">
                    <fmt:message key="crfTabbingMode" bundle="${resword}"/>:
                    <c:set var="leftToRightTabbingMode" value="checked"/>
                    <c:set var="topToBottomTabbingMode" value=""/>
                    <c:if test='${edc.tabbingMode == "topToBottom"}'>
                        <c:set var="leftToRightTabbingMode" value=""/>
                        <c:set var="topToBottomTabbingMode" value="checked"/>
                    </c:if>
                    <input type="radio" name="tabbingMode${count}" onchange="javascript:changeIcon();" value="leftToRight" ${leftToRightTabbingMode}/> <fmt:message key="leftToRight" bundle="${resword}"/>
                    <input type="radio" name="tabbingMode${count}" onchange="javascript:changeIcon();" value="topToBottom" ${topToBottomTabbingMode}/> <fmt:message key="topToBottom" bundle="${resword}"/>
                </td>
            </tr>

			<tr><td class="table_divider" colspan="4">&nbsp;</td></tr>
			</c:if>
			<c:set var="count" value="${count+1}"/>
		</c:forEach>
	</table>
	</div>
	</div></div></div></div></div></div></div></div>
	</div>
	<br>

	<table border="0" cellpadding="0" cellspacing="0">
		<tr>
			<td>
				<input id="GoBack" class="button_medium medium_back" type="button" name="BTN_Back" title="<fmt:message key="back" bundle="${resword}"/>" value="<fmt:message key="back" bundle="${resword}"/>" onclick="formWithStateGoBackSmart('<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
			</td>
			<td>
				<input type="submit" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium medium_continue"
					   onClick="if (${numberOfExistingSitesOnStudy > 0}) return showPropagateChangesDialog(${childEDCConfigurationIsSameAsParent}, this);">
			</td>
			<td>
				<c:set var="addNewCRFBTNCaption"><fmt:message key="add_a_new_CRF" bundle="${resword}"/></c:set>
				<input type="button" name="addNewCRFBTN" value="${addNewCRFBTNCaption}"
					   class="${ui:getHtmlButtonCssClass(addNewCRFBTNCaption, "")}"
					   onclick="updateEventDefinitionAddCRF()">
			</td>
			<td>
				<img src="images/icon_UnchangedData.gif" style="visibility:hidden" title="You have not changed any data in this page." alt="Data Status" name="DataStatus_bottom">
			</td>
		</tr>
	</table>
</form>
<br><br>

<!-- EXPANDING WORKFLOW BOX -->

<table border="0" cellpadding="0" cellspacing="0" style="position: relative; left: -14px;">
	<tr><td id="sidebar_Workflow_closed" style="display: none">
		<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');"><img src="images/<fmt:message key="image_dir" bundle="${resformat}"/>/tab_Workflow_closed.gif" border="0"></a>
	</td>

	<td id="sidebar_Workflow_open" style="display: all">
		<table border="0" cellpadding="0" cellspacing="0" class="workflowBox">
 			<tr>
				<td class="workflowBox_T" valign="top">

					<table border="0" cellpadding="0" cellspacing="0"><tr><td class="workflow_tab">
						<a href="javascript:leftnavExpand('sidebar_Workflow_closed'); leftnavExpand('sidebar_Workflow_open');">
							<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
						</a>
						<b><fmt:message key="workflow" bundle="${resword}"/></b>
					</td></tr></table>

				</td>
				<td class="workflowBox_T" align="right" valign="top"><img src="images/workflowBox_TR.gif"></td>
			</tr>

			<tr>
				<td colspan="2" class="workflowbox_B">
					<div class="box_R"><div class="box_B"><div class="box_BR">
 					<div class="workflowBox_center">

					<!-- Workflow items -->
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td>
								<!-- These DIVs define shaded box borders -->
								<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
								<div class="textbox_center" align="center">
									<span class="title_manage">
										<b><fmt:message key="enter_definition_name_and_description" bundle="${resword}"/><br><br></b>
									</span>
								</div>
								</div></div></div></div></div></div></div></div>
							</td>

							<td><img src="images/arrow.gif"></td>

							<td>
								<!-- These DIVs define shaded box borders -->
								<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
								<div class="textbox_center" align="center">
								<span class="title_manage">
									<fmt:message key="add_CRFs_to_definition" bundle="${resword}"/><br><br>
								</span>
								</div>
								</div></div></div></div></div></div></div></div>
							</td>

							<td><img src="images/arrow.gif"></td>

							<td>
								<!-- These DIVs define shaded box borders -->
								<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
								<div class="textbox_center" align="center">
								<span class="title_manage">
									<fmt:message key="edit_properties_for_each_CRF" bundle="${resword}"/><br><br>
								</span>
								</div>
								</div></div></div></div></div></div></div></div>
							</td>

							<td><img src="images/arrow.gif"></td>

							<td>
								<!-- These DIVs define shaded box borders -->
								<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
								<div class="textbox_center" align="center">
								<span class="title_manage">
									<fmt:message key="confirm_and_submit_definition" bundle="${resword}"/><br><br>
								</span>
								</div>
								</div></div></div></div></div></div></div></div>
							</td>
						</tr>
 					</table>
					<!-- end Workflow items -->

					</div>
					</div></div></div>
				</td>
			</tr>
		</table>
	</td></tr>
</table>

<!-- END WORKFLOW BOX -->
<jsp:include page="../include/footer.jsp"/>
