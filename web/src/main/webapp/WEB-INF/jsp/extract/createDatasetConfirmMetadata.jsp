<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<jsp:include page="../include/extract-header.jsp"/>

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

<jsp:include page="../include/createDatasetSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='newDataset' class='org.akaza.openclinica.bean.extract.DatasetBean'/>

<h1>
	<span class="first_level_header">
		<c:choose>
			<c:when test="${newDataset.id>0}">
				<fmt:message key="edit_dataset" bundle="${resword}"/> - <fmt:message key="confirm_dataset_properties" bundle="${resword}"/>: <c:out value='${newDataset.name}'/>
			</c:when>
			<c:otherwise>
				<fmt:message key="create_dataset" bundle="${resword}"/>: <fmt:message key="confirm_dataset_properties" bundle="${resword}"/>
			</c:otherwise>
		</c:choose>
	</span>
</h1>
<p><fmt:message key="confirm_dataset_properties" bundle="${restext}"/></p>

<form id="datasetForm" action="CreateDataset" method="post">
<input type="hidden" name="action" value="confirmall" />

<table>
	<tr>
		<td class="text"><fmt:message key="name" bundle="${resword}"/></td>
		<td class="text"><b><c:out value="${newDataset.name}" /></b>
	</tr>
	<tr>
		<td class="text" valign="top"><fmt:message key="description" bundle="${resword}"/></td>
		<td class="text" valign="top"><b><c:out value="${newDataset.description}" /></b></td>
	</tr>
</table>
<table>
	<tr>
		<td colspan="3" align="left">
          <input type="button" name="BTN_Back" id="PreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" size="50" onclick="datasetConfirmBack('<fmt:message key="you_have_unsaved_data2" bundle="${resword}"/>', 'datasetForm', 'CreateDataset', 'back_to_scopesubmit');"/></td>
		<td>
		  <input type="submit" id="btnSubmit" name="btnSubmit" title="<fmt:message key="create_new_dataset" bundle="${resword}"/>" value="<fmt:message key="submit_for_dataset" bundle="${resword}"/>" class="button_medium medium_submit"/></td>
		<td>
		  <input type="button" onclick="confirmCancel('ViewDatasets');" name="cancel" value="<fmt:message key="cancel" bundle="${resword}"/>" class="button_medium medium_cancel"/></td>
	</tr>
</table>

</form>

<c:import url="../include/workflow.jsp">
   <c:param name="module" value="extract"/>
</c:import>
<jsp:include page="../include/footer.jsp"/>
