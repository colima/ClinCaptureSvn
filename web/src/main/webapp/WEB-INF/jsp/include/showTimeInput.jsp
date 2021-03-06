<%@ page contentType="text/html; charset=UTF-8" %>

<%@ page import="org.akaza.openclinica.i18n.util.*" %>
<%@ page import="java.util.ResourceBundle" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>


<jsp:useBean scope="request" id="presetValues" class="java.util.HashMap" />

<%
    //ResourceBundleProvider rbProvider = new ResourceBundleProvider();
    ResourceBundle resformat = ResourceBundle.getBundle("org.akaza.openclinica.i18n.format", ResourceBundleProvider.getLocale());
%>
<c:set var="prefix" value="${param.prefix}" />
<c:set var="count" value="${param.count}" />
<c:set var="hour" value="${param.jobHour}" />
<c:set var="minute" value="${param.jobMinute}" />
<c:set var="half" value="" />

<c:set var="hourFieldName" value='${prefix}Hour' />
<c:set var="minuteFieldName" value='${prefix}Minute' />
<c:set var="halfFieldName" value='${prefix}Half' />

<c:forEach var="presetValue" items="${presetValues}">
    <c:if test='${presetValue.key == hourFieldName}'>
        <c:set var="hour" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == minuteFieldName}'>
        <c:set var="minute" value="${presetValue.value}" />
    </c:if>
    <c:if test='${presetValue.key == halfFieldName}'>
        <c:set var="half" value="${presetValue.value}" />
    </c:if>
</c:forEach>

<td valign="top">
    <div class="formfieldXS_BG">
        <select name="<c:out value="${hourFieldName}"/>" class="formfieldXS" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
            <option value="<c:out value="-1"/>" ><c:out value=""/></option>

            <% if (resformat.getString("date_time_format_string").contains("HH") || resformat.getString("date_time_format_string").contains("kk")) { %>
            <c:forEach var="currHour" begin="0" end="23" step="1">
                <c:set var="currHr" value="${currHour}"/>
                <c:if test="${currHour < 10}">
                    <c:set var="currHr" value="0${currHour}"/>
                </c:if>
                <c:choose>
                    <c:when test="${hour == currHour}">
                        <option value="<c:out value="${currHour}"/>" selected><c:out value="${currHr}"/></option>
                    </c:when>
                    <c:otherwise>
                        <option value="<c:out value="${currHour}"/>"><c:out value="${currHr}"/></option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <% } else { %>
            <c:forEach var="currHour" begin="1" end="12" step="1">
                <c:set var="currHr" value="${currHour}"/>
                <c:if test="${currHour < 10}">
                    <c:set var="currHr" value="0${currHour}"/>
                </c:if>
                <c:choose>
                    <c:when test="${hour == currHour}">
                        <option value="<c:out value="${currHour}"/>" selected><c:out value="${currHr}"/></option>
                    </c:when>
                    <c:otherwise>
                        <option value="<c:out value="${currHour}"/>"><c:out value="${currHr}"/></option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
            <% } %>
        </select>
    </div>
</td>

<td class="formlabel">:</td>

<td valign="top">
    <div class="formfieldXS_BG">
        <select name="<c:out value="${minuteFieldName}"/>" class="formfieldXS" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
            <option value="<c:out value="-1"/>" ><c:out value=""/></option>
            <c:forEach var="currMinute" begin="0" end="59" step="1">
                <c:set var="currMin" value="${currMinute}"/>
                <c:if test="${currMinute < 10}">
                    <c:set var="currMin" value="0${currMinute}"/>
                </c:if>
                <c:choose>
                    <c:when test="${minute == currMinute}">
                        <option value="<c:out value="${currMinute}"/>" selected><c:out value="${currMin}"/></option>
                    </c:when>
                    <c:otherwise>
                        <option value="<c:out value="${currMinute}"/>"><c:out value="${currMin}"/></option>
                    </c:otherwise>
                </c:choose>
            </c:forEach>
        </select>
    </div>
</td>

<td valign="top">

    <% if (!(resformat.getString("date_time_format_string").contains("HH") || resformat.getString("date_time_format_string").contains("kk"))) { %>
    <%-- if its not 24h, show the am/pm tbh, 06/2008 --%>
    <div class="formfieldXS_BG">
        <select name="<c:out value="${halfFieldName}"/>" class="formfieldXS" onChange="javascript:setImageWithTitle('DataStatus_bottom','images/icon_UnsavedData.gif', 'Data has been entered, but not saved. ');">
            <c:choose>
                <c:when test='${half == "pm"}'>
                    <option value=""></option>
                    <option value="am">am</option>
                    <option value="pm" selected>pm</option>
                </c:when>
                <c:when test='${half == "am"}'>
                    <option value=""></option>
                    <option value="am" selected>am</option>
                    <option value="pm">pm</option>
                </c:when>
                <c:otherwise>
                    <option value=""></option>
                    <option value="am">am</option>
                    <option value="pm">pm</option>
                </c:otherwise>
            </c:choose>
        </select>
    </div>
    <% } else { %>
    <input type="hidden" name="<c:out value="${halfFieldName}"/>" value=""/>
    <% } %>
</td>