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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.admin.NewCRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.login.StudyUserRoleBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.rule.FileUploadHelper;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.bean.submit.ResponseOptionBean;
import org.akaza.openclinica.bean.submit.ResponseSetBean;
import org.akaza.openclinica.control.core.Controller;
import org.akaza.openclinica.control.form.FormProcessor;
import org.akaza.openclinica.control.form.Validator;
import org.akaza.openclinica.core.form.StringUtil;
import org.akaza.openclinica.dao.admin.CRFDAO;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.akaza.openclinica.dao.submit.CRFVersionDAO;
import org.akaza.openclinica.dao.submit.EventCRFDAO;
import org.akaza.openclinica.dao.submit.ItemDAO;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.exception.CRFReadingException;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.akaza.openclinica.view.Page;
import org.akaza.openclinica.view.StudyInfoPanel;
import org.akaza.openclinica.web.InsufficientPermissionException;
import org.akaza.openclinica.web.SQLInitServlet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Component;

/**
 * Create a new CRF verison by uploading excel file
 * 
 * @author jxu, ywang
 */
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
@Component
public class CreateCRFVersionServlet extends Controller {

	/**
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 */
	@Override
	public void mayProceed(HttpServletRequest request, HttpServletResponse response)
			throws InsufficientPermissionException {
		UserAccountBean ub = getUserAccountBean(request);
		StudyUserRoleBean currentRole = getCurrentRole(request);

		if (ub.isSysAdmin()) {
			return;
		}
		Role r = currentRole.getRole();
		if (r.equals(Role.STUDY_DIRECTOR) || r.equals(Role.STUDY_ADMINISTRATOR)) {
			return;
		}
		addPageMessage(
				respage.getString("no_have_correct_privilege_current_study")
						+ respage.getString("change_study_contact_sysadmin"), request);
		throw new InsufficientPermissionException(Page.MENU_SERVLET, resexception.getString("may_not_submit_data"), "1");
	}

	@Override
	public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);

		StudyInfoPanel panel = getStudyInfoPanel(request);
		panel.reset();
		panel.setStudyInfoShown(true);

		CRFDAO cdao = getCRFDAO();
		CRFVersionDAO vdao = getCRFVersionDAO();
		EventDefinitionCRFDAO edao = getEventDefinitionCRFDAO();

		FormProcessor fp = new FormProcessor(request);
		// checks which module the requests are from
		String module = fp.getString(MODULE);
		// keep the module in the session
		request.getSession().setAttribute(MODULE, module);

		String action = request.getParameter("action");
		CRFVersionBean version = (CRFVersionBean) request.getSession().getAttribute("version");

		HashMap errors = getErrorsHolder(request);

		if (StringUtil.isBlank(action)) {
			logger.info("action is blank");
			request.setAttribute("version", version);
			forwardPage(Page.CREATE_CRF_VERSION, request, response);
		} else if ("confirm".equalsIgnoreCase(action)) {
			String dir = SQLInitServlet.getField("filePath");
			if (!(new File(dir)).exists()) {
				logger.info("The filePath in datainfo.properties is invalid " + dir);
				addPageMessage(resword.getString("the_filepath_you_defined"), request);
				forwardPage(Page.CREATE_CRF_VERSION, request, response);
				return;
			}
			// All the uploaded files will be saved in filePath/crf/original/
			String theDir = dir + "crf" + File.separator + "original" + File.separator;
			if (!(new File(theDir)).isDirectory()) {
				// noinspection ResultOfMethodCallIgnored
				(new File(theDir)).mkdirs();
				logger.info("Made the directory " + theDir);
			}
			String tempFile = "";
			try {
				tempFile = uploadFile(request, theDir, version);
			} catch (CRFReadingException crfException) {
				Validator.addError(errors, "excel_file", crfException.getMessage());
				request.setAttribute("formMessages", errors);
				forwardPage(Page.CREATE_CRF_VERSION, request, response);
				return;
			} catch (Exception e) {
				//
				logger.warn("*** Found exception during file upload***");
				e.printStackTrace();
			}
			request.getSession().setAttribute("tempFileName", tempFile);
			// At this point, if there are errors, they point to no file
			// provided and/or not xls format
			if (errors.isEmpty()) {
				NewCRFBean newCRFBean = (NewCRFBean) request.getSession().getAttribute("nib");
				if (newCRFBean == null) {
					forwardPage(Page.CREATE_CRF_VERSION, request, response);
					return;
				}
				String versionName = newCRFBean.getVersionName();
				if (versionName.length() > 255) {
					Validator
							.addError(errors, "excel_file", resword.getString("the_version_CRF_version_more_than_255"));
				} else if (versionName.length() <= 0) {
					Validator.addError(errors, "excel_file", resword.getString("the_VERSION_column_was_blank"));
				}
				version.setName(versionName);
				if (version.getCrfId() == 0) {
					version.setCrfId(fp.getInt("crfId"));
				}
				request.getSession().setAttribute("version", version);
			}
			if (!errors.isEmpty()) {
				logger.info("has validation errors ");
				request.setAttribute("formMessages", errors);
				forwardPage(Page.CREATE_CRF_VERSION, request, response);
			} else {
				CRFBean crf = (CRFBean) cdao.findByPK(version.getCrfId());
				ArrayList versions = (ArrayList) vdao.findAllByCRF(crf.getId());
				for (Object version2 : versions) {
					CRFVersionBean version1 = (CRFVersionBean) version2;
					if (version.getName().equals(version1.getName())) {
						// version already exists
						logger.info("Version already exists; owner or not:" + ub.getId() + "," + version1.getOwnerId());
						if (ub.getId() != version1.getOwnerId()) {// not owner
							addPageMessage(
									respage.getString("CRF_version_try_upload_exists_database")
											+ version1.getOwner().getName()
											+ respage.getString("please_contact_owner_to_delete"), request);
							forwardPage(Page.CREATE_CRF_VERSION, request, response);
							return;
						} else {// owner,
							ArrayList definitions = edao.findByDefaultVersion(version1.getId());
							if (!definitions.isEmpty()) {// used in
								// definition
								request.setAttribute("definitions", definitions);
								forwardPage(Page.REMOVE_CRF_VERSION_DEF, request, response);
								return;
							} else {// not used in definition
								int previousVersionId = version1.getId();
								version.setId(previousVersionId);
								request.getSession().setAttribute("version", version);
								request.getSession().setAttribute("previousVersionId", previousVersionId);
								forwardPage(Page.REMOVE_CRF_VERSION_CONFIRM, request, response);
								return;
							}
						}
					}
				}
				// didn't find same version in the DB,let user upload the excel
				// file
				logger.info("didn't find same version in the DB,let user upload the excel file.");

				List excelErr = (ArrayList) request.getSession().getAttribute("excelErrors");
				logger.info("excelErr.isEmpty()=" + excelErr.isEmpty());
				if (excelErr.isEmpty()) {
					addPageMessage(resword.getString("congratulations_your_spreadsheet_no_errors"), request);
					forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PREVIEW, request, response);
				} else {
					logger.info("OpenClinicaException thrown, forwarding to CREATE_CRF_VERSION_CONFIRM.");
					forwardPage(Page.CREATE_CRF_VERSION_CONFIRM, request, response);
				}
			}
		} else if ("confirmsql".equalsIgnoreCase(action)) {
			NewCRFBean nib = (NewCRFBean) request.getSession().getAttribute("nib");
			if (nib != null && nib.getItemQueries() != null) {
				request.setAttribute("openQueries", nib.getItemQueries());
			} else {
				request.setAttribute("openQueries", new HashMap());
			}
			// check whether need to delete previous version
			Boolean deletePreviousVersion = (Boolean) request.getSession().getAttribute("deletePreviousVersion");
			Integer previousVersionId = (Integer) request.getSession().getAttribute("previousVersionId");
			if (deletePreviousVersion != null && deletePreviousVersion.equals(Boolean.TRUE)
					&& previousVersionId != null && previousVersionId > 0) {
				logger.info("Need to delete previous version");
				// whether we can delete
				boolean canDelete = canDeleteVersion(request, previousVersionId);
				if (!canDelete) {
					logger.info("but cannot delete previous version");
					if (request.getSession().getAttribute("itemsHaveData") == null
							&& request.getSession().getAttribute("eventsForVersion") == null) {
						addPageMessage(respage.getString("you_are_not_owner_some_items_cannot_delete"), request);
					}
					if (request.getSession().getAttribute("itemsHaveData") == null) {
						request.getSession().setAttribute("itemsHaveData", new ArrayList());
					}
					if (request.getSession().getAttribute("eventsForVersion") == null) {
						request.getSession().setAttribute("eventsForVersion", new ArrayList());
					}

					forwardPage(Page.CREATE_CRF_VERSION_NODELETE, request, response);
					return;
				}
				try {
					CRFVersionDAO cvdao = getCRFVersionDAO();
					ArrayList nonSharedItems = cvdao.findNotSharedItemsByVersion(previousVersionId);

					// now delete the version and related info

					if (nib != null) {
						nib.deleteFromDB();
					}
					// The purpose of this new instance?
					NewCRFBean nib1 = (NewCRFBean) request.getSession().getAttribute("nib");
					// not all the items already in the item table,still need to
					// insert new ones
					if (!nonSharedItems.isEmpty()) {
						logger.info("items are not shared, need to insert");
						request.setAttribute("openQueries", nib1.getBackupItemQueries());
						nib1.setItemQueries(nib1.getBackupItemQueries());
					} else {
						// no need to insert new items, all items are already in
						// the
						// item table(shared by other versions)
						nib1.setItemQueries(new HashMap());

					}
					request.getSession().setAttribute("nib", nib1);

				} catch (OpenClinicaException pe) {
					request.getSession().setAttribute("excelErrors", nib.getDeleteErrors());
					logger.info("OpenClinicaException thrown, forwarding to CREATE_CRF_VERSION_ERROR.");
					forwardPage(Page.CREATE_CRF_VERSION_ERROR, request, response);
					return;
				}
			}

			// submit
			logger.info("commit sql");
			NewCRFBean nib1 = (NewCRFBean) request.getSession().getAttribute("nib");
			if (nib1 != null) {
				try {
					nib1.insertToDB();
					request.setAttribute("queries", nib1.getQueries());
					// For add a link to "View CRF Version Data Entry".
					// For this purpose, CRFVersion id is needed.
					// So the latest CRFVersion Id of A CRF Id is it.
					CRFVersionDAO cvdao = getCRFVersionDAO();
					ArrayList crfvbeans;

					logger.info("CRF-ID [" + version.getCrfId() + "]");
					int crfVersionId = 0;
					if (version.getCrfId() != 0) {
						crfvbeans = cvdao.findAllByCRFId(version.getCrfId());
						CRFVersionBean cvbean = (CRFVersionBean) crfvbeans.get(crfvbeans.size() - 1);
						crfVersionId = cvbean.getId();
						for (Object crfvbean : crfvbeans) {
							cvbean = (CRFVersionBean) crfvbean;
							if (crfVersionId < cvbean.getId()) {
								crfVersionId = cvbean.getId();
							}
						}
					}
					// Not needed; crfVersionId will be autoboxed in Java 5
					// this was added for the old CVS java compiler
					Integer cfvID = crfVersionId;
					if (cfvID == 0) {
						cfvID = cvdao.findCRFVersionId(nib1.getCrfId(), nib1.getVersionName());
					}
					CRFVersionBean finalVersion = (CRFVersionBean) cvdao.findByPK(cfvID);
					version.setCrfId(nib1.getCrfId());

					version.setOid(finalVersion.getOid());

					CRFBean crfBean = (CRFBean) cdao.findByPK(version.getCrfId());
					crfBean.setUpdatedDate(version.getCreatedDate());
					crfBean.setUpdater(ub);
					cdao.update(crfBean);

					// workaround to get a correct file name below
					request.setAttribute("crfVersionId", cfvID);
					// return those properties to initial values
					request.getSession().removeAttribute("version");
					request.getSession().removeAttribute("eventsForVersion");
					request.getSession().removeAttribute("itemsHaveData");
					request.getSession().removeAttribute("nib");
					request.getSession().removeAttribute("deletePreviousVersion");
					request.getSession().removeAttribute("previousVersionId");

					// save new version spreadsheet
					String tempFile = (String) request.getSession().getAttribute("tempFileName");
					if (tempFile != null) {
						logger.info("*** ^^^ *** saving new version spreadsheet" + tempFile);
						try {
							String dir = SQLInitServlet.getField("filePath");
							File f = new File(dir + "crf" + File.separator + "original" + File.separator + tempFile);
							String finalDir = dir + "crf" + File.separator + "new" + File.separator;

							if (!new File(finalDir).isDirectory()) {
								logger.info("need to create folder for excel files" + finalDir);
								// noinspection ResultOfMethodCallIgnored
								new File(finalDir).mkdirs();
							}

							String newFile = version.getCrfId() + version.getOid() + ".xls";
							logger.info("*** ^^^ *** new file: " + newFile);
							File nf = new File(finalDir + newFile);
							logger.info("copying old file " + f.getName() + " to new file " + nf.getName());
							copy(f, nf);
							// ?
						} catch (IOException ie) {
							logger.info("==============");
							addPageMessage(respage.getString("CRF_version_spreadsheet_could_not_saved_contact"),
									request);
						}

					}
					request.getSession().removeAttribute("tempFileName");
					request.getSession().removeAttribute(MODULE);
					request.getSession().removeAttribute("excelErrors");
					request.getSession().removeAttribute("htmlTab");
					forwardPage(Page.CREATE_CRF_VERSION_DONE, request, response);
				} catch (OpenClinicaException pe) {
					logger.info("--------------");
					request.getSession().setAttribute("excelErrors", nib1.getErrors());
					forwardPage(Page.CREATE_CRF_VERSION_ERROR, request, response);
				}
			} else {
				forwardPage(Page.CREATE_CRF_VERSION, request, response);
			}
		} else if ("delete".equalsIgnoreCase(action)) {
			logger.info("user wants to delete previous version");
			request.getSession().setAttribute("deletePreviousVersion", Boolean.TRUE);
			List excelErr = (ArrayList) request.getSession().getAttribute("excelErrors");
			logger.info("for overwrite CRF version, excelErr.isEmpty()=" + excelErr.isEmpty());
			if (excelErr.isEmpty()) {
				addPageMessage(resword.getString("congratulations_your_spreadsheet_no_errors"), request);
				forwardPage(Page.VIEW_SECTION_DATA_ENTRY_PREVIEW, request, response);
			} else {
				logger.info("OpenClinicaException thrown, forwarding to CREATE_CRF_VERSION_CONFIRM.");
				forwardPage(Page.CREATE_CRF_VERSION_CONFIRM, request, response);
			}

		}
	}

	/**
	 * Uploads the excel version file
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param theDir
	 *            String
	 * @param version
	 *            CRFVersionBean
	 * @throws Exception
	 */
	public String uploadFile(HttpServletRequest request, String theDir, CRFVersionBean version) throws Exception {
		UserAccountBean ub = getUserAccountBean(request);
		StudyBean currentStudy = getCurrentStudy(request);
		List<File> theFiles = new FileUploadHelper().returnFiles(request, getServletContext(), theDir);
		HashMap errors = getErrorsHolder(request);
		errors.remove("excel_file");
		String tempFile = null;
		for (File f : theFiles) {
			if (f == null || f.getName() == null) {
				logger.info("file is empty.");
				Validator.addError(errors, "excel_file", resword.getString("you_have_to_provide_spreadsheet"));
				request.getSession().setAttribute("version", version);
				return tempFile;
			} else if (!f.getName().contains(".xls") && !f.getName().contains(".XLS")) {
				logger.info("file name:" + f.getName());
				Validator.addError(errors, "excel_file",
						respage.getString("file_you_uploaded_not_seem_excel_spreadsheet"));
				request.getSession().setAttribute("version", version);
				return tempFile;

			} else {
				logger.info("file name:" + f.getName());
				tempFile = f.getName();
				FileInputStream inStream = null;
				FileInputStream inStreamClassic = null;
				SpreadSheetTableRepeating htab;
				SpreadSheetTableClassic sstc = null;
				// create newCRFBean here
				NewCRFBean nib = null;
				try {
					inStream = new FileInputStream(theDir + tempFile);

					htab = new SpreadSheetTableRepeating(inStream, ub, version.getName(), request.getLocale(),
							currentStudy.getId());

					htab.setMeasurementUnitDao(getMeasurementUnitDao());

					if (!htab.isRepeating()) {
						inStreamClassic = new FileInputStream(theDir + tempFile);
						sstc = new SpreadSheetTableClassic(inStreamClassic, ub, version.getName(), request.getLocale(),
								currentStudy.getId());
						sstc.setMeasurementUnitDao(getMeasurementUnitDao());
					}

					if (htab.isRepeating()) {
						htab.setCrfId(version.getCrfId());
						// not the best place for this but for now...
						request.getSession().setAttribute("new_table", "y");
					} else {
						sstc.setCrfId(version.getCrfId());
					}

					if (htab.isRepeating()) {
						nib = htab.toNewCRF(getDataSource(), respage);
					} else {
						nib = sstc.toNewCRF(getDataSource(), respage);
					}

					// This object is created to pull preview information out of
					// the spreadsheet
					HSSFWorkbook workbook;
					FileInputStream inputStream = null;
					try {
						inputStream = new FileInputStream(theDir + tempFile);
						workbook = new HSSFWorkbook(inputStream);
						// Store the Sections, Items, Groups, and CRF name and
						// version information
						// so they can be displayed in a preview. The Map
						// consists of the
						// names "sections," "items," "groups," and "crf_info"
						// as keys, each of which point
						// to a Map containing data on those CRF sections.

						// Check if it's the old template
						Preview preview;
						if (htab.isRepeating()) {

							// the preview uses date formatting with default
							// values in date fields: yyyy-MM-dd
							preview = new SpreadsheetPreviewNw();

						} else {
							preview = new SpreadsheetPreview();

						}
						request.getSession().setAttribute("preview_crf", preview.createCrfMetaObject(workbook));
					} catch (Exception exc) { // opening the stream could
						// throw FileNotFoundException
						exc.printStackTrace();
						String message = resword.getString("the_application_encountered_a_problem_uploading_CRF");
						logger.info(message + ": " + exc.getMessage());
						this.addPageMessage(message, request);
					} finally {
						if (inputStream != null) {
							try {
								inputStream.close();
							} catch (IOException io) {
								// ignore this close()-related exception
							}
						}
					}
					ArrayList ibs = isItemSame(nib.getItems(), version);

					if (!ibs.isEmpty()) {
						ArrayList warnings = new ArrayList();
						warnings.add(resexception.getString("you_may_not_modify_items"));
						for (Object ib1 : ibs) {
							ItemBean ib = (ItemBean) ib1;
							if (ib.getOwner().getId() == ub.getId()) {
								warnings.add(resword.getString("the_item") + " '" + ib.getName() + "' "
										+ resexception.getString("in_your_spreadsheet_already_exists")
										+ ib.getDescription() + "), DATA_TYPE(" + ib.getDataType().getName()
										+ "), UNITS(" + ib.getUnits() + "), " + resword.getString("and_or")
										+ "PHI_STATUS(" + ib.isPhiStatus() + "). UNITS " + resword.getString("and")
										+ " DATA_TYPE(PDATE to DATE) "
										+ resexception.getString("will_not_be_changed_if")
										+ "PHI, DESCRIPTION, DATA_TYPE from PDATE to DATE"
										+ resexception.getString("will_be_changed_if_you_continue"));
							} else {
								warnings.add(resword.getString("the_item") + " '" + ib.getName() + "' "
										+ resexception.getString("in_your_spreadsheet_already_exists")
										+ ib.getDescription() + "), " + "DATA_TYPE(" + ib.getDataType().getName()
										+ ") ," + "UNITS(" + ib.getUnits() + ")," + resword.getString("and_or")
										+ "PHI_STATUS(" + ib.isPhiStatus() + ")."
										+ resexception.getString("these_field_cannot_be_modified_because_not_owner"));
							}

							request.setAttribute("warnings", warnings);
						}
					}
					ItemBean ib = isResponseValid(nib.getItems(), version);
					if (ib != null) {

						nib.getErrors().add(
								resword.getString("the_item") + ": " + ib.getName() + " "
										+ resexception.getString("in_your_spreadsheet_already_exits_in_DB"));
					}
				} catch (IOException io) {
					logger.warn("Opening up the Excel file caused an error. the error message is: " + io.getMessage());

				} finally {
					if (inStream != null) {
						try {
							inStream.close();
						} catch (IOException ioe) {
							//
						}
					}
					if (inStreamClassic != null) {
						try {
							inStreamClassic.close();
						} catch (IOException ioe) {
							//
						}
					}
				}

				request.getSession().setAttribute("excelErrors", nib.getErrors());
				request.getSession().setAttribute("htmlTable", nib.getHtmlTable());
				request.getSession().setAttribute("nib", nib);
			}
		}
		return tempFile;
	}

	/**
	 * Checks whether the version can be deleted
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param previousVersionId
	 *            int
	 * @return boolean
	 */
	private boolean canDeleteVersion(HttpServletRequest request, int previousVersionId) {
		UserAccountBean ub = getUserAccountBean(request);
		CRFVersionDAO cdao = getCRFVersionDAO();
		ArrayList items;
		ArrayList itemsHaveData = new ArrayList();

		EventCRFDAO ecdao = getEventCRFDAO();
		ArrayList events = ecdao.findAllByCRFVersion(previousVersionId);
		if (!events.isEmpty()) {
			request.getSession().setAttribute("eventsForVersion", events);
			return false;
		}
		items = cdao.findNotSharedItemsByVersion(previousVersionId);
		for (Object item1 : items) {
			ItemBean item = (ItemBean) item1;
			if (ub.getId() != item.getOwner().getId()) {
				logger.info("not owner" + item.getOwner().getId() + "<>" + ub.getId());
				return false;
			}
			if (cdao.hasItemData(item.getId())) {
				itemsHaveData.add(item);
				logger.info("item has data");
				request.getSession().setAttribute("itemsHaveData", itemsHaveData);
				return false;
			}
		}

		// user is the owner and item not have data,
		// delete previous version with non-shared items
		NewCRFBean nib = (NewCRFBean) request.getSession().getAttribute("nib");
		nib.setDeleteQueries(cdao.generateDeleteQueries(previousVersionId, items));
		request.getSession().setAttribute("nib", nib);
		return true;

	}

	/**
	 * Checks whether the item with same name has the same other fields: units, phi_status if no, they are two different
	 * items, cannot have the same same
	 * 
	 * @param items
	 *            items from excel
	 * @return the items found
	 */
	private ArrayList isItemSame(HashMap items, CRFVersionBean version) {
		ItemDAO idao = getItemDAO();
		ArrayList diffItems = new ArrayList();
		Set names = items.keySet();
		for (Object name1 : names) {
			String name = (String) name1;
			ItemBean newItem = (ItemBean) idao.findByNameAndCRFId(name, version.getCrfId());
			ItemBean item = (ItemBean) items.get(name);
			if (newItem.getId() > 0) {
				if (!item.getUnits().equalsIgnoreCase(newItem.getUnits())
						|| item.isPhiStatus() != newItem.isPhiStatus()
						|| item.getDataType().getId() != newItem.getDataType().getId()
						|| !item.getDescription().equalsIgnoreCase(newItem.getDescription())) {

					logger.info("found two items with same name but different units/phi/datatype/description");

					diffItems.add(newItem);
				}
			}
		}

		return diffItems;
	}

	private ItemBean isResponseValid(HashMap items, CRFVersionBean version) {
		ItemDAO idao = getItemDAO();
		ItemFormMetadataDAO metadao = getItemFormMetadataDAO();
		Set names = items.keySet();
		for (Object name1 : names) {
			String name = (String) name1;
			ItemBean oldItem = (ItemBean) idao.findByNameAndCRFId(name, version.getCrfId());
			ItemBean item = (ItemBean) items.get(name);
			if (oldItem.getId() > 0) {// found same item in DB
				ArrayList metas = metadao.findAllByItemId(oldItem.getId());
				for (Object meta : metas) {
					ItemFormMetadataBean ifmb = (ItemFormMetadataBean) meta;
					ResponseSetBean rsb = ifmb.getResponseSet();
					if (hasDifferentOption(rsb, item.getItemMeta().getResponseSet()) != null) {
						return item;
					}
				}

			}
		}
		return null;

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

	/**
	 * When the version is added, for each non-new item OpenClinica should check the RESPONSE_OPTIONS_TEXT, and
	 * RESPONSE_VALUES used for the item in other versions of the CRF.
	 * 
	 * For a given RESPONSE_VALUES code, the associated RESPONSE_OPTIONS_TEXT string is different than in a previous
	 * version
	 * 
	 * For a given RESPONSE_OPTIONS_TEXT string, the associated RESPONSE_VALUES code is different than in a previous
	 * version
	 * 
	 * @param oldRes
	 *            ResponseSetBean
	 * @param newRes
	 *            ResponseSetBean
	 * @return The original option
	 */
	@SuppressWarnings("unused")
	public ResponseOptionBean hasDifferentOption(ResponseSetBean oldRes, ResponseSetBean newRes) {
		ArrayList oldOptions = oldRes.getOptions();
		ArrayList newOptions = newRes.getOptions();
		if (oldOptions.size() != newOptions.size()) {
			// if the sizes are different, means the options don't match
			return null;

		} else {
			for (int i = 0; i < oldOptions.size(); i++) {// from database
				ResponseOptionBean rob = (ResponseOptionBean) oldOptions.get(i);
				String text = rob.getText();
				String value = rob.getValue();
				// noinspection LoopStatementThatDoesntLoop
				for (int j = i; j < newOptions.size(); j++) {// from
					// spreadsheet
					ResponseOptionBean rob1 = (ResponseOptionBean) newOptions.get(j);

					// Fix the problem of cannot recognize the same responses
					String text1 = restoreQuotes(rob1.getText());

					String value1 = restoreQuotes(rob1.getValue());

					if (StringUtil.isBlank(text1) && StringUtil.isBlank(value1)) {
						// this response label appears in the spreadsheet
						// multiple times, so
						// ignore the checking for the repeated ones
						break;
					}
					if (text1.equalsIgnoreCase(text) && !value1.equals(value)) {
						logger.info("different response value:" + value1 + "|" + value);
						return rob;
					} else if (!text1.equalsIgnoreCase(text) && value1.equals(value)) {
						logger.info("different response text:" + text1 + "|" + text);
						return rob;
					}
					break;
				}
			}

		}
		return null;
	}

	/**
	 * Copy one file to another
	 * 
	 * @param src
	 *            File
	 * @param dst
	 *            File
	 * @throws IOException
	 */
	public void copy(File src, File dst) throws IOException {
		InputStream in = new FileInputStream(src);
		OutputStream out = new FileOutputStream(dst);

		// Transfer bytes from in to out
		byte[] buf = new byte[1024];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
	}

	/**
	 * restoreQuotes, utility function meant to replace double quotes in strings with single quote. Don''t -> Don't, for
	 * example. If the option text has single quote, it is changed to double quotes for SQL compatability, so we will
	 * change it back before the comparison
	 * 
	 * @param subj
	 *            the subject line
	 * @return A string with all the quotes escaped.
	 */
	public String restoreQuotes(String subj) {
		if (subj == null) {
			return null;
		}
		String returnme = "";
		String[] subjarray = subj.split("''");
		if (subjarray.length == 1) {
			returnme = subjarray[0];
		} else {
			for (int i = 0; i < subjarray.length - 1; i++) {
				returnme += subjarray[i];
				returnme += "'";
			}
			returnme += subjarray[subjarray.length - 1];
		}
		return returnme;
	}
}
