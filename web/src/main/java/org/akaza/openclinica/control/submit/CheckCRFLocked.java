/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License 
 * as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty 
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  
 \* If not, see <http://www.gnu.org/licenses/>. Modified by Clinovo Inc 01/29/2013.
 ******************************************************************************/

package org.akaza.openclinica.control.submit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.SpringServlet;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

/**
 * Created by IntelliJ IDEA. User: A. Hamid Date: Apr 12, 2010 Time: 3:32:44 PM
 */
@Component
public class CheckCRFLocked extends SpringServlet {
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		int userId;
		String ecId = request.getParameter("ecId");
		if (ecId != null && !ecId.equals("")) {
			if (getUnavailableCRFList().containsKey(Integer.parseInt(ecId))) {
				userId = (Integer) getUnavailableCRFList().get(Integer.parseInt(ecId));
				UserAccountDAO udao = getUserAccountDAO();
				UserAccountBean ubean = (UserAccountBean) udao.findByPK(userId);
				response.getWriter().print(
						getResWord().getString("CRF_unavailable") + "\n" + ubean.getName() + " "
								+ getResWord().getString("Currently_entering_data") + "\n"
								+ getResWord().getString("Leave_the_CRF"));
			} else {
				response.getWriter().print("true");
			}
			return;
		} else if (request.getParameter("userId") != null) {
			removeLockedCRF(Integer.parseInt(request.getParameter("userId")));
			if (request.getParameter("exitTo") != null) {
				response.sendRedirect(request.getParameter("exitTo"));
			} else {
				response.sendRedirect("ListStudySubjects");
			}

		}
	}

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
		return;
	}
}
