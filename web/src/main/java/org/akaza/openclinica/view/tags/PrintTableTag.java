/*******************************************************************************
 * ClinCapture, Copyright (C) 2009-2014 Clinovo Inc.
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

package org.akaza.openclinica.view.tags;

import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.DisplaySectionBean;
import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.form.PrintHorizontalFormBuilder;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * A tag class that is designed to generate a printable group table. The String value formInvolvesDatabaseData maps to
 * an attribute value indicating whether the form involves an event or data.
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class PrintTableTag extends SimpleTagSupport {

	private String involvesDatabaseData;
	private ResourceBundle restext;
	public static final String CURRENT_SESSION_LOCALE = "current.session.locale";

	/**
	 * This JSP Tag API method creates a instance of PrintHorizontalFormBuilder,
	 * then generates that class's XHTML
	 * output into the web page. The tag shows all sections of a CRF.
	 * 
	 * @throws JspException Thrown if an error occured
	 *     while invoking this fragment.
	 * @throws IOException If there was an error writing to the
	 *     stream.
	 */
	@Override
	public void doTag() throws JspException, IOException  {

		JspContext context = getJspContext();
		JspWriter tagWriter = context.getOut();

		PageContext pcontext = (PageContext) getJspContext();
		restext = ResourceBundleProvider.getTextsBundle((Locale) pcontext.getAttribute(CURRENT_SESSION_LOCALE, PageContext.SESSION_SCOPE));

		// This request attribute is generated by the PrintCRf or PrintDataEntry servlets
		List<DisplaySectionBean> listOfDisplayBeans = (ArrayList) context.findAttribute("listOfDisplaySectionBeans");
		StudyBean studyBean = (StudyBean) context.findAttribute("study");
		EventCRFBean eventCRFBean = (EventCRFBean) context.findAttribute("EventCRFBean");

		String isInternetExplorer = (String) context.findAttribute("isInternetExplorer");

		if (listOfDisplayBeans != null) {
			PrintHorizontalFormBuilder printFormBuilder = new PrintHorizontalFormBuilder();
			// Provide the form-building code with the list of display section
			// beans
			printFormBuilder.setDisplaySectionBeans(listOfDisplayBeans);

			// The body content of the tag contains 'true' or 'false', depending on whether the
			// printed CRF involves data entry (and possible saved data) or not.
			JspFragment fragment = this.getJspBody();
			Writer stringWriter = new StringWriter();
			fragment.invoke(stringWriter);
			if ("true".equalsIgnoreCase(stringWriter.toString())) {
				printFormBuilder.setInvolvesDataEntry(true);
			}

			printFormBuilder.setEventCRFbean(eventCRFBean);

			if ("true".equalsIgnoreCase(isInternetExplorer)) {
				printFormBuilder.setInternetExplorer(true);
			}
			if (studyBean != null) {
				printFormBuilder.setStudyBean(studyBean);
			}
			if ("true".equalsIgnoreCase(stringWriter.toString())) {
				tagWriter.println(printFormBuilder.createMarkup());
			} else {
				tagWriter.println(printFormBuilder.createMarkupNoDE());
			}
		} else {
			tagWriter.println(restext.getString("application_could_not_generate_the_markup"));
		}
	}

	public String getInvolvesDatabaseData() {
		return involvesDatabaseData;
	}

	public void setInvolvesDatabaseData(String involvesDatabaseData) {
		this.involvesDatabaseData = involvesDatabaseData;
	}
}
