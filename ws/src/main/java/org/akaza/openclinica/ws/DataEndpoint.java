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
 * If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.ws;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.submit.DisplayItemBeanWrapper;
import org.akaza.openclinica.bean.submit.crfdata.ODMContainer;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.login.UserAccountDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.web.crfdata.DataImportService;
import org.akaza.openclinica.ws.bean.BaseStudyDefinitionBean;
import org.akaza.openclinica.ws.validator.CRFDataImportValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.XPathParam;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.sql.DataSource;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.exolab.castor.mapping.Mapping;
import org.exolab.castor.xml.Unmarshaller;

@Endpoint
public class DataEndpoint {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	private final String NAMESPACE_URI_V1 = "http://openclinica.org/ws/data/v1";

	private final String ODM_HEADER_NAMESPACE = "<ODM xmlns=\"http://www.cdisc.org/ns/odm/v1.3\" targetNamespace=\"http://openclinica.org/ws/data/v1\" xmlns:OpenClinica=\"http://www.openclinica.org/ns/openclinica_odm/v1.3\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.cdisc.org/ns/odm/v1.3\">";
	private final DataSource dataSource;
	private final MessageSource messages;
	private final CoreResources coreResources;
	private final Locale locale;

	public DataEndpoint(DataSource dataSource, MessageSource messages, CoreResources coreResources) {
		this.dataSource = dataSource;
		this.messages = messages;
		this.coreResources = coreResources;

		this.locale = new Locale("en_US");
	}

	/**
	 * if NAMESPACE_URI_V1:importDataRequest execute this method
	 * 
	 * @return
	 * @throws Exception
	 */
	@PayloadRoot(localPart = "importRequest", namespace = NAMESPACE_URI_V1)
	public Source importData(@XPathParam("//ODM") Element odmElement) throws Exception {

		ResourceBundleProvider.updateLocale(new Locale("en_US"));

		logger.debug("rootElement=" + odmElement);

		UserAccountBean userBean = null;

		try {
			if (odmElement == null) {
				return new DOMSource(mapFailConfirmation(null, "Your XML is not well-formed."));
			}
			ODMContainer odmContainer = unmarshallToODMContainer(odmElement);
			String studyUniqueID = odmContainer.getCrfDataPostImportContainer().getStudyOID();
			userBean = getUserAccount();
			BaseStudyDefinitionBean crfDataImportBean = new BaseStudyDefinitionBean(studyUniqueID, userBean);

			DataBinder dataBinder = new DataBinder(crfDataImportBean);
			Errors errors = dataBinder.getBindingResult();
			CRFDataImportValidator crfDataImportValidator = new CRFDataImportValidator(dataSource);
			crfDataImportValidator.validate(crfDataImportBean, errors);

			if (!errors.hasErrors()) {
				List<DisplayItemBeanWrapper> displayItemBeanWrappers = new ArrayList<DisplayItemBeanWrapper>();

				List<String> errorMessagesFromValidation = new DataImportService().validateData(odmContainer,
						dataSource, coreResources, crfDataImportBean.getStudy(), userBean, displayItemBeanWrappers);
				if (errorMessagesFromValidation.size() > 0) {
					String err_msg = convertToErrorString(errorMessagesFromValidation);
					return new DOMSource(mapFailConfirmation(null, err_msg));
				}

				List<String> auditMsgs = new DataImportService().submitData(odmContainer, dataSource,
						crfDataImportBean.getStudy(), userBean, displayItemBeanWrappers);
				return new DOMSource(mapConfirmation(auditMsgs));
			} else {
				return new DOMSource(mapFailConfirmation(errors, null));
			}

			// //
		} catch (Exception npe) {
			return new DOMSource(mapFailConfirmation(null, "Your XML is not well-formed. " + npe.getMessage()));
		}
	}

	private ODMContainer unmarshallToODMContainer(Element odmElement) throws Exception {
		ResourceBundle respage = ResourceBundleProvider.getPageMessagesBundle();

		String xml = node2String(odmElement);
		xml = xml.replaceAll("<ODM>", this.ODM_HEADER_NAMESPACE);

		if (xml == null)
			throw new Exception(respage.getString("unreadable_file"));

		Mapping myMap = new Mapping();

		InputStream mapInputStream = coreResources.getInputStream("cd_odm_mapping.xml");

		myMap.loadMapping(new InputSource(mapInputStream));
		Unmarshaller um1 = new Unmarshaller(myMap);
		ODMContainer odmContainer = new ODMContainer();

		try {
			logger.debug(xml);
			// File xsdFileFinal = new File(xsdFile);
			// schemaValidator.validateAgainstSchema(xml, xsdFile);
			// removing schema validation since we are presented with the chicken v egg error problem
			odmContainer = (ODMContainer) um1.unmarshal(new StringReader(xml));
			logger.debug("Found crf data container for study oid: "
					+ odmContainer.getCrfDataPostImportContainer().getStudyOID());
			logger.debug("found length of subject list: "
					+ odmContainer.getCrfDataPostImportContainer().getSubjectData().size());
			return odmContainer;

		} catch (Exception me1) {
			// fail against one, try another
			me1.printStackTrace();
			logger.debug("failed in unmarshaling, trying another version = " + me1.getMessage());
			throw new Exception();
		}

	}

	/**
	 * Helper Method to get the user account
	 * 
	 * @return UserAccountBean
	 */
	private UserAccountBean getUserAccount() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = null;
		if (principal instanceof UserDetails) {
			username = ((UserDetails) principal).getUsername();
		} else {
			username = principal.toString();
		}
		UserAccountDAO userAccountDao = new UserAccountDAO(dataSource);
		return (UserAccountBean) userAccountDao.findByUserName(username);
	}

	/**
	 * Create Error Response
	 * 
	 * @param confirmation
	 * @return
	 * @throws Exception
	 */
	private Element mapFailConfirmation(Errors errors, String message) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.newDocument();

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "importDataResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");

		String confirmation = messages.getMessage("dataEndpoint.fail", null, "Fail", locale);
		resultElement.setTextContent(confirmation);
		responseElement.appendChild(resultElement);

		if (errors != null) {
			for (ObjectError error : errors.getAllErrors()) {
				Element errorElement = document.createElementNS(NAMESPACE_URI_V1, "error");
				String theMessage = messages.getMessage(error.getCode(), error.getArguments(), locale);
				errorElement.setTextContent(theMessage);
				responseElement.appendChild(errorElement);
			}
		}
		if (message != null) {

			Element msgElement = document.createElementNS(NAMESPACE_URI_V1, "error");
			msgElement.setTextContent(message);
			responseElement.appendChild(msgElement);
			logger.debug("sending fail message " + message);
		}
		return responseElement;

	}

	/**
	 * Create Response
	 * 
	 * @param confirmation
	 * @return
	 * @throws Exception
	 */
	private Element mapConfirmation(List<String> auditMsgs) throws Exception {
		DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
		Document document = docBuilder.newDocument();

		Element responseElement = document.createElementNS(NAMESPACE_URI_V1, "importDataResponse");
		Element resultElement = document.createElementNS(NAMESPACE_URI_V1, "result");

		if (auditMsgs != null) {
			String status = auditMsgs.get(0);
			if ("fail".equals(status)) {
				String confirmation = messages.getMessage("dataEndpoint.fail", null, "Fail", locale);
				resultElement.setTextContent(confirmation);
				responseElement.appendChild(resultElement);
				Element msgElement = document.createElementNS(NAMESPACE_URI_V1, "error");
				auditMsgs.remove(0);
				StringBuffer output_msg = new StringBuffer("");
				for (String mes : auditMsgs) {
					output_msg.append(mes);
				}
				msgElement.setTextContent(output_msg.toString());
				responseElement.appendChild(msgElement);
			} else if ("warn".equals(status)) {
				// set a summary here, and set individual warnings for each DN
				String confirmation = messages.getMessage("dataEndpoint.success", null, "Success", locale);
				resultElement.setTextContent(confirmation);
				responseElement.appendChild(resultElement);
				Element msgElement = document.createElementNS(NAMESPACE_URI_V1, "summary");
				msgElement.setTextContent(auditMsgs.get(1));
				responseElement.appendChild(msgElement);
				String listOfDns = auditMsgs.get(2);
				String[] splitListOfDns = listOfDns.split("---");
				for (String dn : splitListOfDns) {
					Element warning = document.createElementNS(NAMESPACE_URI_V1, "warning");
					warning.setTextContent(dn);
					responseElement.appendChild(warning);
				}
			} else {
				// plain success no warnings
				String confirmation = messages.getMessage("dataEndpoint.success", null, "Success", locale);
				resultElement.setTextContent(confirmation);
				responseElement.appendChild(resultElement);
			}
		}

		return responseElement;
	}

	private static String node2String(Node node) {
		try {
			TransformerFactory tFactory = TransformerFactory.newInstance();

			Transformer transformer = tFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");

			StringWriter outStream = new StringWriter();
			DOMSource source = new DOMSource(node);
			StreamResult result = new StreamResult(outStream);
			transformer.transform(source, result);
			return outStream.getBuffer().toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private String convertToErrorString(List<String> errorMessages) {
		StringBuilder result = new StringBuilder();
		for (String str : errorMessages) {
			result.append(str + " \n");
		}

		return result.toString();
	}

}
