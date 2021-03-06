<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<c:choose>
<c:when test="${userBean.sysAdmin && module=='admin'}">
 <c:import url="../include/admin-header.jsp"/>
</c:when>
<c:otherwise>
 <c:import url="../include/managestudy-header.jsp"/>
</c:otherwise>
</c:choose>

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>
<!-- *JSP* submit/listRuleSets2.jsp -->
<link type="text/css" href="includes/jmesa/jmesa.css?r=${revisionNumber}"  rel="stylesheet">

<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery.jmesa.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jmesa.js?r=${revisionNumber}"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery.blockUI.js?r=${revisionNumber}"></script>
<script type="text/javascript" language="JavaScript" src="includes/jmesa/jquery-ui-1.8.2.custom.min.js"></script>

<script type="text/javascript">
    function onInvokeAction(id,action) {
        if(id.indexOf('ruleAssignments') == -1)  {
        setExportToLimit(id, '');
        }
        createHiddenInputFieldsForLimitAndSubmit(id);
    }
    function onInvokeExportAction(id) {
        var parameterString = createParameterStringForLimit(id);
        location.href = '${pageContext.request.contextPath}/ViewRuleAssignment?'+ parameterString;
    }
    jQuery(window).load(function(){

    	highlightLastAccessedObject();
    });
</script>


<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: all">
        <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>
        <b><fmt:message key="instructions" bundle="${restext}"/></b>
        <div class="sidebar_tab_content">
        <fmt:message key="manage_execute_rule_assignments" bundle="${restext}"/>
        </div>
        </td>
    </tr>
    <tr id="sidebar_Instructions_closed" style="display: none">
        <td class="sidebar_tab">
        <a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>
        <b><fmt:message key="instructions" bundle="${restext}"/></b>
        </td>
  </tr>
<jsp:include page="../include/viewRuleAssignmentSideInfo.jsp"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean'/>
<jsp:useBean scope='request' id='table' class='org.akaza.openclinica.web.domain.EntityBeanTable'/>


<h1>
	<span class="first_level_header">
		<fmt:message key="rule_manage_rule_assignment" bundle="${resworkflow}"/> <c:out value="${study.name}" /> 
		<a href="javascript:openDocWindow('help/3_3_rules_Help.html')">
			<img src="images/bt_Help_Manage.gif" border="0" alt="<fmt:message key="help" bundle="${resword}"/>" title="<fmt:message key="help" bundle="${resword}"/>">
		</a>
	</span>
</h1>



<div id="ruleAssignmentsDiv">
    <form  action="${pageContext.request.contextPath}/ViewRuleAssignment">
        <input type="hidden" name="module" value="admin">
        ${ruleAssignmentsHtml}
    </form>
</div>

<script>
	$("img[title*='PDF']").attr('title', '<fmt:message key="view_rules_download_xml" bundle="${resword}"/>' ).attr('src', 'images/table/csv.gif');
</script>
<br>
	<input type="button" name="BTN_Smart_Back" id="GoToPreviousPage" value="<fmt:message key="back" bundle="${resword}"/>" class="button_medium medium_back" onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />

	<c:set var="createRuleBTNCaption"><fmt:message key="create_rule" bundle="${resword}"/></c:set>
	<input type="button" name="createRule" value="${createRuleBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(createRuleBTNCaption, "")}"
		   onClick="window.location.href='designer/rule.jsp';"/>

	<c:set var="importRuleBTNCaption"><fmt:message key="import_rules" bundle="${resword}"/></c:set>
    <input type="button" name="ImportRule" value="${importRuleBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(importRuleBTNCaption, "")}"
		   onClick="window.location.href='ImportRule';"/>

	<c:set var="testRuleBTNCaption"><fmt:message key="test_rules_title" bundle="${resword}"/></c:set>
	<input type="button" name="TestRule" value="${testRuleBTNCaption}"
		   class="${ui:getHtmlButtonCssClass(testRuleBTNCaption, "")}"
		   onClick="window.location.href='TestRule';"/>
<br>

<br>
<input id="accessAttributeName" type="hidden" value="data-cc-ruleId">
<jsp:include page="../include/footer.jsp"/>
