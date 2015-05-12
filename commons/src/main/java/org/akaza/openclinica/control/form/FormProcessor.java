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
package org.akaza.openclinica.control.form;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.dao.core.EntityDAO;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author ssachs
 *
 *         This class does two things: retrieve input from a form, and prepare to set default values
 *
 *         three dimensions:
 *         <ul>
 *         <li>do we throw an exception when the key isn't present?</li>
 *         <li>do we look in the attributes and parameters, or just the parameters?</li>
 *         <li>do we look in an HttpServletRequest, or a MultipartRequest?</li>
 *         </ul>
 *
 *         TODO handle MultiPartRequests - is this a priority, since we don't have many file uploads?
 */
@SuppressWarnings({"rawtypes", "unchecked"})
public class FormProcessor {
	private HttpServletRequest request;
	private HashMap presetValues;
	private final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String DEFAULT_STRING = "";
	public static final int DEFAULT_INT = 0;
	public static final float DEFAULT_FLOAT = (float) 0.0;
	public static final boolean DEFAULT_BOOLEAN = false;
	public static final Date DEFAULT_DATE = new Date();
	public static final String FIELD_SUBMITTED = "submitted";

	// entity bean list field names
	public static final String EBL_PAGE = "ebl_page";
	public static final String EBL_SORT_COLUMN = "ebl_sortColumnInd";
	public static final String EBL_SORT_ORDER = "ebl_sortAscending";
	public static final String EBL_FILTERED = "ebl_filtered";
	public static final String EBL_FILTER_KEYWORD = "ebl_filterKeyword";
	public static final String EBL_PAGINATED = "ebl_paginated";

	/**
	 *
	 * @param request
	 *            HttpServletRequest
	 */
	public FormProcessor(HttpServletRequest request) {
		this.request = request;
		this.presetValues = new HashMap();
	}

	/**
	 * @return Returns the request.
	 */
	public HttpServletRequest getRequest() {
		return request;
	}

	/**
	 * @param request
	 *            The request to set.
	 */
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @return Returns the presetValues.
	 */
	public HashMap getPresetValues() {
		return presetValues;
	}

	/**
	 * @param presetValues
	 *            The presetValues to set.
	 */
	public void setPresetValues(HashMap presetValues) {
		this.presetValues = presetValues;
	}

	/**
	 * Clears preset values.
	 */
	public void clearPresetValues() {
		presetValues = new HashMap();
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return String
	 */
	public String getString(String fieldName, boolean searchAttributes) {
		String result = DEFAULT_STRING;

		if (searchAttributes) {
			result = request.getAttribute(fieldName) != null ? request.getAttribute(fieldName).toString() : null;

			if (result == null) {
				result = request.getParameter(fieldName);

				if (result == null) {
					return DEFAULT_STRING;
				}
			}
		} else {
			result = request.getParameter(fieldName);
			if (result == null) {
				return DEFAULT_STRING;
			}
		}
		return result;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return String
	 */
	public String getString(String fieldName) {
		return getString(fieldName, false);
	}

	/**
	 * For an input which is supposed to return an array of strings, such as a checkbox or multiple-select input,
	 * retrieve all of those strings in an ArrayList.
	 *
	 * Note that the values must be contained in the request parameters, not in the attributes.
	 *
	 * @param fieldName
	 *            The name of the input
	 * @return An array of all the Strings corresponding to that input. Guaranteed to be non-null. All elements
	 *         guaranteed to be non-null.
	 */
	public ArrayList getStringArray(String fieldName) {
		ArrayList answer = new ArrayList();

		String[] values = request.getParameterValues(fieldName);

		if (values != null) {
			for (String element : values) {
				if (element != null) {
					answer.add(element);
				}
			}
		}

		return answer;
	}

	/**
	 *
	 * @param partialFieldName
	 *            String
	 * @return boolean
	 */
	public boolean getStartsWith(String partialFieldName) {
		boolean answer = false;
		CharSequence seq = partialFieldName.subSequence(0, partialFieldName.length());
		java.util.Enumeration<String> names = request.getParameterNames();

		while (names.hasMoreElements()) {

			String name = names.nextElement();
			if (name.contains(seq)) {
				return true;
			}

		}
		return answer;
	}

	/**
	 *
	 * @param value
	 *            String
	 * @return int
	 */
	public static int getIntFromString(String value) {
		if (value == null) {
			return DEFAULT_INT;
		}

		int result;

		try {
			result = Integer.parseInt(value);
		} catch (Exception e) {
			result = DEFAULT_INT;
		}

		return result;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return int
	 */
	public int getInt(String fieldName, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);
		return FormProcessor.getIntFromString(fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return int
	 */
	public int getInt(String fieldName) {
		return getInt(fieldName, false);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return int
	 * @throws Exception
	 *             in case of failure
	 */
	public int getPresentInt(String fieldName, boolean searchAttributes) throws Exception {
		String fieldValue = getString(fieldName, searchAttributes);
		int result;

		try {
			result = Integer.parseInt(fieldValue);
		} catch (Exception e) {
			throw new Exception("The attribute or parameter with name " + fieldName
					+ " is not an integer; the form is corrupt.");
		}

		return result;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return int
	 * @throws Exception
	 *             in case of failure
	 */
	public int getPresentInt(String fieldName) throws Exception {
		return getPresentInt(fieldName, false);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return boolean
	 */
	public boolean getBoolean(String fieldName, boolean searchAttributes) {
		int fieldValue = getInt(fieldName, searchAttributes);

		if (fieldValue != 0) {
			return true;
		}
		return DEFAULT_BOOLEAN;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return boolean
	 */
	public boolean getBoolean(String fieldName) {
		return getBoolean(fieldName, false);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return float
	 */
	public float getFloat(String fieldName, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);
		float fltValue;

		try {
			fltValue = Float.parseFloat(fieldValue);
		} catch (Exception e) {
			fltValue = DEFAULT_FLOAT;
		}

		return fltValue;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return float
	 */
	public float getFloat(String fieldName) {
		return getFloat(fieldName, false);
	}

	/**
	 * @param fieldName
	 *            The name of the HTML form field which holds the Entity's primary key.
	 * @param edao
	 *            The data source for the Entity.
	 * @return The Entity whose primary key is specified by fieldName, and which can be retrieved by edao.
	 * @throws OpenClinicaException
	 *             in case of failure
	 */
	public EntityBean getEntity(String fieldName, EntityDAO edao) throws OpenClinicaException {
		int id = getInt(fieldName);
		EntityBean result = edao.findByPK(id);
		return result;
	}

	/**
	 *
	 * @param date
	 *            String
	 * @return Date
	 */
	public static Date getDateFromString(String date) {
		Date answer;
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		try {
			SimpleDateFormat f = new SimpleDateFormat(resformat.getString("date_format_string"),
					ResourceBundleProvider.getLocale());
			f.setLenient(false);
			answer = f.parse(date);
		} catch (Exception e) {
			answer = DEFAULT_DATE;
		}

		return answer;
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param searchAttributes
	 *            boolean
	 * @return Date
	 */
	public Date getDate(String fieldName, boolean searchAttributes) {
		String fieldValue = getString(fieldName, searchAttributes);

		return FormProcessor.getDateFromString(fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @return Date
	 */
	public Date getDate(String fieldName) {
		return getDate(fieldName, false);
	}

	/**
	 * @param dateTime
	 *            A string in in date_time_format_string
	 * @return The Date object corresponding to the provided string, or DEFAULT_DATE if the string is improperly
	 *         formatted.
	 */
	public static Date getDateTimeFromString(String dateTime) {
		Date answer;
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		try {
			SimpleDateFormat f = new SimpleDateFormat(resformat.getString("date_time_format_string"),
					ResourceBundleProvider.getLocale());
			f.setLenient(false);
			answer = f.parse(dateTime);
		} catch (Exception e) {
			answer = DEFAULT_DATE;
		}

		return answer;
	}

	/**
	 * Return datetime value. If no input for "Hour" and "Minute" and "am/pm", default time will be 12:00pm. In another
	 * word,
	 * <p>
	 * Precondition:Before calling this method, it should make sure that field has been entered valid datetime data.
	 *
	 * @param prefix
	 *            String
	 * @return Date
	 */
	public Date getDateTime(String prefix) {
		/*
		 * problem with this - if the field values aren't filled in, we grab the default date instead
		 * 
		 * below additions stick defaults into hour, minute and half to make sure we adhere to the simpledateformat, tbh
		 * 
		 * changes have been made to satisfy both data_time_format_string, YW (06-2008)
		 */
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		String date = getString(prefix + "Date");
		String hour = getString(prefix + "Hour");
		String minute = getString(prefix + "Minute");
		String half = getString(prefix + "Half");
		if (hour.startsWith("-1")) {
			hour = "12";
		} else if (hour.length() == 1) {
			hour = "0" + hour;
		}
		if (minute.startsWith("-1")) {
			minute = "00";
		} else if (minute.length() == 1) {
			minute = "0" + minute;
		}
		if ("".equals(half)) {
			half = "am";
		}

		String fieldValue = date + " " + hour + ":" + minute + ":00 " + half;
		SimpleDateFormat sdf = new SimpleDateFormat(resformat.getString("date_time_format_string"),
				ResourceBundleProvider.getLocale());

		sdf.setLenient(false);

		java.util.Date result;
		try {
			logger.info("trying to parse " + fieldValue + " on the pattern "
					+ resformat.getString("date_time_format_string"));
			if (date.isEmpty()) {
				result = DEFAULT_DATE;
			} else {
				result = sdf.parse(fieldValue);
			}
		} catch (Exception fe) {
			logger.info("failed to parse");
			fe.printStackTrace();
			result = DEFAULT_DATE;
			logger.info("replace with default date: " + result.toString());
		}
		logger.info("returning " + result.toString());
		return result;
	}

	/**
	 * @return true if the form was submitted; false otherwise.
	 */
	public boolean isSubmitted() {
		return getBoolean(FIELD_SUBMITTED, true);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param value
	 *            int
	 */
	public void addPresetValue(String fieldName, int value) {
		Integer fieldValue = new Integer(value);
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param value
	 *            float
	 */
	public void addPresetValue(String fieldName, float value) {
		Float fieldValue = new Float(value);
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param value
	 *            String
	 */
	public void addPresetValue(String fieldName, boolean value) {
		Boolean fieldValue = new Boolean(value);
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 *
	 * @param fieldName
	 *            String
	 * @param fieldValue
	 *            String
	 */
	public void addPresetValue(String fieldName, String fieldValue) {
		presetValues.put(fieldName, fieldValue);
	}

	/**
	 * @param fieldName
	 *            The name of the HTML form field whose value should be the Entity's primary key.
	 * @param value
	 *            The Entity whose primary key will populate the HTML form field.
	 */
	public void addPresetValue(String fieldName, EntityBean value) {
		presetValues.put(fieldName, value);
	}

	/**
	 *
	 * @param fieldNames
	 *            String[]
	 */
	public void setCurrentStringValuesAsPreset(String[] fieldNames) {
		for (String fieldName : fieldNames) {
			String fieldValue = getString(fieldName);
			addPresetValue(fieldName, fieldValue);
		}
	}

	/**
	 *
	 * @param fieldNames
	 *            String[]
	 */
	public void setCurrentIntValuesAsPreset(String[] fieldNames) {
		for (String fieldName : fieldNames) {
			int fieldValue = getInt(fieldName);
			addPresetValue(fieldName, fieldValue);
		}
	}

	/**
	 *
	 * @param fieldNames
	 *            String[]
	 */
	public void setCurrentBoolValuesAsPreset(String[] fieldNames) {
		for (String fieldName : fieldNames) {
			boolean fieldValue = getBoolean(fieldName);
			addPresetValue(fieldName, fieldValue);
		}
	}

	/**
	 * Propogates values in date/time fields to the preset values, so that they can be used to populate a form.
	 *
	 * In particular, for each prefix in prefixes, the following strings are loaded in from the form, and propagated to
	 * the preset values: prefix + "Date" prefix + "Hour" prefix + "Minute" prefix + "Half"
	 *
	 * @param prefixes
	 *            An array of Strings. Each String is a prefix for a set of date/time fields.
	 */
	public void setCurrentDateTimeValuesAsPreset(String[] prefixes) {
		for (String prefix : prefixes) {
			String fieldName = prefix + "Date";
			String date = getString(fieldName);
			addPresetValue(fieldName, date);

			fieldName = prefix + "Hour";
			int hour = getInt(fieldName);
			addPresetValue(fieldName, hour);

			fieldName = prefix + "Minute";
			int minute = getInt(fieldName);
			addPresetValue(fieldName, minute);

			fieldName = prefix + "Half";
			String half = getString(fieldName);
			addPresetValue(fieldName, half);

		}
	}

	/**
	 * Return a String which concatenates inputed "Date", "Hour", "Minute" and "am/pm" if applicable. Empty string will
	 * be returned if none of them has been entered.
	 *
	 * @param prefix
	 *            String
	 * @return String
	 */
	public String getDateTimeInputString(String prefix) {
		String str = "";
		str = getString(prefix + "Date");
		String temp = getString(prefix + "Hour");
		str += "-1".equals(temp) ? "" : temp;
		temp = getString(prefix + "Minute");
		str += "-1".equals(temp) ? "" : temp;
		temp = getString(prefix + "Half");
		str += temp == null || "-1".equals(temp) ? "" : temp;

		return str;
	}

	/**
	 * Precondition: is a valid datetime.
	 *
	 * @param prefix
	 *            String
	 * @return boolean
	 */
	public boolean timeEntered(String prefix) {
		ResourceBundle resformat = ResourceBundleProvider.getFormatBundle();
		if (!"-1".equals(getString(prefix + "Hour")) && !"-1".equals(getString(prefix + "Minute"))) {
			if (resformat.getString("date_time_format_string").contains("HH")) {
				return true;
			} else {
				if (!"".equals(getString(prefix + "Half"))) {
					return true;
				}
			}
		}
		return false;
	}
}