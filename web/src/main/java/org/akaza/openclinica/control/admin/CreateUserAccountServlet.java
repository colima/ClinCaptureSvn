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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.TermType;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.hibernate.AuthoritiesDao;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.domain.user.AuthoritiesBean;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.springframework.stereotype.Component;

/**
 * Servlet for creating a user account.
 * 
 * @author ssachs
 */
@SuppressWarnings({ "unchecked", "rawtypes", "serial" })
@Component
public class CreateUserAccountServlet extends Controller {

	public static final String SHOW_EXIT_INSTEAD_OF_BACK = "showExitInsteadOfBack";
	public static final String INPUT_USERNAME = "userName";
	public static final String INPUT_FIRST_NAME = "firstName";
	public static final String INPUT_LAST_NAME = "lastName";
	public static final String INPUT_EMAIL = "email";
	public static final String INPUT_PHONE = "phone";
	public static final String INPUT_INSTITUTION = "institutionalAffiliation";
	public static final String INPUT_STUDY = "activeStudy";
	public static final String INPUT_ROLE = "role";
	public static final String INPUT_TYPE = "type";
	public static final String INPUT_DISPLAY_PWD = "displayPwd";
	public static final String INPUT_RUN_WEBSERVICES = "runWebServices";
	public static final String USER_ACCOUNT_NOTIFICATION = "notifyPassword";

	@Override
	protected void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);

		if (!ub.isSysAdmin()) {
			throw new InsufficientPermissionException(Page.MENU,
					resexception.getString("you_may_not_perform_administrative_functions"), "1");
		}
	}

	@Override
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);

		FormProcessor fp = new FormProcessor(request);

		StudyDAO sdao = getStudyDAO();
		ArrayList<StudyBean> all = (ArrayList<StudyBean>) sdao.findAll();
		ArrayList<StudyBean> finalList = new ArrayList<StudyBean>();
		for (StudyBean sb : all) {
			if (!(sb.getParentStudyId() > 0)) {
				finalList.add(sb);
				finalList.addAll(sdao.findAllByParent(sb.getId()));
			}
		}
		addEntityList("studies", finalList, respage.getString("a_user_cannot_be_created_no_study_as_active"),
				Page.ADMIN_SYSTEM, request, response);

		// for warning window for back button
		String pageIsChanged = request.getParameter("pageIsChanged");
		if (pageIsChanged != null) {
			request.setAttribute("pageIsChanged", pageIsChanged);
		}

		StudyParameterValueDAO dao = new StudyParameterValueDAO(getDataSource());
		StudyParameterValueBean allowCodingVerification = dao.findByHandleAndStudy(currentStudy.getId(),
				"allowCodingVerification");

		Map roleMap;
		if (allowCodingVerification.getValue().equalsIgnoreCase("yes")) {
			roleMap = Role.roleMapWithDescriptions;
		} else {
			Role.roleMapWithDescriptions.remove(7);
			roleMap = Role.roleMapWithDescriptions;
		}

		ArrayList types = UserType.toArrayList();
		types.remove(UserType.INVALID);
		types.remove(UserType.TECHADMIN);
		addEntityList("types", types, respage.getString("a_user_cannot_be_created_no_user_types_for"),
				Page.ADMIN_SYSTEM, request, response);

		Boolean changeRoles = request.getParameter("changeRoles") != null
				&& Boolean.parseBoolean(request.getParameter("changeRoles"));
		int activeStudy = fp.getInt(INPUT_STUDY);
		StudyBean study = (StudyBean) sdao.findByPK(activeStudy);
		request.setAttribute("roles", roleMap);
		request.setAttribute("activeStudy", activeStudy);
		request.setAttribute("isThisStudy", !(study.getParentStudyId() > 0));

		if (!fp.isSubmitted() || changeRoles) {
			String textFields[] = { INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL,
					INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
			fp.setCurrentStringValuesAsPreset(textFields);

			String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE, INPUT_TYPE, INPUT_RUN_WEBSERVICES };
			fp.setCurrentIntValuesAsPreset(ddlbFields);

			HashMap presetValues = fp.getPresetValues();
			String sendPwd = SQLInitServlet.getField("user_account_notification");
			fp.addPresetValue(USER_ACCOUNT_NOTIFICATION, sendPwd);
			//
			setPresetValues(presetValues, request);
			request.setAttribute("pageIsChanged", changeRoles);
			forwardPage(Page.CREATE_ACCOUNT, request, response);
		} else {
			UserType type = UserType.get(fp.getInt("type"));

			UserAccountDAO udao = new UserAccountDAO(getDataSource());
			Validator v = new Validator(new ValidatorHelper(request, getConfigurationDao()));

			// username must not be blank,
			// must be in the format specified by Validator.USERNAME,
			// and must be unique
			v.addValidation(INPUT_USERNAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_USERNAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 64);
			v.addValidation(INPUT_USERNAME, Validator.IS_A_USERNAME);

			v.addValidation(INPUT_USERNAME, Validator.USERNAME_UNIQUE, udao);

			v.addValidation(INPUT_FIRST_NAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_LAST_NAME, Validator.NO_BLANKS);
			v.addValidation(INPUT_FIRST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);
			v.addValidation(INPUT_LAST_NAME, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 50);

			v.addValidation(INPUT_EMAIL, Validator.NO_BLANKS);
			v.addValidation(INPUT_EMAIL, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 120);
			v.addValidation(INPUT_EMAIL, Validator.IS_A_EMAIL);

			v.addValidation(INPUT_INSTITUTION, Validator.NO_BLANKS);
			v.addValidation(INPUT_INSTITUTION, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

			v.addValidation(INPUT_STUDY, Validator.ENTITY_EXISTS, sdao);
			v.addValidation(INPUT_ROLE, Validator.IS_VALID_TERM, TermType.ROLE);

			HashMap errors = v.validate();

			if (errors.isEmpty()) {
				UserAccountBean createdUserAccountBean = new UserAccountBean();
				createdUserAccountBean.setName(fp.getString(INPUT_USERNAME));
				createdUserAccountBean.setFirstName(fp.getString(INPUT_FIRST_NAME));
				createdUserAccountBean.setLastName(fp.getString(INPUT_LAST_NAME));
				createdUserAccountBean.setEmail(fp.getString(INPUT_EMAIL));
				createdUserAccountBean.setPhone(fp.getString(INPUT_PHONE));
				createdUserAccountBean.setInstitutionalAffiliation(fp.getString(INPUT_INSTITUTION));

				SecurityManager secm = getSecurityManager();
				String password = secm.genPassword();
				String passwordHash = secm.encrytPassword(password, getUserDetails());

				createdUserAccountBean.setPasswd(passwordHash);

				createdUserAccountBean.setPasswdTimestamp(null);
				createdUserAccountBean.setLastVisitDate(null);

				createdUserAccountBean.setStatus(Status.AVAILABLE);
				createdUserAccountBean.setPasswdChallengeQuestion("");
				createdUserAccountBean.setPasswdChallengeAnswer("");
				createdUserAccountBean.setOwner(ub);
				createdUserAccountBean.setRunWebservices(fp.getBoolean(INPUT_RUN_WEBSERVICES));

				int studyId = fp.getInt(INPUT_STUDY);
				int roleId = fp.getInt(INPUT_ROLE);

				createdUserAccountBean = addActiveStudyRole(request, createdUserAccountBean, studyId, Role.get(roleId));

				logger.warn("*** found type: " + fp.getInt("type"));
				logger.warn("*** setting type: " + type.getDescription());
				createdUserAccountBean.addUserType(type);
				createdUserAccountBean = (UserAccountBean) udao.create(createdUserAccountBean);
				AuthoritiesDao authoritiesDao = getAuthoritiesDao();
				authoritiesDao.saveOrUpdate(new AuthoritiesBean(createdUserAccountBean.getName()));
				String displayPwd = fp.getString(INPUT_DISPLAY_PWD);

				if (createdUserAccountBean.isActive()) {
					addPageMessage(respage.getString("the_user_account") + "\"" + createdUserAccountBean.getName()
							+ "\"" + respage.getString("was_created_succesfully"), request);
					if ("no".equalsIgnoreCase(displayPwd)) {
						try {
							StudyBean emailParentStudy;
							if (currentStudy.getParentStudyId() > 0) {
								emailParentStudy = (StudyBean) sdao.findByPK(currentStudy.getParentStudyId());
							} else {
								emailParentStudy = currentStudy;
							}
							sendNewAccountEmail(request, createdUserAccountBean, password, emailParentStudy.getName());
						} catch (Exception e) {
							addPageMessage(respage.getString("there_was_an_error_sending_account_creating_mail"),
									request);
						}
					} else {
						addPageMessage(
								respage.getString("user_password") + ":<br/>" + password + "<br/> "
										+ respage.getString("please_write_down_the_password_and_provide"), request);
					}
				} else {
					addPageMessage(respage.getString("the_user_account") + "\"" + createdUserAccountBean.getName()
							+ "\"" + respage.getString("could_not_created_due_database_error"), request);
				}
				if (createdUserAccountBean.isActive()) {
					request.setAttribute(ViewUserAccountServlet.ARG_USER_ID,
							Integer.toString(createdUserAccountBean.getId()));
					request.setAttribute(SHOW_EXIT_INSTEAD_OF_BACK, true);
					forwardPage(Page.VIEW_USER_ACCOUNT_SERVLET, request, response);
				} else {
					forwardPage(Page.LIST_USER_ACCOUNTS_SERVLET, request, response);
				}
			} else {
				String textFields[] = { INPUT_USERNAME, INPUT_FIRST_NAME, INPUT_LAST_NAME, INPUT_PHONE, INPUT_EMAIL,
						INPUT_INSTITUTION, INPUT_DISPLAY_PWD };
				fp.setCurrentStringValuesAsPreset(textFields);

				String ddlbFields[] = { INPUT_STUDY, INPUT_ROLE, INPUT_TYPE, INPUT_RUN_WEBSERVICES };
				fp.setCurrentIntValuesAsPreset(ddlbFields);

				HashMap presetValues = fp.getPresetValues();
				setPresetValues(presetValues, request);

				setInputMessages(errors, request);
				addPageMessage(
						respage.getString("there_were_some_errors_submission")
								+ respage.getString("see_below_for_details"), request);

				forwardPage(Page.CREATE_ACCOUNT, request, response);
			}
		}
	}

	private UserAccountBean addActiveStudyRole(HttpServletRequest request, UserAccountBean createdUserAccountBean,
			int studyId, Role r) {
		UserAccountBean ub = getUserAccountBean(request);
		createdUserAccountBean.setActiveStudyId(studyId);

		StudyUserRoleBean activeStudyRole = new StudyUserRoleBean();

		activeStudyRole.setStudyId(studyId);
		activeStudyRole.setRoleName(r.getName());
		activeStudyRole.setStatus(Status.AVAILABLE);
		activeStudyRole.setOwner(ub);

		createdUserAccountBean.addRole(activeStudyRole);

		return createdUserAccountBean;
	}

	private void sendNewAccountEmail(HttpServletRequest request, UserAccountBean createdUserAccountBean,
			String password, String studyName) throws Exception {
		logger.info("Sending account creation notification to " + createdUserAccountBean.getName());
		String body = "<html><body>";
		body += resword.getString("dear") + " " + createdUserAccountBean.getFirstName() + " "
				+ createdUserAccountBean.getLastName() + ",<br><br>";
		body += restext.getString("a_new_user_account_has_been_created_for_you") + "<br><br>";
		body += resword.getString("user_name") + ": " + createdUserAccountBean.getName() + "<br>";
		body += resword.getString("password") + ": " + password + "<br><br>";
		body += restext.getString("please_test_your_login_information_and_let") + "<br>";
		body += SQLInitServlet.getSystemURL();
		body += " . <br><br> ";
		body += respage.getString("best_system_administrator").replace("{0}", studyName);
		body += "</body></html>";
		sendEmail(createdUserAccountBean.getEmail().trim(), restext.getString("your_new_openclinica_account"), body,
				false, request);
	}

	@Override
	protected String getAdminServlet(HttpServletRequest request) {
		return Controller.ADMIN_SERVLET_CODE;
	}
}
