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
package org.akaza.openclinica.control.login;

import com.clinovo.util.ValidatorHelper;

import java.util.Date;

import org.akaza.openclinica.bean.login.PwdChallengeQuestion;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.control.SpringServletAccess;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.EmailEngine;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.core.SessionManager;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.akaza.openclinica.web.filter.OpenClinicaJdbcService;

/**
 * @author jxu
 * @version CVS: $Id: RequestPasswordServlet.java 9771 2007-08-28 15:26:26Z thickerson $
 * 
 *          Servlet of requesting password
 */
@SuppressWarnings("serial")
public class RequestPasswordServlet extends SecureController {

	@Override
	public void mayProceed() throws InsufficientPermissionException {

	}

	@Override
	public void processRequest() throws Exception {

		String action = request.getParameter("action");
		session.setAttribute("challengeQuestions", PwdChallengeQuestion.toArrayList());

		if (StringUtil.isBlank(action)) {
			request.setAttribute("userBean1", new UserAccountBean());
			forwardPage(Page.REQUEST_PWD);
		} else {
			if ("confirm".equalsIgnoreCase(action)) {
				confirmPassword();

			} else {
				request.setAttribute("userBean1", new UserAccountBean());
				forwardPage(Page.REQUEST_PWD);
			}
		}

	}

	/**
	 * 
	 * @param request
	 * @param response
	 */
	private void confirmPassword() throws Exception {
		Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));
		FormProcessor fp = new FormProcessor(request);
		v.addValidation("name", Validator.NO_BLANKS);
		v.addValidation("email", Validator.IS_A_EMAIL);
		v.addValidation("passwdChallengeQuestion", Validator.NO_BLANKS);
		v.addValidation("passwdChallengeAnswer", Validator.NO_BLANKS);

		errors = v.validate();

		UserAccountBean ubForm = new UserAccountBean(); // user bean from web
		// form
		ubForm.setName(fp.getString("name"));
		ubForm.setEmail(fp.getString("email"));
		ubForm.setPasswdChallengeQuestion(fp.getString("passwdChallengeQuestion"));
		ubForm.setPasswdChallengeAnswer(fp.getString("passwdChallengeAnswer"));

		sm = new SessionManager(null, ubForm.getName(), SpringServletAccess.getApplicationContext(context));

		UserAccountDAO uDAO = new UserAccountDAO(sm.getDataSource());
		// see whether this user in the DB
		UserAccountBean ubDB = (UserAccountBean) uDAO.findByUserName(ubForm.getName());

		UserAccountBean updater = ubDB;

		request.setAttribute("userBean1", ubForm);
		if (!errors.isEmpty()) {
			logger.info("after processing form,has errors");
			request.setAttribute("formMessages", errors);
			forwardPage(Page.REQUEST_PWD);
		} else {
			logger.info("after processing form,no errors");
			// whether this user's email is in the DB
			if (ubDB.getEmail() != null && ubDB.getEmail().equalsIgnoreCase(ubForm.getEmail())) {
				logger.info("ubDB.getPasswdChallengeQuestion()" + ubDB.getPasswdChallengeQuestion());
				logger.info("ubForm.getPasswdChallengeQuestion()" + ubForm.getPasswdChallengeQuestion());
				logger.info("ubDB.getPasswdChallengeAnswer()" + ubDB.getPasswdChallengeAnswer());
				logger.info("ubForm.getPasswdChallengeAnswer()" + ubForm.getPasswdChallengeAnswer());

				// if this user's password challenge can be verified
				if (ubDB.getPasswdChallengeQuestion().equals(ubForm.getPasswdChallengeQuestion())
						&& ubDB.getPasswdChallengeAnswer().equalsIgnoreCase(ubForm.getPasswdChallengeAnswer())) {

					SecurityManager sm = ((SecurityManager) SpringServletAccess.getApplicationContext(context).getBean(
							"securityManager"));
					String newPass = sm.genPassword();
					OpenClinicaJdbcService ocService = ((OpenClinicaJdbcService) SpringServletAccess
							.getApplicationContext(context).getBean("ocUserDetailsService"));
					String newDigestPass = sm.encrytPassword(newPass, ocService.loadUserByUsername(ubForm.getName()));
					ubDB.setPasswd(newDigestPass);

					ubDB.setPasswdTimestamp(null);
					ubDB.setUpdater(updater);
					ubDB.setLastVisitDate(new Date());

					logger.info("user bean to be updated:" + ubDB.getId() + ubDB.getName() + ubDB.getActiveStudyId());

					uDAO.update(ubDB);
					sendPassword(newPass, ubDB);
				} else {
					addPageMessage(respage.getString("your_password_not_verified_try_again"));
					forwardPage(Page.REQUEST_PWD);
				}

			} else {
				addPageMessage(respage.getString("your_email_address_not_found_try_again"));
				forwardPage(Page.REQUEST_PWD);
			}

		}

	}

	/**
	 * Gets user basic info and set email to the administrator
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("rawtypes")
	private void sendPassword(String passwd, UserAccountBean ubDB) throws Exception {
		
		StudyDAO sdao = new StudyDAO(sm.getDataSource());
		StudyBean sBean = (StudyBean) sdao.findByPK(ubDB.getActiveStudyId());
		logger.info("Sending email...");
		
		StringBuffer email = new StringBuffer("Dear " + ubDB.getFirstName() + " " + ubDB.getLastName() + ", <br><br>");
		email.append(restext.getString("this_email_is_from_openclinica_admin") + "<br>")
				.append(restext.getString("your_password_has_been_reset_as") + ": " + passwd)
				.append("<br><br>" + restext.getString("you_will_be_required_to_change"))
				.append(restext.getString("time_you_login_to_the_system"))
				.append(restext.getString("use_the_following_link_to_log") + ":<br>")
				.append(SQLInitServlet.getSystemURL() + "<br><br>")
				.append(respage.getString("best_system_administrator"));
		StudyBean emailParentStudy = new StudyBean();
		if (sBean.getParentStudyId() > 0) {
			emailParentStudy = (StudyBean) sdao.findByPK(sBean.getParentStudyId());
		} else {
			emailParentStudy = sBean;
		}
		String emailBody = email.toString();
		emailBody = emailBody.replace("{0}", emailParentStudy.getName());
		sendEmail(ubDB.getEmail().trim(), EmailEngine.getAdminEmail(), restext.getString("your_openclinica_password"),
				emailBody, true, respage.getString("your_password_reset_new_password_emailed"),
				respage.getString("your_password_not_send_due_mail_server_problem"), true);

		session.removeAttribute("challengeQuestions");
		forwardPage(Page.LOGIN);

	}
}
