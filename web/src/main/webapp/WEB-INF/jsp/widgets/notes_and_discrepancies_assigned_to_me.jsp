<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>

<script>
	<c:import url="../../includes/js/widgets/w_nds_assigned_to_me.js?r=${revisionNumber}" />
</script>

<div class="dns_assigned_to_me" align="center">
	<h2><fmt:message key="nds_assigned_to_me_widget_header" bundle="${resword}"/></h2>
	<div class="chart_wrapper" align="left">
		<ul class="stacked_bar">
			<a href="#"><li class="new stack">
					<div class="pop-up"></div>
			</li></a>
			<a href="#"><li class="updated stack">
					<div class="pop-up"></div>
			</li></a>
			<a href="#" class="optional"><li class="resolution_proposed stack">
					<div class="pop-up"></div>
			</li></a>
			<a href="#"><li class="closed stack">
					<div class="pop-up"></div>
			</li></a>
		</ul>
	</div>
	<table class="captions">
		<tr>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
			<td></td>
		</tr>
	</table>
	<table class="signs">
		<tr align="left">
			<td>
				<div class="popup_legend_min"></div>
				<div class="new sign">&nbsp;</div> - <fmt:message key="new" bundle="${resword}"/></td>
			<td>
				<div class="popup_legend_min"></div>
				<div class="updated sign">&nbsp;</div> - <fmt:message key="updated" bundle="${resword}"/></td>
		</tr>
		<tr align="left">
			<td class="optional">
				<div class="popup_legend_min"></div>
				<div class="resolution_proposed sign">&nbsp;</div> - <fmt:message key="Resolution_Proposed" bundle="${resword}"/></td>
			<td>
				<div class="popup_legend_min"></div>
				<div class="closed sign">&nbsp;</div> - <fmt:message key="closed" bundle="${resword}"/></td>
		</tr>
	</table>
	<form class="hidden" id="ndsWidgetForm">
		<input type="hidden" id="cUser" value="${userBean.name}" />
		<input type="hidden" class="status" value="<fmt:message key='new' bundle='${resword}'/>" />
		<input type="hidden" class="status" value="<fmt:message key='updated' bundle='${resword}'/>" />
		<input type="hidden" class="status" value="<fmt:message key='Resolution_Proposed' bundle='${resword}'/>" />
		<input type="hidden" class="status" value="<fmt:message key='Closed' bundle='${resterm}'/>" />
	</form>
</div>
