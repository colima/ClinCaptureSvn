<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ taglib uri="/WEB-INF/tlds/ui/ui.tld" prefix="ui" %>
<%@ taglib uri="/WEB-INF/tlds/format/date/date-time-format.tld" prefix="cc-fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<ui:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.page_messages" var="respage"/>
<ui:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>

<jsp:useBean scope="request" id="currRow" class="org.akaza.openclinica.web.bean.ListCRFRow" />
<c:set var="count" value="${currRow.bean.versionNumber}"/>
<c:set var="count" value="${count+1}"/>
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<c:choose>
  <c:when test="${currRow.bean.status.available}">
    <c:set var="className" value="aka_green_highlight"/>
  </c:when>
  <c:when test="${currRow.bean.status.deleted || currRow.bean.status.locked}">
    <c:set var="className" value="aka_red_highlight"/>
  </c:when>
  <c:otherwise>
		<c:set var="className" value=""/>	
  </c:otherwise>
  
</c:choose>
<tr valign="top" bgcolor="#F5F5F5">
	<td rowspan="${count}" class="table_cell_left">
		<c:forEach var="study" items="${currRow.bean.studiesWhereUsed}" varStatus="studyVarStatus">
			${studyVarStatus.index == 0 ? '' : '<div class="divider"> </div>'} ${study.identifier}
		</c:forEach>
	</td>
  <td rowspan="<c:out value="${count}"/>" class="table_cell"><c:out value="${currRow.bean.name}"/></td>
  <td rowspan="<c:out value="${count}"/>" class="table_cell">
	  <cc-fmt:formatDate value="${currRow.bean.updatedDate}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
	  &nbsp;
  </td>
  <td rowspan="<c:out value="${count}"/>" class="table_cell"><c:out value="${currRow.bean.updater.name}"/>&nbsp;</td>
  <td rowspan="<c:out value="${count}"/>" class="table_cell"><c:out value="${currRow.bean.oid}"/>&nbsp;</td>
  <td class="table_cell">(<fmt:message key="original" bundle="${resword}"/>)</td>
    <%--oid space --%>
  <td class="table_cell">&nbsp;</td>
  <td class="table_cell">
	  <cc-fmt:formatDate value="${currRow.bean.createdDate}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
  </td>
  <td class="table_cell"><c:out value="${currRow.bean.owner.name}"/></td>
  <td class="table_cell <c:out value='${className}'/>"><c:out value="${currRow.bean.status.name}"/></td>
  <td class="table_cell">&nbsp;</td>
  <td class="table_cell">
    <table border="0" cellpadding="0" cellspacing="0">
      <tr class="innerTable">
        <td><a href="ViewCRF?crfId=<c:out value="${currRow.bean.id}"/>"
               onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
               onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
               data-cc-crfId="${currRow.bean.id}" onclick="setAccessedObjectWithRowspans(this)"><img
          name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
        </td>
        <c:choose>
          <c:when test="${currRow.bean.status.available}">
            <c:if test="${userRole.manageStudy}">
              <td>
                <a href="InitUpdateCRF?crfId=<c:out value="${currRow.bean.id}"/>"
                   onMouseDown="javascript:setImage('bt_Edit1','images/bt_Edit_d.gif');"
                   onMouseUp="javascript:setImage('bt_Edit1','images/bt_Edit.gif');"
                   onclick="setAccessedObjectWithRowspans(this)"><img
                  name="bt_Edit1" src="images/bt_Edit.gif" border="0" alt="<fmt:message key="edit" bundle="${resword}"/>" title="<fmt:message key="edit" bundle="${resword}"/>" align="left" hspace="6"></a>
              </td>
              <td><a href="RemoveCRF?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
                     onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                     onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
                     onclick="setAccessedObjectWithRowspans(this)"><img
                name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
              </td>
            </c:if>
            <c:if test="${userRole.manageStudy}">
                  <td>
                      <a href='pages/completeCRFDelete?crfId=<c:out value="${currRow.bean.id}"/>'
                         onMouseDown="javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');"
                         onMouseUp="javascript:setImage('bt_Delete1','images/bt_Delete.gif');"><img name="bt_Delete1" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="6"></a>
                  </td>
            </c:if>
            <td><a href="InitCreateCRFVersion?crfId=<c:out value="${currRow.bean.id}"/>&name=<c:out value="${currRow.bean.name}"/>"
                   onMouseDown="javascript:setImage('bt_NewVersion1','images/bt_NewVersion_d.gif');"
                   onMouseUp="javascript:setImage('bt_NewVersion1','images/bt_NewVersion.gif');"
                   onclick="setAccessedObjectWithRowspans(this)"><img
              name="bt_NewVersion1" src="images/bt_NewVersion.gif" border="0" alt="<fmt:message key="create_new_version" bundle="${resword}"/>" title="<fmt:message key="create_new_version" bundle="${resword}"/>" align="left" hspace="6"></a>
            </td>
          </c:when>
          <c:otherwise>
			<td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></a>			
			</td>
            <td><a href="RestoreCRF?action=confirm&id=<c:out value="${currRow.bean.id}"/>"
                   onMouseDown="javascript:setImage('bt_Restor3','images/bt_Restore_d.gif');"
                   onMouseUp="javascript:setImage('bt_Restore3','images/bt_Restore.gif');"
                   onclick="setAccessedObjectWithRowspans(this)"><img
              name="bt_Restore3" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
            </td>

              <c:if test="${userRole.manageStudy}">
                  <td>
                      <a href='pages/completeCRFDelete?crfId=<c:out value="${currRow.bean.id}"/>'
                         onMouseDown="javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');"
                         onMouseUp="javascript:setImage('bt_Delete1','images/bt_Delete.gif');"><img name="bt_Delete1" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="6"></a>
                  </td>
              </c:if>
         </c:otherwise>
        </c:choose>
      </tr>
    </table>
  </td>
</tr>

<c:forEach var ="version" items="${currRow.bean.versions}">
  <%-- color-coded statuses...--%>
  <c:choose>
    <c:when test="${version.status.available}">
      <c:set var="className" value="aka_green_highlight"/>
    </c:when>
    <c:when test="${version.status.deleted || version.status.locked}">
      <c:set var="className" value="aka_red_highlight"/>
    </c:when>
    <c:otherwise>
		<c:set var="className" value=""/>	
  </c:otherwise>
  </c:choose>
  <tr valign="top">
    <td class="table_cell"><c:out value="${version.name}"/></td>
    <td class="table_cell"><c:out value="${version.oid}"/></td>    
    <td class="table_cell">
		<cc-fmt:formatDate value="${version.createdDate}" pattern="${dteFormat}" dateTimeZone="${userBean.userTimeZoneId}"/>
	</td>
    <td class="table_cell"><c:out value="${version.owner.name}"/></td>
    <td class="table_cell <c:out value='${className}'/>"><c:out value="${version.status.name}"/></td>
    <td class="table_cell">
      <c:choose>
        <c:when test="${version.downloadable}">
          <a href="DownloadVersionSpreadSheet?crfId=<c:out value="${currRow.bean.id}"/>&crfVersionId=<c:out value="${version.id}"/>"
             onMouseDown="javascript:setImage('bt_Download1','images/bt_Download_d.gif');"
             onMouseUp="javascript:setImage('bt_Download1','images/bt_Download.gif');"
             onclick="setAccessedObjectWithRowspans(this)"><img
            name="bt_Download1" src="images/bt_Download.gif" border="0" alt="<fmt:message key="download_spreadsheet" bundle="${resword}"/>" title="<fmt:message key="download_spreadsheet" bundle="${resword}"/>" align="left" hspace="6">
          </a>
        </c:when>
        <c:otherwise>
          <fmt:message key="N/A" bundle="${resword}"/>
        </c:otherwise>
      </c:choose>
    </td>
    <td class="table_cell">
      <table border="0" cellpadding="0" cellspacing="0">
        <tr class="innerTable">
          <td>
            <a href="ViewSectionDataEntry?crfId=<c:out value="${currRow.bean.id}"/>&crfVersionId=<c:out value="${version.id}"/>&tabId=1&crfListPage=yes"
               onMouseDown="javascript:setImage('bt_View1','images/bt_View_d.gif');"
               onMouseUp="javascript:setImage('bt_View1','images/bt_View.gif');"
               data-cc-crfId="${currRow.bean.id}_${version.id}" onclick="setAccessedObjectWithRowspans(this)"><img
              name="bt_View1" src="images/bt_View.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="view" bundle="${resword}"/>" align="left" hspace="6"></a>
          </td>
          <c:if test="${version.status.available && userRole.manageStudy}">
              <td><a href="LockCRFVersion?id=<c:out value="${version.id}"/>"
                onMouseDown="javascript:setImage('bt_Lock1','images/bt_Lock_d.gif');"
                onMouseUp="javascript:setImage('bt_Lock1','images/bt_Lock.gif');"
                onclick="setAccessedObjectWithRowspans(this)"><img
                name="bt_Lock1" src="images/bt__Lock.png" border="0" alt="<fmt:message key="lock" bundle="${resword}"/>" title="<fmt:message key="lock" bundle="${resword}"/>" align="left" hspace="6"></a>
              </td>
		  </c:if>
		  <c:if test="${!currRow.bean.status.deleted && version.status.locked}">
             <td><a href="UnlockCRFVersion?id=<c:out value="${version.id}"/>"
			  onMouseDown="javascript:setImage('bt_Unlock1','images/bt_Unlock_d.gif');"
			  onMouseUp="javascript:setImage('bt_Unlock1','images/bt_Unlock.gif');"
			  onclick="setAccessedObjectWithRowspans(this)"><img 
			  name="bt_Unlock1" src="images/bt__Unlock.png" border="0" alt="<fmt:message key="unlock" bundle="${resword}"/>" title="<fmt:message key="unlock" bundle="${resword}"/>" align="left" hspace="6"></a>
		     </td>       
          </c:if>
          <c:if test="${userRole.manageStudy}">
            <c:choose>
              <c:when test="${version.status.available}">			
				</td>
                <td><a href="RemoveCRFVersion?action=confirm&id=<c:out value="${version.id}"/>"
                       onMouseDown="javascript:setImage('bt_Remove1','images/bt_Remove_d.gif');"
                       onMouseUp="javascript:setImage('bt_Remove1','images/bt_Remove.gif');"
                       onclick="setAccessedObjectWithRowspans(this)"><img
                  name="bt_Remove1" src="images/bt_Remove.gif" border="0" alt="<fmt:message key="remove" bundle="${resword}"/>" title="<fmt:message key="remove" bundle="${resword}"/>" align="left" hspace="6"></a>
                </td>                
              </c:when>
              <c:when test="${!currRow.bean.status.deleted && version.status.id eq 5}">
			  
			<td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></a>			
			</td>
                <td><a href="RestoreCRFVersion?action=confirm&id=<c:out value="${version.id}"/>"
                       onMouseDown="javascript:setImage('bt_Restor1','images/bt_Restore_d.gif');"
                       onMouseUp="javascript:setImage('bt_Restore1','images/bt_Restore.gif');"
                       onclick="setAccessedObjectWithRowspans(this)"><img
                  name="bt_Restore1" src="images/bt_Restore.gif" border="0" alt="<fmt:message key="restore" bundle="${resword}"/>" title="<fmt:message key="restore" bundle="${resword}"/>" align="left" hspace="6"></a>
                </td>
             </c:when>
             <c:when test="${currRow.bean.status.deleted && version.status.id eq 5}">
                <td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
                <td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
             </c:when>
            </c:choose>
          </c:if>
          <c:if test="${currRow.bean.status.deleted && version.status.locked}">
                <td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></td>
          </c:if>
		  <c:if test="${version.status.id eq 7}">
		  	<td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
			</td>
			<td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
			</td>
		  </c:if>
		  <c:if test="${version.status.id eq 5 && !(userRole.manageStudy)}">
		  	<td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6">
			</td>
		  </c:if>
		  <c:if test="${version.status.locked}">
		  	<td><img name="spaceIcon" src="images/bt_Restore.gif" style="visibility:hidden;" border="0" align="left" hspace="6"></a>			
			</td>
		  </c:if>
          <c:if test="${userRole.manageStudy}">
            <td><a href="pages/deleteCRFVersion?crfVersionId=<c:out value="${version.id}"/>"
                   onMouseDown="javascript:setImage('bt_Delete1','images/bt_Delete_d.gif');"
                   onMouseUp="javascript:setImage('bt_Delete1','images/bt_Delete.gif');"
                   onclick="setAccessedObjectWithRowspans(this)" style="${fn:length(currRow.bean.versions) <= 1 ? 'visibility: hidden;' : ''}"><img
              name="bt_Delete1" src="images/bt_Delete.gif" border="0" alt="<fmt:message key="delete" bundle="${resword}"/>" title="<fmt:message key="delete" bundle="${resword}"/>" align="left" hspace="6"></a>
            </td>
          </c:if>
			<td>
				<c:choose>
					<c:when test="${study.studyParameterConfig.crfAnnotation == 'no'}">
						<img src="images/bt_Transparent.gif" border="0" align="left" hspace="6">
                    </c:when>
	            	<c:otherwise>	
						<a href="ViewSectionDataEntry?crfId=<c:out value="${currRow.bean.id}"/>&crfVersionId=<c:out value="${version.id}"/>&tabId=1&annotated=1"
							onclick="setAccessedObjectWithRowspans(this)">
							<img name="bt_Annotate" src="images/bt_Reassign.gif" border="0" alt="<fmt:message key="view" bundle="${resword}"/>" title="<fmt:message key="annotate_crf" bundle="${resword}"/>" align="left" hspace="6">
						</a>
					</c:otherwise>
				</c:choose>			
			</td>
        </tr>
      </table>
    </td>
  </tr>
</c:forEach>
<tr><td class="table_divider" colspan="12">&nbsp;</td></tr>
