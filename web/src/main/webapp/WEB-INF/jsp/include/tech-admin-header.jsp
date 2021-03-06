<%@ page contentType="text/html; charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope='session' id='userBean' class='org.akaza.openclinica.bean.login.UserAccountBean'/>
<jsp:useBean scope='session' id='study' class='org.akaza.openclinica.bean.managestudy.StudyBean' />
<jsp:useBean scope='session' id='userRole' class='org.akaza.openclinica.bean.login.StudyUserRoleBean' />
<jsp:useBean scope='request' id='isAdminServlet' class='java.lang.String' />

<html>
<head>
    <title><fmt:message key="openclinica" bundle="${resword}"/></title>

    <link rel="stylesheet" href="includes/styles.css?r=${revisionNumber}" type="text/css">
    <%-- <link rel="stylesheet" href="includes/styles2.css?r=${revisionNumber}" type="text/css">--%>
    <%-- <link rel="stylesheet" href="includes/NewNavStyles.css?r=${revisionNumber}" type="text/css" />--%>
    <script type="text/JavaScript" language="JavaScript" src="includes/jmesa/jquery-1.3.2.min.js"></script>
    <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript.js?r=${revisionNumber}"></script>
    <%-- <script type="text/JavaScript" language="JavaScript" src="includes/global_functions_javascript2.js?r=${revisionNumber}"></script> --%>
    <script language="JavaScript" src="includes/Tabs.js?r=${revisionNumber}"></script>
    <script language="JavaScript" src="includes/CalendarPopup.js?r=${revisionNumber}"></script>
    <!-- Added for the new Calender -->

    <ui:calendar/>
    <!-- End -->
    <ui:theme/>

    <link rel="icon" href="<c:url value='${faviconUrl}'/>" />
    <link rel="shortcut icon" href="<c:url value='${faviconUrl}'/>" /></head>
<body class="main_BG" topmargin="0" leftmargin="0" marginwidth="0" marginheight="0"
	<jsp:include page="../include/showPopUp.jsp"/>
><table border="0" cellpadding="0" cellspacing="0" width="100%" height="100%" class="background">
	<tr>
		<td valign="top"><!-- Header Table -->

            <table id="headerInnerTable3" border="0" cellpadding="0" cellspacing="0" class="header">
			<tr>				<td valign="top">

<!-- *JSP* ${pageContext.page['class'].simpleName} -->
	<div class="logo"><img src="<c:url value='${systemLogoUrl}'/>"></div>

<!-- Main Navigation -->
	<jsp:include page="../include/navBar.jsp"/><!-- End Main Navigation -->