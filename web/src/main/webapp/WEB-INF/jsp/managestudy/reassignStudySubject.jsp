<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.format" var="resformat"/>
<fmt:setBundle basename="org.akaza.openclinica.i18n.notes" var="restext"/>    
<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/> 
<fmt:setBundle basename="org.akaza.openclinica.i18n.workflow" var="resworkflow"/> 
<c:set var="dteFormat"><fmt:message key="date_format_string" bundle="${resformat}"/></c:set>

<jsp:include page="../include/managestudy-header.jsp"/>


<!-- *JSP* ${pageContext.page['class'].simpleName} -->
<jsp:include page="../include/sideAlert.jsp"/>

<!-- then instructions-->
<tr id="sidebar_Instructions_open" style="display: none">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_collapse.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		<div class="sidebar_tab_content"> 
		
		</div>

		</td>
	
	</tr>
	<tr id="sidebar_Instructions_closed" style="display: all">
		<td class="sidebar_tab">

		<a href="javascript:leftnavExpand('sidebar_Instructions_open'); leftnavExpand('sidebar_Instructions_closed');"><img src="images/sidebar_expand.gif" border="0" align="right" hspace="10"></a>

		<b><fmt:message key="instructions" bundle="${restext}"/></b>

		</td>
  </tr>
<jsp:include page="../include/sideInfo.jsp"/>

<!-- org.akaza.openclinica.bean.core.AuditableEntityBean -->

<jsp:useBean scope="request" id="displayStudy" class="org.akaza.openclinica.bean.admin.DisplayStudyBean"/>
<jsp:useBean scope="session" id="study" class="org.akaza.openclinica.bean.managestudy.StudyBean"/>
<jsp:useBean scope="request" id="subject" class="org.akaza.openclinica.bean.submit.SubjectBean"/>
<jsp:useBean scope="request" id="studySub" class="org.akaza.openclinica.bean.managestudy.StudySubjectBean"/>
<h1>
	<span class="first_level_header">
		<fmt:message key="reassign_study_subject" bundle="${resworkflow}"/> 
	</span>
</h1>
<p><fmt:message key="you_choose_to_reassign_the_following_subject2" bundle="${restext}"/></p>
<form action="ReassignStudySubject" method="post">
<input type="hidden" name="action" value="confirm">
<input type="hidden" name="id" value="<c:out value="${studySub.id}"/>">
 
 <div style="width: 600px">
 <div class="box_T"><div class="box_L"><div class="box_R"><div class="box_B"><div class="box_TL"><div class="box_TR"><div class="box_BL"><div class="box_BR">

<fmt:message key="study_subject_ID" bundle="${resword}" var="studySubjectIdLabel"/>
<c:if test="${study ne null}">
     <c:set var="studySubjectIDLabel" value="${study.studyParameterConfig.studySubjectIdLabel}"/>
</c:if>

<c:set var="genderShow" value="${true}"/>
<fmt:message key="gender" bundle="${resword}" var="genderLabel"/>
<c:if test="${study ne null}">
    <c:set var="genderShow" value="${!(study.studyParameterConfig.genderRequired == 'false')}"/>
    <c:set var="genderLabel" value="${study.studyParameterConfig.genderLabel}"/>
</c:if>

<div class="tablebox_center">
<table border="0" cellpadding="0" cellspacing="0" width="100%">

 <tr>
   <td class="table_header_column">${studySubjectIDLabel}</td>
   <td class="table_cell"><c:out value="${studySub.label}"/></td>
 </tr>

<c:if test="${study.studyParameterConfig.subjectPersonIdRequired != 'copyFromSSID'}">
 <tr>
   <td class="table_header_column"><fmt:message key="person_ID" bundle="${resword}"/></td>
   <td class="table_cell"><c:out value="${subject.uniqueIdentifier}"/></td>
 </tr>
</c:if>

 <c:if test="${genderShow}">
     <tr>
       <td class="table_header_column">${genderLabel}</td>
       <td class="table_cell"><c:out value="${subject.gender}"/></td>
     </tr>
 </c:if>

 <tr>
   <td class="table_header_column"><fmt:message key="date_created" bundle="${resword}"/></td>
   <td class="table_cell"><fmt:formatDate value="${subject.createdDate}" pattern="${dteFormat}"/></td>
 </tr>
 </table>
 </div>
</div></div></div></div></div></div></div></div>
</div>
<br>
<p><strong><fmt:message key="please_choose_a_study_in_the_following_list2" bundle="${restext}"/></strong></P>
    
   <table border="0" cellpadding="0" cellspacing="0"> 
   <tr><td>   	 
        <c:choose> 	 
         <c:when test="${displayStudy.parent.id==studySub.studyId }">      
                      
          <input type="radio" checked name="studyId" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> <fmt:message key="currently_in" bundle="${restext}"/></b>         
                  	 
         </c:when> 	 
         <c:otherwise>          
          <c:if test="${displayStudy.status.available}">
                <input type="radio" name="studyId" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> </b>
          </c:if>
          <c:if test="${displayStudy.status.locked}">
                <input type="radio" disabled="true" name="studyId" value="<c:out value="${displayStudy.parent.id}"/>"><b><c:out value="${displayStudy.parent.name}"/> </b>
          </c:if>
         </c:otherwise>
        </c:choose> 
        <br>        
     </td></tr> 
      <c:forEach var="child" items="${displayStudy.children}">
      <tr><td>
         <c:choose> 	 
         <c:when test="${child.id==studySub.studyId }">      
           &nbsp;&nbsp;<div class="homebox_bullets"><input type="radio" checked name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/> <fmt:message key="currently_in" bundle="${restext}"/></div>
         </c:when> 	 
         <c:otherwise>          
            <c:if test="${child.status.available}"> 
                &nbsp;&nbsp;<div class="homebox_bullets"><input type="radio" name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/></div>
            </c:if>
             <c:if test="${child.status.locked}">
                 &nbsp;&nbsp;<div class="homebox_bullets"><input type="radio" disabled="true" name="studyId" value="<c:out value="${child.id}"/>"><c:out value="${child.name}"/></div>
             </c:if>
         </c:otherwise>
        </c:choose>       
      
      </td></tr>
      </c:forEach>
    
   </table>
  <p>                          <%-- location.href = '<%=request.getContextPath()%>/ViewStudySubject?id=${studySub.id}' --%>
      <input type="button" name="BTN_Smart_Back" id="GoToPreviousPage"
					value="<fmt:message key="back" bundle="${resword}"/>"
					class="button_medium"
					onClick="javascript: goBackSmart('${navigationURL}', '${defaultURL}');" />
      <input type="submit" name="Submit" value="<fmt:message key="continue" bundle="${resword}"/>" class="button_medium">
  </p>


</form>
 <br><br>


<jsp:include page="../include/footer.jsp"/>
