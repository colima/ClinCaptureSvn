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

import com.clinovo.util.ValidatorHelper;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.core.Controller;

import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "serial" })
@Component
public class UpdateCRFServlet extends Controller {

	private static String CRF = "crf";

	/**
     *
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
			if (r.equals(Role.SYSTEM_ADMINISTRATOR)) {
				isStudyDirectorInParent = true;
			}
		}

		// get current studyid
		int studyId = currentStudy.getId();

		if (ub.hasRoleInStudy(studyId)) {
			Role r = ub.getRoleByStudy(studyId).getRole();
			if (isStudyDirectorInParent || r.equals(Role.SYSTEM_ADMINISTRATOR)) {
				return;
			}
		}

		addPageMessage(
				respage.getString("you_not_have_permission_update_a_CRF")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.CRF_LIST_SERVLET, resexception.getString("not_study_director"),
				"1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

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

		String action = fp.getString("action");

		CRFBean crf = (CRFBean) request.getSession().getAttribute(CRF);
		if (StringUtil.isBlank(action)) {
			request.getSession().setAttribute(CRF, crf);
			forwardPage(Page.UPDATE_CRF, request, response);
		} else {
			if ("back".equalsIgnoreCase(action)) {
				forwardPage(Page.UPDATE_CRF, request, response);
			} else if ("confirm".equalsIgnoreCase(action)) {
				confirmCRF(request, response);

			} else if ("submit".equalsIgnoreCase(action)) {

				submitCRF(request, response);
			}
		}
	}

	/**
	 * 
	 * @throws Exception
	 */
	private void confirmCRF(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);

		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("name", Validator.LENGTH_NUMERIC_COMPARISON, NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO,
				255);

		v.addValidation("description", Validator.LENGTH_NUMERIC_COMPARISON,
				NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 2048);

		HashMap errors = v.validate();

		if (!StringUtil.isBlank(fp.getString("name"))) {
			CRFDAO cdao = getCRFDAO();

			CRFBean crf = (CRFBean) request.getSession().getAttribute(CRF);
			CRFBean crf1 = (CRFBean) cdao.findAnotherByName(fp.getString("name").trim(), crf.getId());
			logger.info("crf:" + crf.getName() + crf.getId());
			logger.info("crf1:" + crf1.getName() + crf1.getId());
			if (crf1.getId() > 0) {
				Validator
						.addError(errors, "name", resexception.getString("CRF_name_used_by_another_CRF_choose_unique"));
			}
		}

		if (!errors.isEmpty()) {
			logger.info("has errors");
			request.setAttribute("formMessages", errors);
			forwardPage(Page.UPDATE_CRF, request, response);
		} else {
			logger.info("no errors");
			CRFBean crf = (CRFBean) request.getSession().getAttribute(CRF);
			crf.setName(fp.getString("name"));
			crf.setDescription(fp.getString("description"));

			request.getSession().setAttribute(CRF, crf);

			forwardPage(Page.UPDATE_CRF_CONFIRM, request, response);
		}

	}

	/**
	 * Inserts the new study into database
	 * 
	 */
	@SuppressWarnings("unchecked")
	private void submitCRF(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		CRFDAO cdao = new CRFDAO(getDataSource());
		CRFBean crf = (CRFBean) request.getSession().getAttribute(CRF);
		String keyValue = (String) request.getSession().getAttribute("savedListCRFsUrl");
		logger.info("CRF bean to be updated:" + crf.getName());

		crf.setUpdater(ub);
		crf.setUpdatedDate(new Date());
		crf.setStatus(Status.AVAILABLE);
		cdao.update(crf);

		request.getSession().removeAttribute(CRF);
		addPageMessage(respage.getString("the_CRF_has_been_updated_succesfully"), request);

		if (keyValue != null) {
			Map storedAttributes = new HashMap();
			storedAttributes.put(Controller.PAGE_MESSAGE, request.getAttribute(Controller.PAGE_MESSAGE));
			request.getSession().setAttribute(STORED_ATTRIBUTES, storedAttributes);
			response.sendRedirect(response.encodeRedirectURL(keyValue));
		} else {
			forwardPage(Page.CRF_LIST_SERVLET, request, response);
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
