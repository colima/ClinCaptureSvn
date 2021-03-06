<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<ui:setBundle basename="org.akaza.openclinica.i18n.terms" var="resterm"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<form id="nds_per_crf_form" class="hidden">
<c:forEach items="${ndsCrfDataColumns}" var="column" varStatus="ndsPerCrfIndex">
	<input type="text" new="${column.value[0]}" updated="${column.value[1]}" closed="${column.value[2]}" not_applicable="${column.value[3]}" value="${column.key}"/>
	<c:set var="ndsRowsCount" value="${ndsPerCrfIndex.count}"/>
</c:forEach>
<c:forEach var="i" begin="${ndsRowsCount + 1}" end="8">
	<input type="text" new="0" updated="0" closed="0" not_applicable="0" value=""/>
</c:forEach>
	<input type="hidden" value="${ndsCrfStart}" id="nds_per_crf_start" />
	<input type="hidden" class="currentColor" value="${newThemeColor}">
	<input type="hidden" class="status legend" value="<fmt:message bundle='${resterm}' key='Closed'/>" />
	<input type="hidden" class="status legend" value="<fmt:message bundle='${resterm}' key='Updated'/>" />
	<input type="hidden" class="status legend" value="<fmt:message bundle='${resterm}' key='New'/>" />
	<input type="hidden" class="status " value="<fmt:message bundle='${resterm}' key='Not_Applicable'/>" />
	<input type="hidden" class="status legend" value="<fmt:message bundle='${resword}' key='w_legend_notes'/>" />
</form>

<div class="tc_column_var_wrapper" align="left" style="overflow:hidden">
	<div id="nds_per_crf_chart"></div>
	<table>
		<tr>
			<td align="left">
				<c:if test="${ndsCrfHasPrevious}">
					<input type="button" name="BTN_Back" id="previous" value="<fmt:message bundle='${resword}' key='previous' />" class="button_medium" onClick="javascript: initNdsPerCrf('goBack');"/>
				</c:if>
			</td>
			<td align="right">
				<c:if test="${ndsCrfHasNext}">
					<input type="button" name="BTN_Forvard" id="next" value="<fmt:message bundle='${resword}' key='next' />" class="button_medium" onClick="javascript: initNdsPerCrf('goForward');"/>
				</c:if>
			</td>
		</tr>
	</table>
</div>

<script>applyThemeForChart();</script>
