<%@ include file="/WEB-INF/jsp/taglibs.jsp" %>

<fmt:setBundle basename="org.akaza.openclinica.i18n.words" var="resword"/>

<script type="text/javascript" src="<c:url value='/includes/wz_tooltip/wz_tooltip.js'/>"></script>
<!-- END MAIN CONTENT AREA -->
</td>
			</tr>
		</table>
		</td>
	</tr>
	<tr>
		<td valign="bottom">

<!-- Footer -->

		<table border="0" cellpadding=0" cellspacing="0" width="100%">
			 <tr>
                <td class="footer">
                <a href="http://www.openclinica.org" target="new"><fmt:message key="openclinica_portal" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                <a href="javascript:openDocWindow('${pageContext.request.contextPath}/help/index.html')"><fmt:message key="help" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                <a href="${pageContext.request.contextPath}/Contact"><fmt:message key="contact" bundle="${resword}"/></a>
                &nbsp;&nbsp;&nbsp;
                </td>
                <td class="footer"><fmt:message key="footer.license.1" bundle="${resword}"/> </td>
                <td class="footer" align="right"><fmt:message key="Version_release" bundle="${resword}"/> &nbsp;&nbsp;</td>
                <td width="80" align="right" valign="bottom"><a href="http://www.akazaresearch.com"><img src="${pageContext.request.contextPath}/images/Akazalogo.gif" border="0" alt="<fmt:message key="developed_by_akaza" bundle="${resword}"/>" title="<fmt:message key="developed_by_akaza" bundle="${resword}"/>"></a></td>
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer"> <fmt:message key="footer.license.2" bundle="${resword}"/></td>
                <td align="right" class="footer"><a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()"><center><fmt:message key="footer.edition.1" bundle="${resword}"/></center></a></td>
            </tr>
            <tr>
                <td class="footer"/>
                <td class="footer"><fmt:message key="footer.license.3" bundle="${resword}"/></td>
                <td align="right" class="footer"><a href="javascript:void(0)" onmouseover="Tip('<fmt:message key="footer.tooltip" bundle="${resword}"/>')" onmouseout="UnTip()"><center><fmt:message key="footer.edition.2" bundle="${resword}"/></center></a></td>
            </tr>
		</table>

<!-- End Footer -->

		</td>
	</tr>
</table>

<script type="text/javascript">
        jQuery(document).ready(function() {
            jQuery('#cancel').click(function() {
                jQuery.unblockUI();
                return false;
            });

            jQuery('#Contact').click(function() {
                jQuery.blockUI({ message: jQuery('#contactForm'), css:{left: "200px", top:"180px" } });
            });
        });

    </script>


        <div id="contactForm" style="display:none;">
              <%-- <c:import url="contactPop.jsp">
              </c:import> --%>
        </div>
</body>

</html>
