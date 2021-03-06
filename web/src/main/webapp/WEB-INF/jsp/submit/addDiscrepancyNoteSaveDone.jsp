<%@ page import="org.akaza.openclinica.bean.core.ResolutionStatus" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<jsp:useBean scope='request' id='strResStatus' class='java.lang.String' />
<jsp:useBean scope='request' id='writeToDB' class='java.lang.String' />
<jsp:useBean scope='request' id='submitClose' class='java.lang.String' />

<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<html>
<head>
<title><fmt:message key="openclinica" bundle="${resword}"/>- <fmt:message key="add_discrepancy_note" bundle="${resword}"/></title>
    <link rel="icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" />

<link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
<script type="text/JavaScript" language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
<script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>

<style type="text/css">
.popup_BG { background-image: url(images/main_BG.gif);
	background-repeat: repeat-x;
	background-position: top;
	background-color: #FFFFFF;
	}
</style>

<script type="text/javascript" language="javascript">
    setImageInParentWin('flag_<c:out value="${updatedDiscrepancyNote.field}"/>', '<%=request.getContextPath()%>/<c:out value="${updatedDiscrepancyNote.resStatus.iconFilePath}"/>', '${updatedDiscrepancyNote.resStatus.id}');
</script>

</head>
<body class="popup_BG" style="margin: 25px;" onload="javascript:refreshSource('true','/ViewNotes?');javascript:window.setTimeout('window.close()',3000);">
<!-- *JSP* submit/addDiscrepancyNoteSaveDone.jsp -->
<div style="float: left;">
	<h1>
		<span class="first_level_header">
			<fmt:message key="add_discrepancy_note" bundle="${resword}"/>
		</span>
	</h1>
</div>
<br clear="all">
	<p class="alert" style="font-size: 14px; margin: 120px 50px;" >
		<c:forEach var="message" items="${pageMessages}">
			<c:out value="${message}" escapeXml="false"/><br><br>
		</c:forEach>
	</p>
</html>
