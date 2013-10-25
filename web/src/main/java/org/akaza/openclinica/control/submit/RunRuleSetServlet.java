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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.control.submit;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.domain.rule.RuleSetBasedViewContainer;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.service.rule.RuleSetServiceInterface;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings({"serial"})
public class RunRuleSetServlet extends SecureController {

	private static String RULESET_ID = "ruleSetId";
	private static String RULE_ID = "ruleId";
	private static String RULESET = "ruleSet";
	private static String RULESET_RESULT = "ruleSetResult";
	private RuleSetServiceInterface ruleSetService;

	/**
     * 
     */
	@Override
	public void mayProceed() throws InsufficientPermissionException {
		if (ub.isSysAdmin()) {
			return;
		}
		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"));
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("not_study_director"), "1");

	}

	@SuppressWarnings("unused")
	@Override
	public void processRequest() throws Exception {

		String ruleSetId = request.getParameter(RULESET_ID);
		String ruleId = request.getParameter(RULE_ID);
		String dryRun = request.getParameter("dryRun");

		RuleSetBean ruleSetBean = getRuleSetBean(ruleSetId, ruleId);
		if (ruleSetBean != null) {
			List<RuleSetBean> ruleSets = new ArrayList<RuleSetBean>();
			ruleSets.add(ruleSetBean);
			if (dryRun != null && dryRun.equals("no")) {
				List<RuleSetBasedViewContainer> resultOfRunningRules = getRuleSetService().runRulesInBulk(ruleSets, false, currentStudy, ub);
				addPageMessage(respage.getString("actions_successfully_taken"));
				forwardPage(Page.LIST_RULE_SETS_SERVLET);

			} else {
				List<RuleSetBasedViewContainer> resultOfRunningRules = getRuleSetService().runRulesInBulk(ruleSets,
						true, currentStudy, ub);
				request.setAttribute(RULESET, ruleSetBean);
				request.setAttribute(RULESET_RESULT, resultOfRunningRules);
				if (resultOfRunningRules.size() > 0) {
					addPageMessage(resword.getString("view_executed_rules_affected_subjects"));
				} else {
					addPageMessage(resword.getString("view_executed_rules_no_affected_subjects"));
				}

				forwardPage(Page.VIEW_EXECUTED_RULES);

			}

		} else {
			addPageMessage("RuleSet not found");
			forwardPage(Page.LIST_RULE_SETS_SERVLET);
		}
	}

	private RuleSetBean getRuleSetBean(String ruleSetId, String ruleId) {
		RuleSetBean ruleSetBean = null;
		if (ruleId != null && ruleSetId != null && ruleId.length() > 0 && ruleSetId.length() > 0) {
			ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
			ruleSetBean = ruleSetService.filterByRules(ruleSetBean, Integer.valueOf(ruleId));
		} else if (ruleSetId != null && ruleSetId.length() > 0) {
			ruleSetBean = getRuleSetService().getRuleSetById(currentStudy, ruleSetId);
		}
		return ruleSetBean;
	}

	@Override
	protected String getAdminServlet() {
		if (ub.isSysAdmin()) {
			return SecureController.ADMIN_SERVLET_CODE;
		} else {
			return "";
		}
	}

	private RuleSetServiceInterface getRuleSetService() {
		ruleSetService = this.ruleSetService != null ? ruleSetService : (RuleSetServiceInterface) SpringServletAccess
				.getApplicationContext(context).getBean("ruleSetService");
		ruleSetService.setContextPath(getContextPath());
		ruleSetService.setMailSender((JavaMailSenderImpl) SpringServletAccess.getApplicationContext(context).getBean(
				"mailSender"));
		ruleSetService.setRequestURLMinusServletPath(getRequestURLMinusServletPath());
		return ruleSetService;
	}

}
