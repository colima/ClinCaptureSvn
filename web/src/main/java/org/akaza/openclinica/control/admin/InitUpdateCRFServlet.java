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

/*
 * OpenClinica is distributed under the
 * GNU Lesser General Public License (GNU LGPL).

 * For details see: http://www.openclinica.org/license
 * copyright 2003-2005 Akaza Research
 */
package org.akaza.openclinica.control.admin;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class InitUpdateCRFServlet extends Controller {

	private static final String CRF_ID = "crfId";
	private static final String CRF = "crf";

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		if (ub.isSysAdmin()) {
			return;
		}

		boolean isStudyDirectorInParent = false;
		if (currentStudy.getParentStudyId() > 0) {
			logger.info("2222");
			Role r = ub.getRoleByStudy(currentStudy.getParentStudyId()).getRole();
			if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.SYSTEM_ADMINISTRATOR)) {
				isStudyDirectorInParent = true;
			}
		}

		// get current studyid
		int studyId = currentStudy.getId();

		if (ub.hasRoleInStudy(studyId)) {
			Role r = ub.getRoleByStudy(studyId).getRole();
			if (isStudyDirectorInParent || r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.SYSTEM_ADMINISTRATOR)) {
				return;
			}
		}

		addPageMessage(
				respage.getString("you_not_have_permission_to_update_a_CRF")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(false);
		panel.setOrderedData(true);

		setToPanel(resword.getString("create_CRF"), respage.getString("br_create_new_CRF_entering"), request);

		setToPanel(resword.getString("create_CRF_version"), respage.getString("br_create_new_CRF_uploading"), request);
		setToPanel(resword.getString("revise_CRF_version"), respage.getString("br_if_you_owner_CRF_version"), request);
		setToPanel(resword.getString("CRF_spreadsheet_template"),
				respage.getString("br_download_blank_CRF_spreadsheet_from"), request);
		setToPanel(resword.getString("example_CRF_br_spreadsheets"),
				respage.getString("br_download_example_CRF_instructions_from"), request);

		FormProcessor fp = new FormProcessor(request);

		// checks which module the requests are from
		String module = fp.getString(MODULE);
		request.setAttribute(MODULE, module);

		int crfId = fp.getInt(CRF_ID);
		if (crfId == 0) {
			addPageMessage(respage.getString("please_choose_a_CRF_version_to_update"), request);
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
		} else {
			CRFDAO cdao = getCRFDAO();
			CRFBean crf = (CRFBean) cdao.findByPK(crfId);
			if (!ub.isSysAdmin() && (crf.getOwnerId() != ub.getId())) {
				addPageMessage(
						respage.getString("no_have_correct_privilege_current_study") + " "
								+ respage.getString("change_active_study_or_contact"), request);
				forwardPage(Page.MENU_SERVLET, request, response);
			} else {
				request.getSession().setAttribute(CRF, crf);
				forwardPage(Page.UPDATE_CRF, request, response);
			}

		}
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		UserAccountBean ub = getUserAccountBean(request);
		if (ub.isSysAdmin()) {
			return Controller.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

}
