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

package org.akaza.openclinica.dao;

import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.managestudy.CriteriaCommand;
import org.akaza.openclinica.domain.SourceDataVerification;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class EventCRFSDVFilter implements CriteriaCommand {

	private List<Filter> filters = new ArrayList<Filter>();
	private HashMap<String, String> columnMapping = new HashMap<String, String>();
	private Integer studyId;
	private static final String NON_SDVD_STUDY_SUBJECTS = " AND ( 0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id ) OR 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ec.sdv_status = false AND ss.study_subject_id = mss.study_subject_id AND (  ((1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) OR 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) AND 0 = ( select count(edc.source_data_verification_code) from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) OR ( 1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) or 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) )))) ";
	private static final String SDVD_STUDY_SUBJECTS = " AND ( 0 < (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ss.study_subject_id = mss.study_subject_id ) AND 0 = (select count(ec.event_crf_id) from event_crf ec, study_event se, study_subject ss,crf_version cv,study s where ec.study_event_id = se.study_event_id AND ss.study_subject_id = se.study_subject_id AND ec.crf_version_id = cv.crf_version_id AND ss.study_id = s.study_id AND se.subject_event_status_id = 4 AND ec.sdv_status = false AND ss.study_subject_id = mss.study_subject_id AND (  ((1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) OR 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.parent_study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) AND 0 = ( select count(edc.source_data_verification_code) from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id )) OR ( 1 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) or 2 = ( select edc.source_data_verification_code from event_definition_crf edc where study_id = s.study_id and crf_id = cv.crf_id and study_event_definition_id = se.study_event_definition_id ) )))) ";

	public EventCRFSDVFilter(Integer studyId) {
		this.studyId = studyId;
		columnMapping.put("sdvStatus", "ec.sdv_status");
		columnMapping.put("studySubjectId", "ss.label");
		columnMapping.put("studyIdentifier", "s.unique_identifier");
		columnMapping.put("eventName", "sed.name");
		columnMapping.put("sdvRequirementDefinition", "");
		columnMapping.put("crfStatus", "ec.status_id");
		columnMapping.put("crfNameVersion", "crf.name");

	}

	public void addFilter(String property, Object value) {
		filters.add(new Filter(property, value));
	}

	public String execute(String criteria) {
		String theCriteria = "";
		for (Filter filter : filters) {
			theCriteria += buildCriteria(criteria, filter.getProperty(), filter.getValue());
		}
		return theCriteria;
	}

	private String buildCriteria(String criteria, String property, Object value) {
		value = StringEscapeUtils.escapeSql(value.toString());
		if (value != null) {
			if (property.equals("sdvStatus")) {
				String dbType = CoreResources.getDBType();
				String theTrue = dbType.equals("postgres") ? " true " : " 1 ";
				String theFalse = dbType.equals("postgres") ? " false " : " 0 ";
				if (value.equals(ResourceBundleProvider.getResWord("complete"))) {
					criteria = criteria + " and ";
					criteria = criteria + " " + columnMapping.get(property) + " = " + theTrue;
				} else {
					criteria = criteria + " and ";
					criteria = criteria + " " + columnMapping.get(property) + " = " + theFalse;
				}
			} else if (property.equals("sdvRequirementDefinition")) {
				ArrayList<Integer> reqs = new ArrayList<Integer>();
				String sdvRequirement = value.toString().trim();
				if (sdvRequirement.contains("&")) {
					for (String requirement : sdvRequirement.split("&")) {
						reqs.add(SourceDataVerification.getByI18nDescription(requirement.trim()).getCode());
					}
				} else {
					reqs.add(SourceDataVerification.getByI18nDescription(sdvRequirement.trim()).getCode());
				}
				if (reqs.size() > 0) {
					criteria = criteria + " and ";
					criteria = criteria
							+ " ec.crf_version_id in (select distinct crf_version_id from crf_version crfv, crf cr, event_definition_crf edc where crfv.crf_id = cr.crf_id AND cr.crf_id = edc.crf_id AND edc.crf_id in (select crf_id from event_definition_crf where (s.study_id  = "
							+ studyId + " or s.study_id in (select study_id from study where s.parent_study_id = "
							+ studyId + ")) )) ";
					criteria += " AND ( ";
					for (int i = 0; i < reqs.size(); i++) {
						criteria += i != 0 ? " OR " : "";
						criteria += " edc.source_data_verification_code = " + reqs.get(i);
					}
					criteria += " ) ";
				}
			} else if (property.equals("crfStatus")) {
				if (value.toString().toLowerCase().equals(ResourceBundleProvider.getResWord("completed").toLowerCase())) {
					criteria = criteria + " and ";
					criteria = criteria
							+ " ( "
							+ columnMapping.get(property)
							+ " = 2 and  se.subject_event_status_id != 5 and se.subject_event_status_id != 6 and se.subject_event_status_id != 7 ) ";
				} else {
					criteria = criteria + " and ";
					criteria = criteria
							+ " ( "
							+ columnMapping.get(property)
							+ " = 6 or ( se.subject_event_status_id = 5 or se.subject_event_status_id = 6 or se.subject_event_status_id = 7 ) )";
				}
			} else {
				criteria = criteria + " and ";
				criteria = criteria + " UPPER(" + columnMapping.get(property) + ") like UPPER('%" + value.toString()
						+ "%')" + " ";
			}
		}
		return criteria;
	}

	/**
	 * Get filter for masked CRFs for the current user.
	 * @param userId int
	 * @return string query part
	 */
	public static String getMaskedCRFsFilter(int userId) {
		StringBuilder builder = new StringBuilder("");
		if (userId != 0) {
			builder = builder.append("and ((SELECT COUNT (*) ")
					.append("FROM crfs_masking ")
					.append("WHERE (event_definition_crf_id = (")
					.append("SELECT event_definition_crf_id ")
					.append("FROM event_definition_crf edc ")
					.append("WHERE crf_id = crf.crf_id ")
					.append("AND study_event_definition_id = sed.study_event_definition_id ")
					.append("AND study_id = ss.study_id) ")
					.append("AND study_id = ss.study_id ")
					.append("AND user_id = ").append(userId).append(")) = 0)");
		}
		return builder.toString();
	}

	/**
	 * Add filter for masked CRFs if EDC table is in query.
	 * @param userId int
	 * @return filter
	 */
	public static String getMaskedCRFsFilterWithEDC(int userId) {
		StringBuilder builder = new StringBuilder("");
		if (userId != 0) {
			builder = builder.append("and ((SELECT COUNT (*) ")
					.append("FROM crfs_masking ")
					.append("WHERE event_definition_crf_id = edc.event_definition_crf_id ")
					.append("AND study_id = edc.study_id ")
					.append("AND user_id = ").append(userId).append(") = 0)");
		}
		return builder.toString();
	}

	private static class Filter {
		private final String property;
		private final Object value;

		public Filter(String property, Object value) {
			this.property = property;
			this.value = value;
		}

		public String getProperty() {
			return property;
		}

		public Object getValue() {
			return value;
		}
	}

}
