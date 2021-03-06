<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterms"/>

<c:choose>
	<c:when test="${userBean.sysAdmin && module=='admin'}">
		<c:import url="../include/admin-header.jsp"/>
	</c:when>
	<c:otherwise>
		<c:import url="../include/submit-header.jsp"/>
	</c:otherwise>
</c:choose>

<!-- *JSP* submit/import.jsp -->
<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
		<div class="sidebar_tab_content">
			<fmt:message key="import_side_bar_instructions" bundle="${restext}"/>
		</div>
	</td>
</tr>

<tr id="sidebar_Instructions_closed" style="display: none">
	<td class="sidebar_tab">
		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');">
			<img src="images/sidebar_expand.gif" border="0" align="right" hspace="10">
		</a>
		<b><fmt:message key="instructions" bundle="${restext}"/></b>
	</td>
</tr>

<jsp:include page="../include/sideInfo.jsp"/>
<jsp:useBean scope='session' id='version' class='org.akaza.openclinica.bean.submit.CRFVersionBean'/>
<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='crfName' class='java.lang.String'/>

 <c:out value="${crfName}"/>

<h1>
	<span class="first_level_header">
		<fmt:message key="import_crf_data" bundle="${resworkflow}"/>
		<a href="javascript:openDocWindow('help/2_6_importData_Help.html')">
		    <img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${restext}"/>" title="<fmt:message key="help" bundle="${restext}"/>">
		</a>
	</span>
</h1>

<p><fmt:message key="import_instructions" bundle="${restext}"/></p>

<form action="ImportCRFData?action=confirm&crfId=<c:out value="${version.crfId}"/>&name=<c:out value="${version.name}"/>" method="post" ENCTYPE="multipart/form-data">
	<div style="display: inline-block">
		<div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">
			<div class="textbox_center">
				<table border="0">				
					<tr>
						<td class="formlabel"><fmt:message key="xml_file_to_upload" bundle="${resterms}"/>:</td>
						<td>
							<input type="file" name="xml_file" id="xml_file_path" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
							<br><jsp:include page="../showMessage.jsp"><jsp:param name="key" value="xml_file"/></jsp:include>
						</td>
					</tr>
					<input type="hidden" name="crfId" value="<c:out value="${version.crfId}"/>">
				</table>				
			</div>
		</div></div></div></div></div></div></div></div>
	</div>
	
	<br clear="all">
	<input type="button" name="BTN_Smart_Back_A" id="GoToPreviousPage" 
						value="<fmt:message key="back" bundle="${resword}"/>" 
						class="button_medium medium_back" 
						onClick="javascript: checkGoBackSmartEntryStatus('DataStatus_bottom', '<fmt:message key="you_have_unsaved_data3" bundle="${resword}"/>', '${navigationURL}', '${defaultURL}');"/>
	<input type="submit" value="<fmt:message key="continue" bundle="${resword}"/>" onclick="return checkFileUpload('xml_file_path', '<fmt:message key="select_a_file_to_upload" bundle="${restext}"/>');" class="button_medium medium_continue" />
	<img src="images/icon_UnchangedData.gif" style="visibility:hidden" alt="Data Status" name="DataStatus_bottom">
</form>


<jsp:include page="../include/footer.jsp"/>