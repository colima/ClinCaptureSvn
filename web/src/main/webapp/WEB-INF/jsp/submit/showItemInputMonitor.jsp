<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope="request" id="section" class="org.akaza.openclinica.bean.submit.DisplaySectionBean" />
<jsp:useBean scope="request" id="displayItem" class="org.akaza.openclinica.bean.submit.DisplayItemBean" />
<jsp:useBean scope='request' id='formMessages' class='java.util.HashMap'/>
<!-- *JSP* submit/showItemInputMonitor.jsp -->
<c:set var="repeatParentId" value="${param.repeatParentId}" />
<c:set var="contextPath" value="${pageContext.request.contextPath}" />
<c:set var="inputType" value="${displayItem.metadata.responseSet.responseType.name}" />
<c:set var="itemId" value="${displayItem.item.id}" />
<c:set var="inputName" value="input${itemId}" />
<c:set var="inputVal" value="input${itemId}" />
<c:set var="numOfDate" value="${param.key}" />
<c:set var="defValue" value="${param.defaultValue}" />
<c:set var="respLayout" value="${param.respLayout}" />

<%-- What is the including JSP (e.g., doubleDataEntry)--%>
<c:set var="originJSP" value="${param.originJSP}" />
<%-- A boolean request attribute set in DataEntryServlet...--%>
<c:set var="hasDataFlag" value="${hasDataFlag}" />
<c:set var="ddeEntered" value="${requestScope['ddeEntered']}" />

<c:set var="isLocked" value="${param.isLocked}" />

<c:set var="isBlank" value="0" />

<c:if test="${empty displayItem.data.value}">
        <c:set var="isBlank" value="1" />
</c:if>

<c:if test="${(respLayout eq 'Horizontal' || respLayout eq 'horizontal')}">
  <c:set var="isHorizontal" value="${true}" />
</c:if>

<%-- text input value; the default value is not displayed if the application has data, or is
 not originating from doubleDataEntry--%>
<c:if test="${hasDataFlag == null || empty hasDataFlag}">
  <c:set var="hasDataFlag" value="${false}"/>
</c:if>

<c:choose>
  <c:when test="${section.section.processDefaultValues}"><c:set var="inputTxtValue" value="${defValue}"/></c:when>
  <c:otherwise><c:set var="inputTxtValue" value="${displayItem.metadata.responseSet.value}"/></c:otherwise>
</c:choose>

<%-- for tab index. must start from 1, not 0--%>
<c:set var="crfTabIndex" value="${crfTabIndex + 1}" scope="request"/>
<c:set var="tabNum" value="${crfTabIndex}" />

<%-- find out whether the item is involved with an error message, and if so, outline the
form element in red --%>


<c:forEach var="frmMsg" items="${formMessages}">
  <c:if test="${frmMsg.key eq inputVal}">
    <c:set var="isInError" value="${true}" />
    <c:set var="errorTxtMessage" value="${frmMsg.value}" />
    <c:set var="errorTxtMessage" value='<%= StringEscapeUtils.escapeJavaScript(pageContext.getAttribute("errorTxtMessage").toString()) %>' />
  </c:if>
</c:forEach>

 <c:if test="${isInError}">
      <c:set var="errorFlag" value="1"/><!--  use in discrepancy note-->
 </c:if>

<c:if test="${displayItem.item.dataType.id == 13}">
    <c:set var="inputType" value="divider"/>
</c:if>

<c:if test="${displayItem.item.dataType.id == 14}">
    <c:set var="inputType" value="label"/>
</c:if>

<%-- A way to deal with the lack of 'break' out of forEach loop--%>
<c:if test='${inputType=="file"}'>
	<label for="input<c:out value="${itemId}"/>"></label>
	<c:choose>
	<c:when test="${empty displayItem.data.value}">
		<input type="text" id="ft<c:out value="${itemId}"/>" name="fileText<c:out value="${itemId}"/>" value="">
		<input type="button" id="up<c:out value="${inputName}"/>" name="uploadFile<c:out value="${inputName}"/>" value="<fmt:message key="click_to_upload" bundle="${resword}"/>">
	</c:when>
	<c:otherwise>
		<c:choose>
		<c:when test="${fn:contains(inputTxtValue, 'fileNotFound#')}">
			<del><c:out value="${fn:substringAfter(inputTxtValue,'fileNotFound#')}"/></del>
		</c:when>
		<c:otherwise>
			<c:set var="filename" value="${displayItem.data.value}"/>
			<c:set var="sep" value="\\"/>
            <c:set var="sep2" value="\\\\"/>
			<a href="DownloadAttachedFile?eventCRFId=<c:out value="${section.eventCRF.id}"/>&&fileName=${fn:replace(fn:replace(filename,'+','%2B'),sep,sep2)}" id="a<c:out value="${itemId}"/>"><c:out value="${inputTxtValue}"/></a>
		</c:otherwise>
		</c:choose>
	</c:otherwise>
	</c:choose>
</c:if>

<c:if test='${inputType == "instant-calculation"}'>
    <label for="input<c:out value="${itemId}"/>"></label>
    <c:choose>
        <c:when test="${isInError}">
            <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
                "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="input<c:out value="${itemId}" />" value="<c:out value="${inputTxtValue}"/>" />
        </c:when>
        <c:otherwise>
            <input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
                    "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="input<c:out value="${itemId}" />" value="<c:out value="${inputTxtValue}"/>" />
        </c:otherwise>
    </c:choose>
</c:if>

<c:if test='${inputType == "text"}'>

  <label for="input<c:out value="${itemId}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
      "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="input<c:out value="${itemId}" />" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
    </c:when>
    <c:otherwise>
      <input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange=
        "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" name="input<c:out value="${itemId}" />" <c:out value="${respLayout}"/> value="<c:out value="${inputTxtValue}"/>" />
    </c:otherwise>
  </c:choose>
  <c:if test="${displayItem.item.itemDataTypeId==9 || displayItem.item.itemDataTypeId==10}"><!-- date type-->
	  <ui:calendarIcon onClickSelector="getSib(this.previousSibling)" linkId="anchor${itemId}" linkName="anchor${itemId}" imageId="anchor${itemId}" checkIfShowYear="true"/>
    <c:set var="numOfDate" value="${numOfDate+1}"/>
  </c:if>
</c:if>
<c:if test='${inputType == "textarea"}'>
  <label for="input<c:out value="${itemId}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><textarea class="aka_input_error" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="input<c:out value="${itemId}" />" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
    </c:when>
    <c:otherwise>
      <textarea id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" name="input<c:out value="${itemId}" />" rows="5" cols="40"><c:out value="${inputTxtValue}"/></textarea>
    </c:otherwise>
  </c:choose>
</c:if>
<c:if test='${inputType == "checkbox"}'>
  <%-- What if the defaultValue is a comma- or space-separated value for
 multiple checkboxes or multi-select tags? --%>
  <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
    <c:set var="checked" value="" />
    <c:choose>
      <c:when test="${section.section.processDefaultValues}">
        <c:forTokens items="${inputTxtValue}" delims=","  var="_item">
          <c:if test="${(option.text eq _item) || (option.value eq _item)}"><c:set var="checked" value="checked" /></c:if>
        </c:forTokens>
      </c:when>
      <c:otherwise><c:if test="${option.selected}"><c:set var="checked" value="checked" /></c:if></c:otherwise>
    </c:choose>
    <label for="input<c:out value="${itemId}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
      </c:when>
      <c:otherwise>
        <input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="checkbox" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
      </c:otherwise>
    </c:choose>
  </c:forEach>
</c:if>
<c:if test='${inputType == "radio"}'>
  <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
    <c:choose>
      <c:when test="${option.selected}"><c:set var="checked" value="checked" /></c:when>
      <c:otherwise><c:set var="checked" value="" /></c:otherwise>
    </c:choose>
    <label for="input<c:out value="${itemId}"/>"></label>
    <c:choose>
      <c:when test="${isInError}">
        <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
      </c:when><c:otherwise>
      <input id="input<c:out value="${itemId}"/>" tabindex="${tabNum}" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="radio" name="input<c:out value="${itemId}"/>" value="<c:out value="${option.value}" />" <c:out value="${checked}"/> /> <c:out value="${option.text}" /> <c:if test="${! isHorizontal}"><br/></c:if>
    </c:otherwise>
    </c:choose>
  </c:forEach>
</c:if>

<%-- adding some spacing to make this more readable, tbh --%>

<c:if test='${inputType == "single-select"}'>
    <label for="input${itemId}"></label>
    <c:if test="${isInError && !hasShown}">
        <span class="${exclaim}">! </span>
    </c:if>
    <c:set var="scdScript" value=""/>
    <c:set var="optionWasSelected" value="false"/>
    <c:set var="defaultValueInOptions" value="false"/>
    <c:set var="selectDefault" value="${section.section.processDefaultValues && displayItem.metadata.defaultValue != '' && displayItem.metadata.defaultValue != null}"/>
    <c:if test="${fn:length(displayItem.scdData.scdSetsForControl)>0}">
        <c:set var="scdPairStr" value=""/>
        <c:forEach var="aPair" items="${displayItem.scdData.scdSetsForControl}">
            <c:set var="scdPairStr" value="${scdPairStr}-----${aPair.scdItemId}-----${aPair.optionValue}"/>
        </c:forEach>
        <c:set var="scdScript" value="selectControlShow(this, '${scdPairStr}');"/>
    </c:if>
    <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
        <c:if test="${option.text eq displayItem.metadata.defaultValue || option.value eq displayItem.metadata.defaultValue}">
            <c:set var="defaultValueInOptions" value="true"/>
        </c:if>
    </c:forEach>
    <select class="${isInError ? 'aka_input_error' : 'formfield'}" id="input${itemId}" tabindex="${tabNum}"
            onChange="this.className='changedField'; destNonRepInstant('${itemId}', '${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStr}', '${displayItem.instantFrontStrGroup.nonRepFrontStr.frontStrDelimiter.code}'); ${scdScript} changeImage('input${itemId}');" name="input${itemId}">
        <c:if test="${!defaultValueInOptions}">
            <c:set var="optionWasSelected" value="${selectDefault}"/>
            <option value="" ${selectDefault ? 'selected' : ''}>${displayItem.metadata.defaultValue}</option>
        </c:if>
        <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
            <option value="${option.value}" ${!optionWasSelected && ((selectDefault && (option.text eq displayItem.metadata.defaultValue || option.value eq displayItem.metadata.defaultValue)) || option.selected) ? 'selected' : ''}>${option.text}</option>
        </c:forEach>
    </select>
</c:if>

<c:if test='${inputType == "multi-select"}'>
  <label for="input<c:out value="${itemId}"/>"></label>
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><select class="aka_input_error" id="input<c:out value="${itemId}"/>" multiple  tabindex="${tabNum}" name="input<c:out value="${itemId}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');">
    </c:when><c:otherwise>
    <select id="input<c:out value="${itemId}"/>" multiple  tabindex="${tabNum}" name="input<c:out value="${itemId}"/>" onChange="this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');">
  </c:otherwise>
  </c:choose>
  <c:forEach var="option" items="${displayItem.metadata.responseSet.options}">
    <c:set var="checked" value="" />
    <c:choose>
      <c:when test="${section.section.processDefaultValues}">
        <c:forTokens items="${inputTxtValue}" delims=","  var="_item">
          <c:if test="${(option.text eq _item) || (option.value eq _item)}"><c:set var="checked" value="selected" /></c:if>
        </c:forTokens>
      </c:when>
      <c:otherwise><c:if test="${option.selected}"><c:set var="checked" value="selected" /></c:if></c:otherwise>
    </c:choose>
    <option value="${option.value}" ${checked}>${option.text}</option>
  </c:forEach>
  </select>
</c:if>
<c:if test='${inputType == "calculation" || inputType == "group-calculation"}'>
<label for="<c:out value="${inputName}"/>"></label>
	<input type="hidden" name="<c:out value="${inputName}"/>" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
  <c:choose>
    <c:when test="${isInError}">
      <span class="aka_exclaim_error">! </span><input class="aka_input_error" id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
      "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}" />" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
    </c:when>
    <c:otherwise>
      <input id="<c:out value="${inputName}"/>" tabindex="${tabNum}" onChange=
        "this.className='changedField'; javascript:setImage('DataStatus_top','images/icon_UnsavedData.gif'); javascript:setImage('DataStatus_bottom','images/icon_UnsavedData.gif');" type="text" class="disabled" disabled="disabled" name="<c:out value="${inputName}" />" value="<c:out value="${displayItem.metadata.responseSet.value}"/>" />
    </c:otherwise>
  </c:choose>
</c:if>
<c:if test="${displayItem.metadata.required}">
  <td valign="top"><span class="alert">*</span></td>
</c:if>

<c:if test="${study.studyParameterConfig.discrepancyManagement=='true' && !study.status.locked}">
    <c:choose>
    <c:when test="${displayItem.discrepancyNoteStatus == 0}">
        <c:set var="imageFileName" value="icon_noNote" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 1 || displayItem.discrepancyNoteStatus == 6}">
        <c:set var="imageFileName" value="icon_Note" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 2}">
        <c:set var="imageFileName" value="icon_flagYellow" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 3}">
        <c:set var="imageFileName" value="icon_flagBlack" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 4}">
        <c:set var="imageFileName" value="icon_flagGreen" />
    </c:when>
    <c:when test="${displayItem.discrepancyNoteStatus == 5}">
        <c:set var="imageFileName" value="icon_flagWhite" />
    </c:when>
    <c:otherwise>
    </c:otherwise>
  </c:choose>

  <c:import url="../submit/crfShortcutAnchors.jsp">
      <c:param name="rowCount" value=""/>
      <c:param name="itemId" value="${itemId}" />
      <c:param name="inputName" value="${inputName}"/>
  </c:import>

  <td valign="top">

      <c:choose>
          <c:when test="${displayItem.item.dataType.id eq 13 or displayItem.item.dataType.id eq 14}"></c:when>
          <c:when test="${displayItem.numDiscrepancyNotes > 0}">
              <a tabindex="<c:out value="${tabNum + 1000}"/>" href="#"  onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip()"
                 onClick="openDNoteWindow('<c:out value="${contextPath}" />/ViewDiscrepancyNote?stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=1&isLocked=<c:out value="${isLocked}"/>','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>'); return false;">
                  <img id="flag_<c:out value="${inputName}"/>" name="flag_<c:out value="${inputName}"/>"
                       src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
              </a>
          </c:when>
          <c:otherwise>
              <c:if test="${(isLocked eq 'no') && (displayItem.data.id > 0)}">
                  <c:set var="imageFileName" value="icon_noNote" />
                  <a tabindex="<c:out value="${tabNum + 1000}"/>" href="#"  onmouseover="callTip(genToolTips(${displayItem.data.id}));" onmouseout="UnTip()"
                     onClick="openDNWindow('<c:out value="${contextPath}" />/CreateDiscrepancyNote?stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&blank=<c:out value="${isBlank}"/>&writeToDB=1&errorFlag=<c:out value="${errorFlag}"/>&isLocked=<c:out value="${isLocked}"/>','spanAlert-<c:out value="${inputName}"/>','<c:out value="${errorTxtMessage}"/>', event); return false;">
                      <img id="flag_<c:out value="${inputName}" />" name="flag_<c:out value="${inputName}" />"
                           src="<c:out value="${contextPath}" />/images/<c:out value="${imageFileName}"/>.gif" border="0" alt="<fmt:message key="discrepancy_note" bundle="${resword}"/>" title="<fmt:message key="discrepancy_note" bundle="${resword}"/>">
                      <input type="hidden" value="<c:out value="${contextPath}" />/ViewDiscrepancyNote?stSubjectId=<c:out value="${studySubject.id}" />&itemId=<c:out value="${itemId}" />&id=<c:out value="${displayItem.data.id}"/>&name=itemData&field=<c:out value="${inputName}"/>&column=value&monitor=1&writeToDB=1&isLocked=<c:out value="${isLocked}"/>"/>
                  </a>
              </c:if>
          </c:otherwise>
      </c:choose>

      <c:import url="../submit/itemSDV.jsp">
          <c:param name="rowCount" value=""/>
          <c:param name="itemId" value="${itemId}" />
          <c:param name="inputName" value="${inputName}"/>
      </c:import>

  </td>

</c:if>
