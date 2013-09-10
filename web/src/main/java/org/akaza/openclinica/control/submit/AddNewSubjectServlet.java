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
package org.akaza.openclinica.control.submit;

// import org.akaza.openclinica.bean.core.Role;
import com.clinovo.util.ValidatorHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.akaza.openclinica.bean.core.DiscrepancyNoteType;
import org.akaza.openclinica.bean.core.NumericComparisonOperator;
import org.akaza.openclinica.bean.core.ResolutionStatus;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.core.SubjectEventStatus;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudyGroupClassBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.bean.service.StudyParameterValueBean;
import org.akaza.openclinica.bean.submit.DisplaySubjectBean;
import org.akaza.openclinica.bean.submit.SubjectBean;
import org.akaza.openclinica.bean.submit.SubjectGroupMapBean;
import org.akaza.openclinica.control.core.SecureController;
import org.akaza.openclinica.control.form.DiscrepancyValidator;
import org.akaza.openclinica.control.form.FormDiscrepancyNotes;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.managestudy.DiscrepancyNoteDAO;
import org.akaza.openclinica.dao.managestudy.StudyDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDAO;
import org.akaza.openclinica.dao.managestudy.StudyEventDefinitionDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupClassDAO;
import org.akaza.openclinica.dao.managestudy.StudyGroupDAO;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.dao.service.StudyParameterValueDAO;
import org.akaza.openclinica.dao.submit.SubjectDAO;
import org.akaza.openclinica.dao.submit.SubjectGroupMapDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// import javax.servlet.http.*;

/**
 * Enroll a new subject into system
 * 
 * @author ssachs
 * @version CVS: $Id: AddNewSubjectServlet.java,v 1.15 2005/07/05 17:20:43 jxu Exp $
 */
@SuppressWarnings({"rawtypes", "unchecked", "serial"})
public class AddNewSubjectServlet extends SecureController {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private final Object simpleLockObj = new Object();
	public static final String INPUT_UNIQUE_IDENTIFIER = "uniqueIdentifier";// global

	public static final String INPUT_DOB = "dob";

	public static final String INPUT_YOB = "yob"; // year of birth

	public static final String INPUT_GENDER = "gender";

	public static final String INPUT_LABEL = "label";

	public static final String INPUT_SECONDARY_LABEL = "secondaryLabel";

	public static final String INPUT_ENROLLMENT_DATE = "enrollmentDate";

	public static final String INPUT_EVENT_START_DATE = "startDate";

	public static final String INPUT_GROUP = "group";

	public static final String INPUT_FATHER = "father";

	public static final String INPUT_MOTHER = "mother";

	public static final String BEAN_GROUPS = "groups";
	
	public static final String BEAN_DYNAMIC_GROUPS = "dynamicGroups";

	public static final String BEAN_FATHERS = "fathers";

	public static final String BEAN_MOTHERS = "mothers";

	public static final String SUBMIT_EVENT_BUTTON = "submitEvent";

	public static final String SUBMIT_ENROLL_BUTTON = "submitEnroll";

	public static final String SUBMIT_DONE_BUTTON = "submitDone";

	public static final String EDIT_DOB = "editDob";

	public static final String EXISTING_SUB_SHOWN = "existingSubShown";

	public static final String FORM_DISCREPANCY_NOTES_NAME = "fdnotes";

	public static final String STUDY_EVENT_DEFINITION = "studyEventDefinition";
	public static final String LOCATION = "location";
	public static final String SELECTED_DYN_GROUP_CLASS_ID = "selectedDynGroupClassId";
	public static final String DEFAULT_DYN_GROUP_CLASS_ID = "defaultDynGroupClassId";
	public static final String DEFAULT_DYN_GROUP_CLASS_NAME = "defaultDynGroupClassName";

	String DOB = "";
	String YOB = "";
	String GENDER = "";
	public static final String G_WARNING = "gWarning";
	public static final String D_WARNING = "dWarning";
	public static final String Y_WARNING = "yWarning";
	boolean needUpdate;
	SubjectBean updateSubject = new SubjectBean();

	@SuppressWarnings("deprecation")
	@Override
	protected void processRequest() throws Exception {

		checkStudyLocked(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_locked"));
		checkStudyFrozen(Page.LIST_STUDY_SUBJECTS, respage.getString("current_study_frozen"));

		StudySubjectDAO ssd = new StudySubjectDAO(sm.getDataSource());
		StudyDAO stdao = new StudyDAO(sm.getDataSource());
		StudyGroupClassDAO sgcdao = new StudyGroupClassDAO(sm.getDataSource());
		ArrayList classes = new ArrayList();
		ArrayList dynamicClasses = new ArrayList();
		int defaultDynGroupClassId = 0;
		String defaultDynGroupClassName = "";
		panel.setStudyInfoShown(false);
		FormProcessor fp = new FormProcessor(request);
		FormDiscrepancyNotes discNotes;

		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		// TODO l10n for dates? Note that in some places we hard-code the YOB by
		// using "01/01/"+yob,
		// not exactly supporting i18n...tbh

		// Update study parameters of current study.
		// "collectDob" and "genderRequired" are set as the same as the parent
		// study
		int parentStudyId = currentStudy.getParentStudyId();
		if (parentStudyId <= 0) {
			parentStudyId = currentStudy.getId();
			classes = sgcdao.findAllActiveByStudy(currentStudy, true);
			dynamicClasses = getDynamicGroupClassesByStudyId(currentStudy.getId());
		} else {
			StudyBean parentStudy = (StudyBean) stdao.findByPK(parentStudyId);
			classes = sgcdao.findAllActiveByStudy(parentStudy, true);
			dynamicClasses = getDynamicGroupClassesByStudyId(parentStudyId);
		}
		
		if (dynamicClasses.size() > 0) {
			if (((StudyGroupClassBean) dynamicClasses.get(0)).isDefault()){
				defaultDynGroupClassId = ((StudyGroupClassBean) dynamicClasses.get(0)).getId();
				defaultDynGroupClassName = ((StudyGroupClassBean) dynamicClasses.get(0)).getName();
			}
		}
		fp.addPresetValue(DEFAULT_DYN_GROUP_CLASS_ID, defaultDynGroupClassId);
		fp.addPresetValue(DEFAULT_DYN_GROUP_CLASS_NAME, defaultDynGroupClassName);
		
		StudyParameterValueDAO spvdao = new StudyParameterValueDAO(sm.getDataSource());
		StudyParameterValueBean parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "collectDob");
		currentStudy.getStudyParameterConfig().setCollectDob(parentSPV.getValue());
		parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "genderRequired");
		currentStudy.getStudyParameterConfig().setGenderRequired(parentSPV.getValue());
		
		StudyParameterValueBean checkPersonId = spvdao.findByHandleAndStudy(parentStudyId, "subjectPersonIdRequired");
		currentStudy.getStudyParameterConfig().setSubjectPersonIdRequired(checkPersonId.getValue());

		if (!fp.isSubmitted()) {
			if (fp.getBoolean("instr")) {
				session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
				forwardPage(Page.INSTRUCTIONS_ENROLL_SUBJECT);
			} else {

				setUpBeans(classes);
				request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);
				
				Date today = new Date(System.currentTimeMillis());
				String todayFormatted = local_df.format(today);
				fp.addPresetValue(INPUT_ENROLLMENT_DATE, todayFormatted);

				String idSetting = "";
				if (currentStudy.getParentStudyId() > 0) {
					parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
					currentStudy.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
				}
				idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
				logger.info("subject id setting :" + idSetting);
				// set up auto study subject id
				// If idSetting is auto, do not calculate the next
				// available ID (label) for now
				if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {

					String nextLabel = ssd.findNextLabel(currentStudy.getIdentifier());
					fp.addPresetValue(INPUT_LABEL, nextLabel);

				}

				setPresetValues(fp.getPresetValues());
				discNotes = new FormDiscrepancyNotes();
				session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, discNotes);
				forwardPage(Page.ADD_NEW_SUBJECT);

			}
		} else {// submitted

			// Record parameters' values on input page so those values
			// could be used to compare against
			// values in database <subject> table for "add existing subject"
			if (!fp.getBoolean(EXISTING_SUB_SHOWN)) {
				DOB = fp.getString(INPUT_DOB);
				YOB = fp.getString(INPUT_YOB);
				GENDER = fp.getString(INPUT_GENDER);
			}
			
			discNotes = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
			if (discNotes == null) {
				discNotes = new FormDiscrepancyNotes();
			}
			DiscrepancyValidator v = new DiscrepancyValidator(new ValidatorHelper(request, getConfigurationDao()),
					discNotes);

			v.addValidation(INPUT_LABEL, Validator.NO_BLANKS);

			String subIdSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
			if (!subIdSetting.equalsIgnoreCase("auto non-editable") && !subIdSetting.equalsIgnoreCase("auto editable")) {
				v.addValidation(INPUT_LABEL, Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
			}

			if (currentStudy.getStudyParameterConfig().getSubjectPersonIdRequired().equals("required")) {
				v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.NO_BLANKS);
			}
			v.addValidation(INPUT_UNIQUE_IDENTIFIER, Validator.LENGTH_NUMERIC_COMPARISON,
					NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 255);

			if (!StringUtil.isBlank(fp.getString(INPUT_SECONDARY_LABEL))) {
				v.addValidation(INPUT_SECONDARY_LABEL, Validator.LENGTH_NUMERIC_COMPARISON,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, 30);
			}

			String dobSetting = currentStudy.getStudyParameterConfig().getCollectDob();
			if (dobSetting.equals("1")) {// date of birth
				v.addValidation(INPUT_DOB, Validator.IS_A_DATE);
				if (!StringUtil.isBlank(fp.getString("INPUT_DOB"))) {
					v.alwaysExecuteLastValidation(INPUT_DOB);
				}
				v.addValidation(INPUT_DOB, Validator.DATE_IN_PAST);
			} else if (dobSetting.equals("2")) {// year of birth
				v.addValidation(INPUT_YOB, Validator.IS_AN_INTEGER);
				v.alwaysExecuteLastValidation(INPUT_YOB);
				v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
						NumericComparisonOperator.GREATER_THAN_OR_EQUAL_TO, 1000);

				// get today's year
				Date today = new Date();
				Calendar c = Calendar.getInstance();
				c.setTime(today);
				int currentYear = c.get(Calendar.YEAR);
				v.addValidation(INPUT_YOB, Validator.COMPARES_TO_STATIC_VALUE,
						NumericComparisonOperator.LESS_THAN_OR_EQUAL_TO, currentYear);
			} else { // DOB not used, added
				logger.info("should read this only if DOB not used");
			}

			ArrayList acceptableGenders = new ArrayList();
			acceptableGenders.add("m");
			acceptableGenders.add("f");

			if (!currentStudy.getStudyParameterConfig().getGenderRequired().equals("false")) {
				v.addValidation(INPUT_GENDER, Validator.IS_IN_SET, acceptableGenders);
			}

			if (currentStudy.getStudyParameterConfig().getDateOfEnrollmentForStudyRequired().equals("yes")) {
				v.addValidation("enrollmentDate", Validator.NO_BLANKS);
			}
			if (currentStudy.getStudyParameterConfig().getSecondaryIdRequired().equals("yes")) {
				v.addValidation("secondaryLabel", Validator.NO_BLANKS);
			}

			if (!"".equals(fp.getDateTimeInputString("enrollment"))) {
				v.addValidation(INPUT_ENROLLMENT_DATE, Validator.IS_A_DATE);
				v.alwaysExecuteLastValidation(INPUT_ENROLLMENT_DATE);
				v.addValidation(INPUT_ENROLLMENT_DATE, Validator.DATE_IN_PAST);
			}

			boolean locationError = false;
			if (fp.getBoolean("addWithEvent")) {
				if (!"".equals(fp.getDateTimeInputString("enrollment"))) {
					v.addValidation(INPUT_EVENT_START_DATE, Validator.IS_A_DATE);
					v.alwaysExecuteLastValidation(INPUT_EVENT_START_DATE);
				}
				if (currentStudy.getStudyParameterConfig().getEventLocationRequired().equalsIgnoreCase("required")) {
					v.addValidation("location", Validator.NO_BLANKS);
					locationError = true;
				}
			}

			HashMap errors = v.validate();

			SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
			String uniqueIdentifier = fp.getString(INPUT_UNIQUE_IDENTIFIER);// global
			// Id
			SubjectBean subjectWithSameId = new SubjectBean();
			boolean showExistingRecord = false;
			if (!uniqueIdentifier.equals("")) {
				boolean subjectWithSameIdInCurrentStudyTree = false;
				// checks whether there is a subject with same id inside current
				// study/site
				subjectWithSameId = sdao.findByUniqueIdentifierAndStudy(uniqueIdentifier, currentStudy.getId());
				
				if (subjectWithSameId.isActive()) { // ||
					Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
							resexception.getString("subject_with_person_ID") + " " + uniqueIdentifier + " "
									+ resexception.getString("is_already_enrolled_in_this_study"));

					subjectWithSameIdInCurrentStudyTree = true;
					logger.info("just added unique id in study tree");
				} else {
					// checks whether there is a subject with same id inside
					// sites of
					// current study
					subjectWithSameId = sdao.findByUniqueIdentifierAndParentStudy(uniqueIdentifier,
							currentStudy.getId());
					if (subjectWithSameId.isActive()) {
						StudySubjectBean ssub = ssd.findBySubjectIdAndStudy(subjectWithSameId.getId(), currentStudy);
						StudyBean site = (StudyBean) stdao.findByPK(ssub.getStudyId());
						Validator.addError(
								errors,
								INPUT_UNIQUE_IDENTIFIER,
								resexception.getString("this_subject_person_ID") + " " + uniqueIdentifier
										+ resexception.getString("has_already_enrolled_site") + site.getName()
										+ resexception.getString("of_current_study_need_to_move")
										+ resexception.getString("please_have_user_manage_privileges"));
						subjectWithSameIdInCurrentStudyTree = true;
					} else {
						// check whether there is a subject with same id in the
						// parent study
						subjectWithSameId = sdao.findByUniqueIdentifierAndStudy(uniqueIdentifier,
								currentStudy.getParentStudyId());
						if (subjectWithSameId.isActive()) {
							Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
									resexception.getString("this_subject_with_person_ID") + " " + uniqueIdentifier
											+ resexception.getString("has_already_enrolled_parent_study"));

							subjectWithSameIdInCurrentStudyTree = true;
						} else {
							// YW 11-26-2007 << check whether there is a subject
							// with the same id in other sites of the same study
							subjectWithSameId = sdao.findByUniqueIdentifierAndParentStudy(uniqueIdentifier,
									currentStudy.getParentStudyId());
							if (subjectWithSameId.isActive()) {
								Validator.addError(errors, INPUT_UNIQUE_IDENTIFIER,
										resexception.getString("this_subject_with_person_ID") + " " + uniqueIdentifier
												+ resexception.getString("has_already_enrolled_site_study"));

								subjectWithSameIdInCurrentStudyTree = true;
							}
							// YW >>
						}
					}
				}

				if (!subjectWithSameIdInCurrentStudyTree) {
					subjectWithSameId = sdao.findByUniqueIdentifier(uniqueIdentifier);
					// found subject with same id in other study
					if (subjectWithSameId.isActive()) {
						showExistingRecord = true;
					}
				}
			}// end of the block if(!uniqueIdentifier.equals(""))

			boolean insertWithParents = fp.getInt(INPUT_MOTHER) > 0 || fp.getInt(INPUT_FATHER) > 0;

			if (fp.getInt(INPUT_MOTHER) > 0) {
				SubjectBean mother = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_MOTHER));
				if (mother == null || !mother.isActive() || mother.getGender() != 'f') {
					Validator.addError(errors, INPUT_MOTHER,
							resexception.getString("please_choose_valid_female_subject_as_mother"));
				}
			}

			if (fp.getInt(INPUT_FATHER) > 0) {
				SubjectBean father = (SubjectBean) sdao.findByPK(fp.getInt(INPUT_FATHER));
				if (father == null || !father.isActive() || father.getGender() != 'm') {
					Validator.addError(errors, INPUT_FATHER,
							resexception.getString("please_choose_valid_male_subject_as_father"));
				}
			}

			String label = fp.getString(INPUT_LABEL).trim();
			// Shaoyu Su: if the form submitted for field "INPUT_LABEL" has
			// value of "AUTO_LABEL",
			// then Study Subject ID should be created when db row is inserted.
			if (!label.equalsIgnoreCase(resword.getString("id_generated_Save_Add"))) {
				StudySubjectBean subjectWithSameLabel = ssd.findByLabelAndStudy(label, currentStudy);

				StudySubjectBean subjectWithSameLabelInParent = new StudySubjectBean();
				// tbh
				if (currentStudy.getParentStudyId() > 0) {
					subjectWithSameLabelInParent = ssd.findSameByLabelAndStudy(label, currentStudy.getParentStudyId(),
							0);// <
					// --
					// blank
					// id
					// since
					// the
					// ss
					// hasn't
					// been
					// created
					// yet,
					// tbh
				}
				// tbh
				if (subjectWithSameLabel.isActive() || subjectWithSameLabelInParent.isActive()) {
					Validator.addError(errors, INPUT_LABEL,
							resexception.getString("another_assigned_this_ID_choose_unique"));
				}
			}

			if (!classes.isEmpty()) {
				for (int i = 0; i < classes.size(); i++) {
					StudyGroupClassBean sgc = (StudyGroupClassBean) classes.get(i);
					int groupId = fp.getInt("studyGroupId" + i);
					String notes = fp.getString("notes" + i);

					if ("Required".equals(sgc.getSubjectAssignment()) && groupId == 0) {
						Validator.addError(errors, "studyGroupId" + i,
								resexception.getString("group_class_is_required"));
					}
					if (notes.trim().length() > 255) {
						Validator.addError(errors, "notes" + i, resexception.getString("notes_cannot_longer_255"));
					}
					sgc.setStudyGroupId(groupId);
					sgc.setGroupNotes(notes);
				}
			}

			if (!errors.isEmpty()) {

				addPageMessage(respage.getString("there_were_some_errors_submission"));
				if (locationError) {
					addPageMessage(respage.getString("location_blank_error"));
				}

				setInputMessages(errors);
				fp.addPresetValue(INPUT_DOB, fp.getString(INPUT_DOB));
				fp.addPresetValue(INPUT_YOB, fp.getString(INPUT_YOB));
				fp.addPresetValue(INPUT_GENDER, fp.getString(INPUT_GENDER));
				fp.addPresetValue(INPUT_UNIQUE_IDENTIFIER, uniqueIdentifier);
				fp.addPresetValue(INPUT_LABEL, label);
				fp.addPresetValue(INPUT_SECONDARY_LABEL, fp.getString(INPUT_SECONDARY_LABEL));
				fp.addPresetValue(INPUT_ENROLLMENT_DATE, fp.getString(INPUT_ENROLLMENT_DATE));
				fp.addPresetValue(INPUT_EVENT_START_DATE, fp.getString(INPUT_EVENT_START_DATE));
				fp.addPresetValue(STUDY_EVENT_DEFINITION, fp.getInt(STUDY_EVENT_DEFINITION));
				fp.addPresetValue(LOCATION, fp.getString(LOCATION));
				fp.addPresetValue(SELECTED_DYN_GROUP_CLASS_ID, request.getParameter("dynamicGroupClassId"));
				
				if (currentStudy.isGenetic()) {
					String intFields[] = { INPUT_GROUP, INPUT_FATHER, INPUT_MOTHER };
					fp.setCurrentIntValuesAsPreset(intFields);
				}
				fp.addPresetValue(EDIT_DOB, fp.getString(EDIT_DOB));
				setPresetValues(fp.getPresetValues());

				setUpBeans(classes);
				request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);
				boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);

				if (!existingSubShown) {
					Object isSubjectOverlay = fp.getRequest().getParameter("subjectOverlay");
					if (isSubjectOverlay != null) {
						int eventId = fp.getInt("studyEventDefinition");
						if (eventId < 1) {
							Validator.addError(errors, STUDY_EVENT_DEFINITION,
									resexception.getString("input_not_acceptable_option"));
						}
						String location = fp.getString(LOCATION);
						if (location == null || location.length() == 0) {
							Validator.addError(errors, LOCATION, resexception.getString("field_not_blank"));
						}
						request.setAttribute("showOverlay", true);
						forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
					} else {
						forwardPage(Page.ADD_NEW_SUBJECT);
					}
				} else {
					forwardPage(Page.ADD_EXISTING_SUBJECT);
				}
			} else {
				// no errors
				StudySubjectBean studySubject = new StudySubjectBean();
				SubjectBean subject = new SubjectBean();
				boolean existingSubShown = fp.getBoolean(EXISTING_SUB_SHOWN);

				if (showExistingRecord && !existingSubShown) {
					needUpdate = false;
					subject = subjectWithSameId;
					Calendar cal = Calendar.getInstance();
					int year = 0;
					if (subject.getDateOfBirth() != null) {
						cal.setTime(subject.getDateOfBirth());
						year = cal.get(Calendar.YEAR);
						fp.addPresetValue(INPUT_DOB, local_df.format(subject.getDateOfBirth()));
					} else {
						fp.addPresetValue(INPUT_DOB, "");
					}

					if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1") && !subject.isDobCollected()) {
						// fp.addPresetValue(EDIT_DOB, "yes");
						fp.addPresetValue(INPUT_DOB, fp.getString(INPUT_DOB));
					}
					// YW << it has been taken off to solve bug0001125
					/*
					 * else { fp.addPresetValue(INPUT_DOB, ""); }
					 */
					// YW >>
					fp.addPresetValue(INPUT_YOB, String.valueOf(year));

					if (!currentStudy.getStudyParameterConfig().getGenderRequired().equals("false")) {
						fp.addPresetValue(INPUT_GENDER, subject.getGender() + "");
					} else {
						fp.addPresetValue(INPUT_GENDER, "");
					}

					// YW <<
					// Shaoyu Su: delay setting INPUT_LABEL field
					if (!label.equalsIgnoreCase(resword.getString("id_generated_Save_Add"))) {
						fp.addPresetValue(INPUT_LABEL, label);
					}
					fp.addPresetValue(INPUT_SECONDARY_LABEL, fp.getString(INPUT_SECONDARY_LABEL));
					fp.addPresetValue(INPUT_ENROLLMENT_DATE, fp.getString(INPUT_ENROLLMENT_DATE));
					fp.addPresetValue(INPUT_EVENT_START_DATE, fp.getString(INPUT_EVENT_START_DATE));
					// YW >>

					fp.addPresetValue(INPUT_UNIQUE_IDENTIFIER, subject.getUniqueIdentifier());

					fp.addPresetValue(INPUT_FATHER, subject.getFatherId());
					fp.addPresetValue(INPUT_MOTHER, subject.getMotherId());

					setPresetValues(fp.getPresetValues());
					setUpBeans(classes);
					request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);

					// YW <<
					int warningCount = 0;
					if (currentStudy.getStudyParameterConfig().getGenderRequired().equalsIgnoreCase("true")) {
						if (String.valueOf(subjectWithSameId.getGender()).equals(" ")) {
							fp.addPresetValue(G_WARNING, "emptytrue");
							fp.addPresetValue(INPUT_GENDER, GENDER);
							needUpdate = true;
							updateSubject = subjectWithSameId;
							updateSubject.setGender(GENDER.toCharArray()[0]);
							warningCount++;
						} else if (!String.valueOf(subjectWithSameId.getGender()).equals(GENDER)) {
							fp.addPresetValue(G_WARNING, "true");
							warningCount++;
						} else {
							fp.addPresetValue(G_WARNING, "false");
						}
					} else {
						fp.addPresetValue(G_WARNING, "false");
					}

					// Current study required DOB
					if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
						// date-of-birth in subject table is not completed
						if (subjectWithSameId.isDobCollected() == false) {
							needUpdate = true;
							updateSubject = subjectWithSameId;
							updateSubject.setDobCollected(true);

							if (subjectWithSameId.getDateOfBirth() == null) {
								fp.addPresetValue(INPUT_DOB, DOB);
								updateSubject.setDateOfBirth(new Date(DOB));
							} else {
								String y = String.valueOf(subjectWithSameId.getDateOfBirth()).split("\\-")[0];
								String[] d = DOB.split("\\/");
								// if year-of-birth in subject table
								if (!y.equals("0001")) {
									// if year-of-birth is different from DOB's
									// year, use year-of-birth
									if (!y.equals(d[2])) {
										fp.addPresetValue(D_WARNING, "dobYearWrong");
										fp.addPresetValue(INPUT_DOB, d[0] + "/" + d[1] + "/" + y);
										updateSubject.setDateOfBirth(sdf.parse(d[0] + "/" + d[1] + "/" + y));
									} else {
										fp.addPresetValue(D_WARNING, "dobUsed");
										fp.addPresetValue(INPUT_DOB, DOB);
										updateSubject.setDateOfBirth(sdf.parse(DOB));
									}
								}
								// date-of-birth is not required in subject
								// table
								else {
									fp.addPresetValue(D_WARNING, "emptyD");
									fp.addPresetValue(INPUT_DOB, DOB);
									updateSubject.setDateOfBirth(sdf.parse(DOB));
								}
							}
							warningCount++;
						}
						// date-of-birth in subject table but doesn't match DOB
						else if (!local_df.format(subjectWithSameId.getDateOfBirth()).toString().equals(DOB)) {
							// System.out.println("comparing " +
							// local_df.format(
							// subjectWithSameId.getDateOfBirth()).toString());
							fp.addPresetValue(D_WARNING, "currentDOBWrong");
							warningCount++;
						}
						// date-of-birth in subject table matchs DOB
						else {
							fp.addPresetValue(D_WARNING, "false");
						}
					}
					// current Study require YOB
					else if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
						String y = String.valueOf(subjectWithSameId.getDateOfBirth()).split("\\-")[0];
						// year of date-of-birth in subject table is avaible
						if (!y.equals("0001")) {
							// year in subject table doesn't match YOB,
							if (!y.equals(YOB)) {
								fp.addPresetValue(Y_WARNING, "yobWrong");
								warningCount++;
							}
							// year in subject table matches YOB
							else {
								fp.addPresetValue(Y_WARNING, "false");
							}
						}
						// year of date-of-birth in the subject talbe is not
						// availbe, YOB is used
						else {
							needUpdate = true;
							updateSubject = subjectWithSameId;
							fp.addPresetValue(Y_WARNING, "yearEmpty");
							fp.addPresetValue(INPUT_YOB, YOB);
							updateSubject.setDateOfBirth(sdf.parse("01/01/" + YOB));
							warningCount++;
						}
					}
					// current study require no DOB, there is no need to check
					// date-of-birth in the subject table
					else {
						fp.addPresetValue(Y_WARNING, "false");
					}

					if (warningCount > 0) {
						warningCount = 0;
						forwardPage(Page.ADD_EXISTING_SUBJECT);
						return;
					}
					// forwardPage(Page.ADD_EXISTING_SUBJECT);
					// return;
					// YW >>
				}
				// YW << If showExistingRecord, which means there is a record
				// for the subject
				// in <subject> table, the subject only needs to be inserted
				// into <studysubject> table.
				// In other words, if(!showExistingRecord), the subject needs 
				// to be inserted into both <subject> and <studysubject> tables
				if (!showExistingRecord) {
					// YW >>
					if (!StringUtil.isBlank(fp.getString(INPUT_GENDER))) {
						subject.setGender(fp.getString(INPUT_GENDER).charAt(0));
					} else {
						subject.setGender(' ');
					}

					subject.setUniqueIdentifier(uniqueIdentifier);

					if (currentStudy.getStudyParameterConfig().getCollectDob().equals("1")) {
						if (!StringUtil.isBlank(fp.getString(INPUT_DOB))) {
							subject.setDateOfBirth(fp.getDate(INPUT_DOB));
							subject.setDobCollected(true);
						} else {
							subject.setDateOfBirth(null);
							subject.setDobCollected(false);
						}

					} else if (currentStudy.getStudyParameterConfig().getCollectDob().equals("2")) {
						// generate a fake birthday in 01/01/YYYY format, only
						// the year is
						// valid
						// added the "2" to make sure that 'not used' is kept to
						// null, tbh 102007
						subject.setDobCollected(false);
						int yob = fp.getInt(INPUT_YOB);
						Date fakeDate = new Date("01/01/" + yob);
						// Calendar fakeCal = Calendar.getInstance();
						// fakeCal.set(Calendar.YEAR, yob);
						// fakeCal.set(Calendar.MONTH, 1);
						// fakeCal.set(Calendar.DAY_OF_MONTH, 1);
						// String dobString = "01/01/" + yob;
						String dobString = local_df.format(fakeDate);

						try {
							Date fakeDOB = local_df.parse(dobString);
							subject.setDateOfBirth(fakeDOB);
						} catch (ParseException pe) {
							subject.setDateOfBirth(new Date());
							addPageMessage(respage.getString("problem_happened_saving_year"));
						}

					}
					subject.setStatus(Status.AVAILABLE);
					subject.setOwner(ub);

					if (insertWithParents) {
						subject.setFatherId(fp.getInt(INPUT_FATHER));
						subject.setMotherId(fp.getInt(INPUT_MOTHER));
						subject = sdao.create(subject);
					} else {
						subject = sdao.create(subject);
					}

					if (!subject.isActive()) {
						throw new OpenClinicaException(resexception.getString("could_not_create_subject"), "3");
					}
					// YW << for showExistingRecord && existingSubShown,
					// If input value(s) is(are) different from database,
					// warning will be shown.
					// If value(s) in database is(are) empty, entered value(s)
					// could be used;
					// Otherwise, value(s) in database will be used.
					// For date-of-birth, if database only has year-of-birth,
					// the year in database will be used for year part
				} else if (existingSubShown) {
					if (!needUpdate) {
						subject = subjectWithSameId;
					} else {
						updateSubject.setUpdater(ub);
						updateSubject = (SubjectBean) sdao.update(updateSubject);

						if (!updateSubject.isActive()) {
							throw new OpenClinicaException("Could not create subject.", "5");
						}
						subject = updateSubject;
						needUpdate = false;
					}
				}
				// YW >>
				// enroll the subject in the active study
				studySubject.setSubjectId(subject.getId());
				studySubject.setStudyId(currentStudy.getId());
				studySubject.setLabel(label);
				studySubject.setSecondaryLabel(fp.getString(INPUT_SECONDARY_LABEL));
				studySubject.setStatus(Status.AVAILABLE);
				studySubject.setEnrollmentDate(fp.getDate(INPUT_ENROLLMENT_DATE));
				if (fp.getBoolean("addWithEvent")) {
					studySubject.setEventStartDate(fp.getDate(INPUT_EVENT_START_DATE));
				}

				studySubject.setOwner(ub);
				
				if (!"".equals(fp.getString("dynamicGroupClassId"))) {
					studySubject.setDynamicGroupClassId(Integer.valueOf(fp.getString("dynamicGroupClassId")));
				} else {
					studySubject.setDynamicGroupClassId(0);
				}

				// Shaoyu Su: prevent same label ("Study Subject ID")
				synchronized (simpleLockObj) {
					if (label.equalsIgnoreCase(resword.getString("id_generated_Save_Add"))) {
						int nextLabel = ssd.findTheGreatestLabel() + 1;
						studySubject.setLabel(nextLabel + "");
						studySubject = ssd.createWithoutGroup(studySubject);
						if (showExistingRecord && !existingSubShown) {
							fp.addPresetValue(INPUT_LABEL, label);
						}
					} else {
						studySubject = ssd.createWithoutGroup(studySubject);
					}
				}
				if (!classes.isEmpty() && studySubject.isActive()) {
					SubjectGroupMapDAO sgmdao = new SubjectGroupMapDAO(sm.getDataSource());
					for (int i = 0; i < classes.size(); i++) {
						StudyGroupClassBean group = (StudyGroupClassBean) classes.get(i);
						group.getStudyGroupId();
						group.getGroupNotes();
						SubjectGroupMapBean map = new SubjectGroupMapBean();
						map.setNotes(group.getGroupNotes());
						map.setStatus(Status.AVAILABLE);
						map.setStudyGroupId(group.getStudyGroupId());
						map.setStudySubjectId(studySubject.getId());
						map.setStudyGroupClassId(group.getId());
						map.setOwner(ub);
						if (map.getStudyGroupId() > 0) {
							sgmdao.create(map);
						}
					}
				}
				
				if (!studySubject.isActive()) {
					throw new OpenClinicaException(resexception.getString("could_not_create_study_subject"), "4");
				}

				// save discrepancy notes into DB
				FormDiscrepancyNotes fdn = (FormDiscrepancyNotes) session.getAttribute(FORM_DISCREPANCY_NOTES_NAME);
				DiscrepancyNoteDAO dndao = new DiscrepancyNoteDAO(sm.getDataSource());

				String[] subjectFields = { INPUT_DOB, INPUT_YOB, INPUT_GENDER };
				for (String element : subjectFields) {
					saveFieldNotes(element, fdn, dndao, subject.getId(), "subject", currentStudy);
				}
				saveFieldNotes(INPUT_ENROLLMENT_DATE, fdn, dndao, studySubject.getId(), "studySub", currentStudy);

				request.removeAttribute(FormProcessor.FIELD_SUBMITTED);
				request.setAttribute(CreateNewStudyEventServlet.INPUT_STUDY_SUBJECT, studySubject);
				request.setAttribute(CreateNewStudyEventServlet.INPUT_REQUEST_STUDY_SUBJECT, "no");
				request.setAttribute(FormProcessor.FIELD_SUBMITTED, "0");

				addPageMessage(respage.getString("subject_with_unique_identifier") + studySubject.getLabel()
						+ respage.getString("X_was_created_succesfully"));

				if (fp.getBoolean("addWithEvent")) {
					createStudyEvent(fp, studySubject);
					// YW <<
					request.setAttribute("id", studySubject.getId() + "");
					response.encodeRedirectURL("ViewStudySubject?id=" + studySubject.getId());
					forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
					// YW >>
					// we want to get the url of viewing study subject in
					// browser to avoid page expired problem
					// response.sendRedirect(response.encodeRedirectURL(
					// "ViewStudySubject?id="
					// + studySubject.getId()));
					return;

				}

				String submitEvent = fp.getString(SUBMIT_EVENT_BUTTON);
				String submitEnroll = fp.getString(SUBMIT_ENROLL_BUTTON);
				fp.getString(SUBMIT_DONE_BUTTON);

				session.removeAttribute(FORM_DISCREPANCY_NOTES_NAME);
				if (!StringUtil.isBlank(submitEvent)) {
					forwardPage(Page.CREATE_NEW_STUDY_EVENT_SERVLET);
				} else if (!StringUtil.isBlank(submitEnroll)) {
					// NEW MANTIS ISSUE 4770
					setUpBeans(classes);
					request.setAttribute(BEAN_DYNAMIC_GROUPS, dynamicClasses);
					Date today = new Date(System.currentTimeMillis());
					String todayFormatted = local_df.format(today);
					fp.addPresetValue(INPUT_ENROLLMENT_DATE, todayFormatted);

					// YW 10-07-2007 <<
					String idSetting = "";
					if (currentStudy.getParentStudyId() > 0) {
						parentSPV = spvdao.findByHandleAndStudy(parentStudyId, "subjectIdGeneration");
						currentStudy.getStudyParameterConfig().setSubjectIdGeneration(parentSPV.getValue());
					}
					idSetting = currentStudy.getStudyParameterConfig().getSubjectIdGeneration();
					// YW >>
					logger.info("subject id setting :" + idSetting);
					// set up auto study subject id
					if (idSetting.equals("auto editable") || idSetting.equals("auto non-editable")) {
						// Shaoyu Su
						// int nextLabel = ssd.findTheGreatestLabel() + 1;
						// fp.addPresetValue(INPUT_LABEL, new Integer(nextLabel).toString());
						// fp.addPresetValue(INPUT_LABEL, resword.getString("id_generated_Save_Add"));
						String nextLabel = ssd.findNextLabel(currentStudy.getIdentifier());
						fp.addPresetValue(INPUT_LABEL, nextLabel);
					}

					setPresetValues(fp.getPresetValues());
					discNotes = new FormDiscrepancyNotes();
					session.setAttribute(FORM_DISCREPANCY_NOTES_NAME, discNotes);
					// End of 4770
					forwardPage(Page.ADD_NEW_SUBJECT);
				} else {
					// forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);

					// clinovo - ticket #39
					/*
					 * String listStudySubject = "/ListStudySubjects?maxRows=15" +
					 * "&showMoreLink=true&findSubjects_tr_=true" + "&findSubjects_p_=1&findSubjects_mr_=15" +
					 * "&findSubjects_s_0_studySubject.label=asc";
					 */

					forwardPage(Page.LIST_STUDY_SUBJECTS_SERVLET);
					// tbh clinovo 10/30/2012, change
					// request.setAttribute("id", studySubject.getId() + "");
					// response.encodeRedirectURL("ViewStudySubject?id=" + studySubject.getId());
					// forwardPage(Page.VIEW_STUDY_SUBJECT_SERVLET);
					// end clinovo 10/30/2012

					return;
				}
			}// end of no error (errors.isEmpty())
		}// end of fp.isSubmitted()
	}

	protected void createStudyEvent(FormProcessor fp, StudySubjectBean s) {
		int studyEventDefinitionId = fp.getInt("studyEventDefinition");
		String location = fp.getString("location");
		Date startDate = s.getEventStartDate();
		if (studyEventDefinitionId > 0) {
			String locationTerm = resword.getString("location");
			// don't allow user to use the default value 'Location' since
			// location
			// is a required field
			if (location.equalsIgnoreCase(locationTerm)) {
				addPageMessage(restext.getString("not_a_valid_location"));
			} else {
				logger.info("will create event with new subject");
				StudyEventDAO sedao = new StudyEventDAO(sm.getDataSource());
				StudyEventDefinitionDAO seddao = new StudyEventDefinitionDAO(sm.getDataSource());
				StudyEventBean se = new StudyEventBean();
				se.setLocation(location);
				se.setDateStarted(startDate);
				se.setDateEnded(startDate);
				se.setOwner(ub);
				se.setStudyEventDefinitionId(studyEventDefinitionId);
				se.setStatus(Status.AVAILABLE);
				se.setStudySubjectId(s.getId());
				se.setSubjectEventStatus(SubjectEventStatus.SCHEDULED);

				StudyEventDefinitionBean sed = (StudyEventDefinitionBean) seddao.findByPK(studyEventDefinitionId);
				se.setSampleOrdinal(sedao.getMaxSampleOrdinal(sed, s) + 1);
				sedao.create(se);
			}

		} else {
			addPageMessage(respage.getString("no_event_sheduled_for_subject"));

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.akaza.openclinica.control.core.SecureController#mayProceed()
	 */
	@Override
	protected void mayProceed() throws InsufficientPermissionException {

		String exceptionName = resexception.getString("no_permission_to_add_new_subject");
		String noAccessMessage = respage.getString("may_not_add_new_subject") + " "
				+ respage.getString("change_study_contact_sysadmin");

		if (SubmitDataServlet.maySubmitData(ub, currentRole)) {
			return;
		}

		addPageMessage(noAccessMessage);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, exceptionName, "1");
	}

	protected void setUpBeans(ArrayList classes) throws Exception {
		StudyGroupDAO sgdao = new StudyGroupDAO(sm.getDataSource());

		SubjectDAO sdao = new SubjectDAO(sm.getDataSource());
		ArrayList fathers = sdao.findAllByGender('m');
		ArrayList mothers = sdao.findAllByGender('f');

		ArrayList dsFathers = new ArrayList();
		ArrayList dsMothers = new ArrayList();

		StudySubjectDAO ssdao = new StudySubjectDAO(sm.getDataSource());
		StudyDAO stdao = new StudyDAO(sm.getDataSource());

		displaySubjects(dsFathers, fathers, ssdao, stdao);
		displaySubjects(dsMothers, mothers, ssdao, stdao);

		request.setAttribute(BEAN_FATHERS, dsFathers);
		request.setAttribute(BEAN_MOTHERS, dsMothers);

		for (int i = 0; i < classes.size(); i++) {
			StudyGroupClassBean group = (StudyGroupClassBean) classes.get(i);
			ArrayList studyGroups = sgdao.findAllByGroupClass(group);
			group.setStudyGroups(studyGroups);
		}

		request.setAttribute(BEAN_GROUPS, classes);
	}

	/**
	 * Save the discrepancy notes of each field into session in the form
	 * 
	 * @param field
	 * @param notes
	 * @param dndao
	 * @param entityId
	 * @param entityType
	 * @param sb
	 */
	public static void saveFieldNotes(String field, FormDiscrepancyNotes notes, DiscrepancyNoteDAO dndao, int entityId,
			String entityType, StudyBean sb) {

		if (notes == null || dndao == null || sb == null) {
			// logger.info("AddNewSubjectServlet,saveFieldNotes:parameter is
			// null, cannot proceed:");
			return;
		}
		ArrayList fieldNotes = notes.getNotes(field);
		// System.out.println("+++ notes size:" + fieldNotes.size() + " for field " + field);
		for (int i = 0; i < fieldNotes.size(); i++) {
			DiscrepancyNoteBean dnb = (DiscrepancyNoteBean) fieldNotes.get(i);
			dnb.setEntityId(entityId);
			dnb.setStudyId(sb.getId());
			dnb.setEntityType(entityType);

			// updating exsiting note if necessary
			/*
			 * if ("itemData".equalsIgnoreCase(entityType)) { System.out.println(" **** find parent note for item data:"
			 * + dnb.getEntityId()); ArrayList parentNotes = dndao.findExistingNotesForItemData(dnb.getEntityId()); for
			 * (int j = 0; j < parentNotes.size(); j++) { DiscrepancyNoteBean parent = (DiscrepancyNoteBean)
			 * parentNotes.get(j); if (parent.getParentDnId() == 0) { if (dnb.getDiscrepancyNoteTypeId() ==
			 * parent.getDiscrepancyNoteTypeId()) { if (dnb.getResolutionStatusId() != parent.getResolutionStatusId()) {
			 * parent.setResolutionStatusId(dnb.getResolutionStatusId()); dndao.update(parent); } } } } }
			 */
			if (dnb.getResolutionStatusId() == 0) {
				dnb.setResStatus(ResolutionStatus.NOT_APPLICABLE);
				dnb.setResolutionStatusId(ResolutionStatus.NOT_APPLICABLE.getId());
				if (!dnb.getDisType().equals(DiscrepancyNoteType.REASON_FOR_CHANGE)) {
					dnb.setResStatus(ResolutionStatus.OPEN);
					dnb.setResolutionStatusId(ResolutionStatus.OPEN.getId());
				}

			}
			// << tbh 05/2010 second fix to try out queries
			// ClinCapture #42
			if (dndao.findByPK(dnb.getId()) == null) {
				dnb = (DiscrepancyNoteBean) dndao.create(dnb);
				dndao.createMapping(dnb);
			}

			if (dnb.getParentDnId() == 0) {
				// see issue 2659 this is a new thread, we will create two notes
				// in this case,
				// This way one can be the parent that updates as the status
				// changes, but one also stays as New.
				dnb.setParentDnId(dnb.getId());
				dnb = (DiscrepancyNoteBean) dndao.create(dnb);
				dndao.createMapping(dnb);
			} else if (dnb.getParentDnId() > 0) {
				DiscrepancyNoteBean parentNote = (DiscrepancyNoteBean) dndao.findByPK(dnb.getParentDnId());
				if (dnb.getDiscrepancyNoteTypeId() == parentNote.getDiscrepancyNoteTypeId()
						&& dnb.getResolutionStatusId() != parentNote.getResolutionStatusId()) {
					parentNote.setResolutionStatusId(dnb.getResolutionStatusId());
					dndao.update(parentNote);
				}
			}
		}
	}

	/**
	 * Find study subject id for each subject, and construct displaySubjectBean
	 * 
	 * @param displayArray
	 * @param subjects
	 */
	public static void displaySubjects(ArrayList displayArray, ArrayList subjects, StudySubjectDAO ssdao, StudyDAO stdao) {

		for (int i = 0; i < subjects.size(); i++) {
			SubjectBean subject = (SubjectBean) subjects.get(i);
			ArrayList studySubs = ssdao.findAllBySubjectId(subject.getId());
			String protocolSubjectIds = "";
			for (int j = 0; j < studySubs.size(); j++) {
				StudySubjectBean studySub = (StudySubjectBean) studySubs.get(j);
				int studyId = studySub.getStudyId();
				StudyBean stu = (StudyBean) stdao.findByPK(studyId);
				String protocolId = stu.getIdentifier();
				if (j == studySubs.size() - 1) {
					protocolSubjectIds = protocolId + "-" + studySub.getLabel();
				} else {
					protocolSubjectIds = protocolId + "-" + studySub.getLabel() + ", ";
				}
			}
			DisplaySubjectBean dsb = new DisplaySubjectBean();
			dsb.setSubject(subject);
			dsb.setStudySubjectIds(protocolSubjectIds);
			displayArray.add(dsb);

		}

	}
}
