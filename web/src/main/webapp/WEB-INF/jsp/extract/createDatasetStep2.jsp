<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/extract-header.jsp"/>

<script language="JavaScript">
<!--

function selectAll() {
    if (document.cl.all.checked) {
      for (var i=0; i <document.cl.elements.length; i++) {
        if (document.cl.elements[i].name.indexOf('itemSelected') != -1) {
            document.cl.elements[i].checked = true;
        }
      }
    } else {
      for (var i=0; i <document.cl.elements.length; i++) {
        if (document.cl.elements[i].name.indexOf('itemSelected') != -1) {
            document.cl.elements[i].checked = false;
        }
      }
    }
}
function notSelectAll() {
    if (!this.checked){
        document.cl.all.checked = false;
    }

}
//-->
</script>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->

<jsp:include page="../include/sideAlert.jsp"/>
<tr id="sidebar_Instructions_open" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
		<div class="sidebar_tab_content"></div>
	</td>
</tr>
<tr id="sidebar_Instructions_closed" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
		<b><fmt:message key="instructions" bundle="${resword}"/></b>
	</td>
</tr>
<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope="request" id="eventlist" class="java.util.HashMap"/>

<c:choose>
	<c:when test="${newDataset.id>0}">
		<h1>
			<span class="first_level_header">
				<fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="select_items" bundle="${resword}"/>
				<c:choose>
					<c:when test="${newDataset.id<=0}">
						<a href="javascript:openDocWindow('help/4_2_createDataset_Help.html#step1')">
						<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
					</c:when>
					<c:otherwise>
						<a href="javascript:openDocWindow('help/4_7_editDataset_Help.html#step1')">
						<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>"></a>
					</c:otherwise>
				</c:choose>
				: <c:out value="${newDataset.name}"/>
			</span>
		</h1>
	</c:when>
	<c:otherwise>
		<h1>
			<span class="first_level_header">
				<fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="select_items" bundle="${resword}"/>
				<a href="javascript:openDocWindow('help/4_2_createDataset_Help.html#step1')">
					<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
				</a>
			</span>
		</h1>
	</c:otherwise>
</c:choose>

<c:choose>
	<c:when test="${empty newDataset.itemDefCrf}">
		<p><fmt:message key="please_select_one_CRF_from_the" bundle="${restext}"/> <b><fmt:message key="left_side_info_panel" bundle="${restext}"/></b><fmt:message key="select_items_in_CRF_include_dataset" bundle="${restext}"/></p>
		<p><fmt:message key="click_event_subject_attributes_specify" bundle="${restext}"/></p>
	</c:when>
	<c:otherwise>
		<p><fmt:message key="can_view_items_selected_inclusion" bundle="${restext}"/><fmt:message key="select_all_items_inclusion_clicking" bundle="${restext}"/></p>
	</c:otherwise>
</c:choose>

<table border="0" cellpadding="0" cellspacing="0">
	<tr>
		<td><img src="images/arrow_left.gif" alt="<fmt:message key="select_CRF_on_the_left" bundle="${restext}"/>" title="<fmt:message key="select_CRF_on_the_left" bundle="${restext}"/>"></td>
		<td>
			<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center" align="center">
			<span class="title_extract">
				 <b><fmt:message key="use_task_pane_to_select_CRF" bundle="${restext}"/></b>
			</span>
			</div>
			</div></div></div></div></div></div></div></div>
		</td>
	</tr>
</table>

<c:if test="${crf != null && crf.id>0}">
	<div style="width: 600px">
	<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
	<div class="tablebox_center" align="center">
	
	<table class="table_vertical" width="100%">
		<tr>
			<td style="width:120px">
				<fmt:message key="event_name" bundle="${resword}" />:</td>
			<td>
				<c:out value="${definition.name}" />
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="CRF_name" bundle="${resword}" />:</td>
			<td>
				<c:out value="${crf.name}" />
			</td>
		</tr>
		<tr>
			<td>
				<fmt:message key="description" bundle="${resword}" />:</td>
			<td>
				<c:out value="${crf.description}" />
			</td>
		</tr>
	</table>
	
	</div>
	</div></div></div></div></div></div></div></div>
	</div>
</c:if>
<br>

<form id="datasetForm" action="CreateDataset" method="post" name="cl">

	<input type="hidden" name="action" value="beginsubmit"/>
	<c:if test="${!empty allCrfItems}">
		<input type="hidden" name="crfId" value="<c:out value="${crf.id}"/>">
		<input type="hidden" name="defId" value="<c:out value="${definition.id}"/>">

		<jsp:include page="../showMessage.jsp"><jsp:param name="key" value="all"/></jsp:include>

		<table border="0" cellpadding="0" cellspacing="0" >
			<tr>
				<td><input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_begin');"/></td>
				<td><input type="submit" id="btnSubmit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
				<td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
				<td><input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium medium_cancel"/></td>
			</tr>
			<tr>
				<td colspan="4" style="padding-top: 20px;"><input type="checkbox" name="all" value="1" onClick="javascript:selectAll();"> <fmt:message key="select_all_items" bundle="${resword}"/>&nbsp;&nbsp;&nbsp;</td>
			</tr>
		</table>
		<br/>

		<P><B><fmt:message key="show_items_this_dataset" bundle="${restext}"/></b></p>

		<!--javascript to select all-->
		<div style="width: 100%">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
		<div class="tablebox_center" align="center">

		<table class="table_horizontal" width="100%">
			<tr>
				<td>&nbsp;</td>
				<td><fmt:message key="name" bundle="${resword}"/></td>
				<td><fmt:message key="description" bundle="${resword}"/></td>
				<td><fmt:message key="version2" bundle="${resword}"/></td>
				<td><fmt:message key="section_s" bundle="${resword}"/></td>
				<td><fmt:message key="group_s" bundle="${resword}"/></td>
				<td><fmt:message key="data_type" bundle="${resword}"/></td>
				<td><fmt:message key="units" bundle="${resword}"/></td>
				<td><fmt:message key="response_type" bundle="${resword}"/></td>
				<td><fmt:message key="response_label" bundle="${resword}"/></td>
				<td><fmt:message key="PHI" bundle="${resword}"/></td>
				<td><fmt:message key="required" bundle="${resword}"/>?</td>
				<td><fmt:message key="validation_label" bundle="${resword}"/></td>
				<td><fmt:message key="default_value" bundle="${resword}"/></td>
				<td><fmt:message key="max_repeats" bundle="${resword}"/></td>
			</tr>

			<c:set var="count" value="0"/>
			<c:forEach var='item' items='${allCrfItems}'>

			<tr>
				<td>
					<c:choose>
						<c:when test="${item.selected}">
							<input type="checkbox" name="itemSelected<c:out value="${count}"/>" checked value="yes" onclick="javascript:notSelectAll();">
						</c:when>
						<c:otherwise>
							<input type="checkbox" name="itemSelected<c:out value="${count}"/>" value="yes" onclick="javascript:notSelectAll();">
						</c:otherwise>
					</c:choose>
				</td>

				<td><a href="javascript: openDocWindow('ViewItemDetail?itemId=<c:out value="${item.id}"/>')"><c:out value="${item.name}"/></a></td>
				<td><c:out value="${item.description}"/>&nbsp;</td>
				<td><c:out value="${item.itemMeta.crfVersionName}"/>&nbsp;</td>

				<%-- SECTION --%>
				<td><c:out value="${item.itemMeta.sectionName}"/>&nbsp;</td>

				<%-- GROUP --%>
				<td><c:out value="${item.itemMeta.groupLabel}" default=" "/>&nbsp;</td>

				<%-- DATA TYPE --%>
				<td><c:out value="${item.dataType.name}"/>&nbsp;</td>

				<%-- UNITS --%>
				<td><c:out value="${item.units}"/>&nbsp;</td>

				<%-- RESPONSE TYPE --%>
				<td><c:out value="${item.itemMeta.responseSet.responseType.name}"/>&nbsp;</td>

				<%-- response set label --%>
				<td><c:out value="${item.itemMeta.responseSet.label}"/>&nbsp;</td>

				<%-- PHI --%>
				<td>
					<c:choose>
						<c:when test="${item.phiStatus}">
							<fmt:message key="yes" bundle="${resword}"/>
						</c:when>
						<c:otherwise>
							<fmt:message key="no" bundle="${resword}"/>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>

				<%-- REQUIRED --%>
				<td>
					<c:choose>
						<c:when test="${item.itemMeta.required}">
							<fmt:message key="yes" bundle="${resword}"/>
						</c:when>
						<c:otherwise>
							<fmt:message key="no" bundle="${resword}"/>
						</c:otherwise>
					</c:choose>
					&nbsp;
				</td>

				<%-- VALIDATION --%>
				<td><c:out value="${item.itemMeta.regexp}" default=" "/>&nbsp;</td>

				<%-- DEFAULT VALUE --%>
				<td><c:out value="${item.itemMeta.defaultValue}" default=" "/>&nbsp;</td>

				<%-- MAX REPEATS --%>
				<td><c:out value="${item.itemMeta.repeatMax}"/>&nbsp;</td>
			</tr>

			<c:set var="count" value="${count+1}"/>
		</c:forEach>
		</table>

		</div>
		</div></div></div></div></div></div></div></div>
		</div>

		<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td><input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_begin');"/></td>
				<td><input type="submit" id="btnSubmit" name="save" value="<fmt:message key="save_and_add_more_items" bundle="${resword}"/>" class="button_xlong"/></td>
				<td><input type="submit" name="saveContinue" value="<fmt:message key="save_and_define_scope" bundle="${resword}"/>" class="button_xlong"/></td>
				<td><input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium medium_cancel"/></td>
			</tr>
		</table>
	</c:if>

	<c:import url="../include/workflow.jsp">
		<c:param name="module" value="extract"/>
	</c:import>
	
	<c:if test="${empty allCrfItems}">
		<table border="0" cellpadding="0" cellspacing="0" style="padding-top: 20px;">
			<tr>
				<td>
					<input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_begin');"/></td>
				<td>
					<input type="button" onclick="confirmCancel('ViewDatasets');"  name="cancel" value="   <fmt:message key="cancel" bundle="${resword}"/>   " class="button_medium medium_cancel"/></td>
				<td>
					<input type="submit" id="btnSubmit" value="<fmt:message key="submit" bundle="${resword}"/>" style="display: none;"/></td>
			</tr>
		</table>
	</c:if>

</form>

<jsp:include page="../include/footer.jsp"/>