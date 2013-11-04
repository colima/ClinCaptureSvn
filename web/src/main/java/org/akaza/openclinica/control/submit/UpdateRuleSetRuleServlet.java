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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.domain.Status;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleAuditBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.springframework.stereotype.Component;

@Component
public class UpdateRuleSetRuleServlet extends Controller {

	private static final long serialVersionUID = 1L;

	private static String RULESET_ID = "ruleSetId";
	private static String RULESETRULE_ID = "ruleSetRuleId";
	private static String ACTION = "action";

	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response) throws InsufficientPermissionException {
        UserAccountBean ub = getUserAccountBean(request);
        StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}

		if (currentRole.getRole().equals(Role.STUDY_DIRECTOR) || currentRole.getRole().equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}

		addPageMessage(respage.getString("no_have_correct_privilege_current_study")
				+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.LIST_DEFINITION_SERVLET,
				resexception.getString("not_study_director"), "1");

	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserAccountBean ub = getUserAccountBean(request);

		String ruleSetId = request.getParameter(RULESET_ID);
		String ruleSetRuleId = request.getParameter(RULESETRULE_ID);
		String source = request.getParameter("source");
		String action = request.getParameter(ACTION);
		Status status = null;
		String pageMessage = "";
		if (ruleSetRuleId != null) {
			RuleSetRuleBean ruleSetRule = getRuleSetRuleDao().findById(Integer.valueOf(ruleSetRuleId));
			if (ruleSetRuleId != null && action.equals("remove")) {
				status = Status.DELETED;
				updateRuleSetRule(ub, ruleSetRule, status);
				pageMessage = "view_rules_remove_confirmation";
			} else if (ruleSetRuleId != null && action.equals("restore")) {
				status = Status.AVAILABLE;
				ruleSetRule.getRuleSetBean().setStatus(Status.AVAILABLE);
				updateRuleSetRule(ub, ruleSetRule, status);
				pageMessage = "view_rules_restore_confirmation";
			}
		}
		if (ruleSetRuleId == null && ruleSetId != null && action.equals("remove")) {
			RuleSetBean rs = getRuleSetDao().findById(Integer.valueOf(ruleSetId));
			for (RuleSetRuleBean theRuleSetRule : rs.getRuleSetRules()) {
				if (theRuleSetRule.getStatus() != Status.DELETED) {
					status = Status.DELETED;
					updateRuleSetRule(ub, theRuleSetRule, status);
					pageMessage = "view_rules_remove_confirmation";
				}
			}
		}

		addPageMessage(resword.getString(pageMessage), request);
		if (source != null && source.equals("ViewRuleSet")) {

			getServletContext().getRequestDispatcher("/ViewRuleSet?ruleSetId=" + ruleSetId).forward(request, response);
		} else {
			forwardPage(Page.LIST_RULE_SETS_SERVLET, request, response);
		}
	}

	private void updateRuleSetRule(UserAccountBean ub, RuleSetRuleBean ruleSetRule, Status status) {
		ruleSetRule.setStatus(status);
		ruleSetRule.setUpdater(ub);
		ruleSetRule = getRuleSetRuleDao().saveOrUpdate(ruleSetRule);
		createRuleSetRuleAuditBean(ruleSetRule, ub, status);

	}

	private void createRuleSetRuleAuditBean(RuleSetRuleBean ruleSetRuleBean, UserAccountBean ub, Status status) {
		RuleSetRuleAuditBean ruleSetRuleAuditBean = new RuleSetRuleAuditBean();
		ruleSetRuleAuditBean.setRuleSetRuleBean(ruleSetRuleBean);
		ruleSetRuleAuditBean.setUpdater(ub);
		ruleSetRuleAuditBean.setStatus(status);
		getRuleSetRuleAuditDao().saveOrUpdate(ruleSetRuleAuditBean);
	}
}
