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

package org.akaza.openclinica.service.extract;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.zip.ZipOutputStream;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.extract.ArchivedDatasetFileBean;
import org.akaza.openclinica.bean.extract.DatasetBean;
import org.akaza.openclinica.bean.extract.DisplayItemHeaderBean;
import org.akaza.openclinica.bean.extract.ExportFormatBean;
import org.akaza.openclinica.bean.extract.ExtractBean;
import org.akaza.openclinica.bean.extract.SPSSReportBean;
import org.akaza.openclinica.bean.extract.SPSSVariableNameValidator;
import org.akaza.openclinica.bean.extract.TabReportBean;
import org.akaza.openclinica.bean.extract.odm.AdminDataReportBean;
import org.akaza.openclinica.bean.extract.odm.FullReportBean;
import org.akaza.openclinica.bean.extract.odm.MetaDataReportBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.odmbeans.ODMBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.dao.extract.ArchivedDatasetFileDAO;
import org.akaza.openclinica.dao.extract.DatasetDAO;
import org.akaza.openclinica.dao.hibernate.RuleSetRuleDao;
import org.akaza.openclinica.dao.submit.ItemFormMetadataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.logic.odmExport.AdminDataCollector;
import org.akaza.openclinica.logic.odmExport.ClinicalDataCollector;
import org.akaza.openclinica.logic.odmExport.ClinicalDataUnit;
import org.akaza.openclinica.logic.odmExport.MetaDataCollector;
import org.akaza.openclinica.logic.odmExport.OdmStudyBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.clinovo.util.OdmExtractUtil;

@SuppressWarnings({"rawtypes", "unchecked"})
public class GenerateExtractFileService {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	private final DataSource ds;
	private Locale locale;
	public static ResourceBundle resword;
	private final UserAccountBean userBean;
	private final CoreResources coreResources;

	private static File files[] = null;
	private static List<File> oldFiles = new LinkedList<File>();
	private final RuleSetRuleDao ruleSetRuleDao;

	public GenerateExtractFileService(DataSource ds, Locale locale, UserAccountBean userBean,
			CoreResources coreResources, RuleSetRuleDao ruleSetRuleDao) {
		this.ds = ds;
		this.locale = locale;
		this.userBean = userBean;
		this.coreResources = coreResources;
		this.ruleSetRuleDao = ruleSetRuleDao;
	}

	public GenerateExtractFileService(DataSource ds, UserAccountBean userBean, CoreResources coreResources,
			RuleSetRuleDao ruleSetRuleDao) {
		this.ds = ds;
		this.locale = new Locale("en");
		this.userBean = userBean;
		this.coreResources = coreResources;
		this.ruleSetRuleDao = ruleSetRuleDao;
	}

	public void setUpResourceBundles() {
		ResourceBundleProvider.updateLocale(locale);
		resword = ResourceBundleProvider.getWordsBundle(locale);
	}

	public HashMap<String, Integer> createTabFile(ExtractBean eb, long sysTimeBegin, String generalFileDir,
			DatasetBean datasetBean, int activeStudyId, int parentStudyId, String generalFileDirCopy) {

		TabReportBean answer = new TabReportBean();

		DatasetDAO dsdao = new DatasetDAO(ds);
		// create the extract bean here, tbh
		eb = dsdao.getDatasetData(eb, activeStudyId, parentStudyId);
		eb.getMetadata();
		eb.computeReport(answer);

		long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
		String TXTFileName = datasetBean.getName() + "_tab.xls";

		int fId = this.createFile(TXTFileName, generalFileDir, answer.toString(), datasetBean, sysTimeEnd,
				ExportFormatBean.TXTFILE, true);
		if (!"".equals(generalFileDirCopy)) {
			this.createFile(TXTFileName, generalFileDirCopy, answer.toString(), datasetBean, sysTimeEnd,
					ExportFormatBean.TXTFILE, false);
		}
		logger.info("created txt file");
		HashMap answerMap = new HashMap<String, Integer>();
		answerMap.put(TXTFileName, new Integer(fId));
		return answerMap;
	}

	private Integer getStudySubjectNumber(String studySubjectNumber) {
		try {
			Integer value = Integer.valueOf(studySubjectNumber);
			return value > 0 ? value : 99;
		} catch (NumberFormatException e) {
			return 99;
		}
	}

	/**
	 * Creates ODM file. Note that this is created to be backwards-compatible with previous versions of OpenClinica-web.
	 * i.e. we remove the boolean zipped variable.
	 *
	 * @param odmVersion
	 *            String
	 * @param sysTimeBegin
	 *            long
	 * @param generalFileDir
	 *            String
	 * @param datasetBean
	 *            DatasetBean
	 * @param currentStudy
	 *            StudyBean
	 * @param generalFileDirCopy
	 *            String
	 * @param eb
	 *            ExtractBean
	 * @param currentStudyId
	 *            Integer
	 * @param parentStudyId
	 *            Integer
	 * @param studySubjectNumber
	 *            String
	 * @return Map
	 */
	public HashMap<String, Integer> createODMFile(String odmVersion, long sysTimeBegin, String generalFileDir,
			DatasetBean datasetBean, StudyBean currentStudy, String generalFileDirCopy, ExtractBean eb,
			Integer currentStudyId, Integer parentStudyId, String studySubjectNumber) {
		// default zipped - true
		return createODMFile(odmVersion, sysTimeBegin, generalFileDir, datasetBean, currentStudy, generalFileDirCopy,
				eb, currentStudyId, parentStudyId, studySubjectNumber, true, true, true, false, null);
	}

	/**
	 * Creates ODM file.
	 * 
	 * @param odmVersion
	 *            String
	 * @param sysTimeBegin
	 *            long
	 * @param generalFileDir
	 *            String
	 * @param datasetBean
	 *            DatasetBean
	 * @param currentStudy
	 *            StudyBean
	 * @param generalFileDirCopy
	 *            String
	 * @param eb
	 *            ExtractBean
	 * @param currentStudyId
	 *            Integer
	 * @param parentStudyId
	 *            Integer
	 * @param studySubjectNumber
	 *            String
	 * @param zipped
	 *            boolean
	 * @param saveToDB
	 *            boolean
	 * @param deleteOld
	 *            boolean
	 * @param skipBlanks
	 *            boolean
	 * @param odmType
	 *            String
	 * @return Map
	 */
	public HashMap<String, Integer> createODMFile(String odmVersion, long sysTimeBegin, String generalFileDir,
			DatasetBean datasetBean, StudyBean currentStudy, String generalFileDirCopy, ExtractBean eb,
			Integer currentStudyId, Integer parentStudyId, String studySubjectNumber, boolean zipped, boolean saveToDB,
			boolean deleteOld, boolean skipBlanks, String odmType) {
		Integer ssNumber = getStudySubjectNumber(studySubjectNumber);
		MetaDataCollector mdc = new MetaDataCollector(ds, datasetBean, currentStudy, ruleSetRuleDao);
		AdminDataCollector adc = new AdminDataCollector(ds, datasetBean, currentStudy);
		ClinicalDataCollector cdc = new ClinicalDataCollector(ds, datasetBean, currentStudy);

		final int textLength = 200;
		MetaDataCollector.setTextLength(textLength);
		if (deleteOld) {
			File file = new File(generalFileDir);
			if (file.isDirectory()) {
				files = file.listFiles();
				if (files != null) {
					oldFiles = Arrays.asList(files);
				}
			}
		}
		if (odmVersion != null) {
			// by default odmVersion is 1.2
			if ("1.3".equals(odmVersion)) {
				ODMBean odmb = new ODMBean();
				odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3 ODM1-3-0.xsd");
				ArrayList<String> xmlnsList = new ArrayList<String>();
				xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"");
				odmb.setXmlnsList(xmlnsList);
				odmb.setODMVersion("1.3");
				mdc.setODMBean(odmb);
				adc.setOdmbean(odmb);
				cdc.setODMBean(odmb);
			} else if ("oc1.2".equals(odmVersion)) {
				ODMBean odmb = new ODMBean();
				odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.2 OpenClinica-ODM1-2-1-OC1.xsd");
				ArrayList<String> xmlnsList = new ArrayList<String>();
				xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.2\"");
				xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/odm_ext_v121/v3.1\"");
				xmlnsList.add("xmlns:OpenClinicaRules=\"http://www.openclinica.org/ns/rules/v3.1\"");
				odmb.setXmlnsList(xmlnsList);
				odmb.setODMVersion("oc1.2");
				mdc.setODMBean(odmb);
				adc.setOdmbean(odmb);
				cdc.setODMBean(odmb);
			} else if ("oc1.3".equals(odmVersion)) {
				ODMBean odmb = mdc.getODMBean();
				odmb.setSchemaLocation("http://www.cdisc.org/ns/odm/v1.3 OpenClinica-ODM1-3-0-OC2-0.xsd");
				ArrayList<String> xmlnsList = new ArrayList<String>();
				xmlnsList.add("xmlns=\"http://www.cdisc.org/ns/odm/v1.3\"");
				xmlnsList.add("xmlns:OpenClinica=\"http://www.openclinica.org/ns/odm_ext_v130/v3.1\"");
				xmlnsList.add("xmlns:OpenClinicaRules=\"http://www.openclinica.org/ns/rules/v3.1\"");
				odmb.setXmlnsList(xmlnsList);
				odmb.setODMVersion("oc1.3");
				odmb.setOdmType(odmType);
				mdc.setODMBean(odmb);
				adc.setOdmbean(odmb);
				cdc.setODMBean(odmb);
			}

		}

		mdc.collectFileData();
		MetaDataReportBean metaReport = new MetaDataReportBean(mdc.getOdmStudyMap(), coreResources);
		metaReport.setODMVersion(odmVersion);
		metaReport.setOdmBean(mdc.getODMBean());
		metaReport.createChunkedOdmXml(Boolean.TRUE);

		long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
		String odmXmlFileName = mdc.getODMBean().getFileOID() + ".xml";
		this.createFileK(odmXmlFileName, generalFileDir, metaReport.getXmlOutput().toString(), datasetBean, sysTimeEnd,
				ExportFormatBean.XMLFILE, false, zipped, deleteOld);
		if (!"".equals(generalFileDirCopy)) {
			this.createFileK(odmXmlFileName, generalFileDirCopy, metaReport.getXmlOutput().toString(), datasetBean,
					sysTimeEnd, ExportFormatBean.XMLFILE, false, zipped, deleteOld);
		}

		adc.collectFileData();
		AdminDataReportBean adminReport = new AdminDataReportBean(adc.getOdmAdminDataMap());
		adminReport.setODMVersion(odmVersion);
		adminReport.setOdmBean(mdc.getODMBean());
		adminReport.createChunkedOdmXml(Boolean.TRUE);

		sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
		this.createFileK(odmXmlFileName, generalFileDir, adminReport.getXmlOutput().toString(), datasetBean,
				sysTimeEnd, ExportFormatBean.XMLFILE, false, zipped, deleteOld);
		if (!"".equals(generalFileDirCopy)) {
			this.createFileK(odmXmlFileName, generalFileDirCopy, adminReport.getXmlOutput().toString(), datasetBean,
					sysTimeEnd, ExportFormatBean.XMLFILE, false, zipped, deleteOld);
		}

		DatasetDAO dsdao = new DatasetDAO(ds);
		String sql = eb.getDataset().getSQLStatement();
		String stSedIn = dsdao.parseSQLDataset(sql, true, true);
		String stItemIdIn = dsdao.parseSQLDataset(sql, false, true);
		int datasetItemStatusId = eb.getDataset().getDatasetItemStatus().getId();
		String ecStatusConstraint = dsdao.getECStatusConstraint(datasetItemStatusId);
		String itStatusConstraint = dsdao.getItemDataStatusConstraint(datasetItemStatusId);

		Map<Integer, StudyBean> studyCache = new HashMap<Integer, StudyBean>();
		List<Map<Integer, Integer>> pairList = new ArrayList<Map<Integer, Integer>>();
		for (OdmStudyBase odmStudyBase : cdc.getStudyBaseMap().values()) {
			studyCache.put(odmStudyBase.getStudy().getId(), odmStudyBase.getStudy());
			pairList.add(OdmExtractUtil.pair(odmStudyBase.getStudy().getId(), 0));
		}

		Map<Integer, List<OdmExtractUtil.StudySubjectsHolder>> mapOfStudySubjectsHolderList = dsdao
				.selectStudySubjects(pairList, stSedIn, stItemIdIn, dsdao.genDatabaseDateConstraint(eb),
						ecStatusConstraint, itStatusConstraint, ssNumber);
		for (Integer studyIdKey : mapOfStudySubjectsHolderList.keySet()) {
			List<OdmExtractUtil.StudySubjectsHolder> studySubjectsHolderList = mapOfStudySubjectsHolderList
					.get(studyIdKey);
			for (OdmExtractUtil.StudySubjectsHolder studySubjectsHolder : studySubjectsHolderList) {
				boolean firstIteration = studySubjectsHolderList.indexOf(studySubjectsHolder) == 0;
				boolean lastIteration = studySubjectsHolderList.indexOf(studySubjectsHolder) == studySubjectsHolderList
						.size() - 1;

				ClinicalDataUnit cdata = new ClinicalDataUnit(ds, datasetBean, cdc.getOdmbean(),
						studyCache.get(studyIdKey), cdc.getCategory(), studySubjectsHolder.getStudySubjectIds());
				cdata.setCategory(cdc.getCategory());
				cdata.setSkipBlanks(skipBlanks);
				cdata.collectOdmClinicalData();

				FullReportBean report = new FullReportBean();
				report.setClinicalData(cdata.getOdmClinicalData());
				report.setOdmStudyMap(mdc.getOdmStudyMap());
				report.setODMVersion(odmVersion);
				report.setOdmBean(mdc.getODMBean());
				if (firstIteration && lastIteration) {
					report.createChunkedOdmXml(Boolean.TRUE, true, true);
				} else if (firstIteration) {
					report.createChunkedOdmXml(Boolean.TRUE, true, false);
				} else if (lastIteration) {
					report.createChunkedOdmXml(Boolean.TRUE, false, true);
				} else {
					report.createChunkedOdmXml(Boolean.TRUE, false, false);
				}
				this.createFileK(odmXmlFileName, generalFileDir, report.getXmlOutput().toString(), datasetBean,
						sysTimeEnd, ExportFormatBean.XMLFILE, false, zipped, deleteOld);
				if (!"".equals(generalFileDirCopy)) {
					this.createFileK(odmXmlFileName, generalFileDirCopy, report.getXmlOutput().toString(), datasetBean,
							sysTimeEnd, ExportFormatBean.XMLFILE, false, zipped, deleteOld);
				}
			}
		}

		sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;
		int fId = this.createFileK(odmXmlFileName, generalFileDir, "</ODM>", datasetBean, sysTimeEnd,
				ExportFormatBean.XMLFILE, saveToDB, zipped, deleteOld);
		if (!"".equals(generalFileDirCopy)) {
			this.createFileK(odmXmlFileName, generalFileDirCopy, "</ODM>", datasetBean, sysTimeEnd,
					ExportFormatBean.XMLFILE, false, zipped, deleteOld);
		}

		HashMap answerMap = new HashMap<String, Integer>();
		answerMap.put(odmXmlFileName, fId);
		return answerMap;
	}

	public List<File> getOldFiles() {
		return oldFiles;
	}

	/**
	 * createSPSSFile, added by tbh, 01/2009
	 * 
	 * @param db
	 * @param eb
	 * @param currentstudyid
	 * @param parentstudy
	 * @return
	 */
	public HashMap<String, Integer> createSPSSFile(DatasetBean db, ExtractBean eb2, StudyBean currentStudy,
			StudyBean parentStudy, long sysTimeBegin, String generalFileDir, SPSSReportBean answer,
			String generalFileDirCopy) {
		setUpResourceBundles();

		String SPSSFileName = db.getName() + "_data_spss.dat";
		String DDLFileName = db.getName() + "_ddl_spss.sps";
		String ZIPFileName = db.getName() + "_spss";

		SPSSVariableNameValidator svnv = new SPSSVariableNameValidator();
		answer.setDatFileName(SPSSFileName);

		answer.setItems(eb2.getItemNames());// set up items here to get
		// itemMetadata

		ItemFormMetadataDAO imfdao = new ItemFormMetadataDAO(ds);
		ArrayList items = answer.getItems();
		for (int i = 0; i < items.size(); i++) {
			DisplayItemHeaderBean dih = (DisplayItemHeaderBean) items.get(i);
			ItemBean item = dih.getItem();
			ArrayList metas = imfdao.findAllByItemId(item.getId());
			item.setItemMetas(metas);

		}

		HashMap eventDescs = new HashMap<String, String>();

		eventDescs = eb2.getEventDescriptions();

		eventDescs.put("SubjID", resword.getString("study_subject_ID"));
		eventDescs.put("ProtocolID", resword.getString("protocol_ID_site_ID"));
		eventDescs.put("DOB", resword.getString("date_of_birth"));
		eventDescs.put("YOB", resword.getString("year_of_birth"));
		eventDescs.put("Gender", resword.getString("gender"));
		answer.setDescriptions(eventDescs);

		ArrayList generatedReports = new ArrayList<String>();
		try {
			generatedReports.add(answer.getMetadataFile(svnv, eb2).toString());
			generatedReports.add(answer.getDataFile().toString());
		} catch (IndexOutOfBoundsException i) {
			generatedReports.add(answer.getMetadataFile(svnv, eb2).toString());
			logger.debug("throw the error here");
		}

		long sysTimeEnd = System.currentTimeMillis() - sysTimeBegin;

		ArrayList titles = new ArrayList();
		titles.add(DDLFileName);
		titles.add(SPSSFileName);

		// create new createFile method that accepts array lists to
		// put into zip files
		int fId = this.createFile(ZIPFileName, titles, generalFileDir, generatedReports, db, sysTimeEnd,
				ExportFormatBean.TXTFILE, true);
		if (!"".equals(generalFileDirCopy)) {
			this.createFile(ZIPFileName, titles, generalFileDirCopy, generatedReports, db, sysTimeEnd,
					ExportFormatBean.TXTFILE, false);
		}
		// return DDLFileName;
		HashMap answerMap = new HashMap<String, Integer>();
		answerMap.put(DDLFileName, new Integer(fId));
		return answerMap;
	}

	public int createFile(String zipName, ArrayList names, String dir, ArrayList contents, DatasetBean datasetBean,
			long time, ExportFormatBean efb, boolean saveToDB) {
		ArchivedDatasetFileBean fbFinal = new ArchivedDatasetFileBean();
		zipName = zipName.replaceAll(" ", "_");
		fbFinal.setId(0);
		BufferedWriter w = null;
		try {
			File complete = new File(dir);
			if (!complete.isDirectory()) {
				complete.mkdirs();
			}
			int totalSize = 0;
			ZipOutputStream z = new ZipOutputStream(new FileOutputStream(new File(complete, zipName + ".zip")));
			FileInputStream is = null;
			for (int i = 0; i < names.size(); i++) {
				String name = (String) names.get(i);
				name = name.replaceAll(" ", "_");
				String content = (String) contents.get(i);
				File newFile = new File(complete, name);
				newFile.setLastModified(System.currentTimeMillis());

				w = new BufferedWriter(new FileWriter(newFile));
				w.write(content);
				w.close();
				logger.info("finished writing the text file...");
				// now, we write the file to the zip file
				is = new FileInputStream(newFile);

				logger.info("created zip output stream...");

				z.putNextEntry(new java.util.zip.ZipEntry(name));

				int bytesRead;
				byte[] buff = new byte[512];

				while ((bytesRead = is.read(buff)) != -1) {
					z.write(buff, 0, bytesRead);
					totalSize += 512;
				}
				z.closeEntry();
				is.close();
				if (CoreResources.getField("dataset_file_delete").equalsIgnoreCase("true")
						|| CoreResources.getField("dataset_file_delete").equals("")) {
					newFile.delete();
				}

			}
			logger.info("writing buffer...");
			// }
			z.flush();
			z.finish();
			z.close();

			if (is != null) {
				try {
					is.close();
				} catch (java.io.IOException ie) {
					ie.printStackTrace();
				}
			}
			logger.info("finished zipping up file...");
			// set up the zip to go into the database
			if (saveToDB) {
				ArchivedDatasetFileBean fb = new ArchivedDatasetFileBean();
				fb.setName(zipName + ".zip");
				fb.setFileReference(dir + zipName + ".zip");
				// current location of the file on the system
				fb.setFileSize(totalSize);
				// set the above to compressed size?
				fb.setRunTime((int) time);
				// need to set this in milliseconds, get it passed from above
				// methods?
				fb.setDatasetId(datasetBean.getId());
				fb.setExportFormatBean(efb);
				fb.setExportFormatId(efb.getId());
				fb.setOwner(userBean);
				fb.setOwnerId(userBean.getId());
				fb.setDateCreated(new Date(System.currentTimeMillis()));

				boolean write = true;
				ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(ds);

				if (write) {
					fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
					logger.info("Created ADSFile!: " + fbFinal.getId() + " for " + zipName + ".zip");
				} else {
					logger.info("duplicate found: " + fb.getName());
				}
			}
			// created in database!

		} catch (Exception e) {
			logger.warn(e.getMessage());
			System.out.println("-- exception at create file: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (w != null)
				try {
					w.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return fbFinal.getId();
	}

	public int createFileK(String name, String dir, String content, DatasetBean datasetBean, long time,
			ExportFormatBean efb, boolean saveToDB, boolean zipped, boolean deleteOld) {
		ArchivedDatasetFileBean fbFinal = new ArchivedDatasetFileBean();
		name = name.replaceAll(" ", "_");
		fbFinal.setId(0);
		BufferedWriter w = null;
		try {

			File complete = new File(dir);
			if (!complete.isDirectory()) {
				complete.mkdirs();
			}

			File oldFile = new File(complete, name);
			File newFile = null;
			if (oldFile.exists()) {
				newFile = oldFile;
				if (oldFiles != null || !oldFiles.isEmpty())
					oldFiles.remove(oldFile);
			} else {
				newFile = new File(complete, name);
			}

			// File
			newFile.setLastModified(System.currentTimeMillis());

			w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile, true), "UTF-8"));
			w.write(content);
			w.close();
			logger.info("finished writing the text file...");
			if (saveToDB) {
				ArchivedDatasetFileBean fb = new ArchivedDatasetFileBean();
				if (zipped) {
					fb.setName(name + ".zip");
					fb.setFileReference(dir + name + ".zip");
				} else {
					fb.setName(name);
					fb.setFileReference(dir + name);
				}
				fb.setFileSize((int) newFile.length());
				fb.setRunTime((int) time);
				fb.setDatasetId(datasetBean.getId());
				fb.setExportFormatBean(efb);
				fb.setExportFormatId(efb.getId());
				fb.setOwner(userBean);
				fb.setOwnerId(userBean.getId());
				fb.setDateCreated(new Date(System.currentTimeMillis()));
				boolean write = true;
				ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(ds);
				// eliminating all checks so that we create multiple files, tbh 6-7
				if (write) {
					fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
				} else {
					logger.info("duplicate found: " + fb.getName());
				}
			}
			// created in database!

		} catch (Exception e) {
			logger.warn(e.getMessage());
			System.out.println("-- exception thrown at createFile: " + e.getMessage());
			logger.info("-- exception thrown at createFile: " + e.getMessage());
			e.printStackTrace();
		} finally {
			if (w != null)
				try {
					w.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return fbFinal.getId();
	}

	public int createFile(String name, String dir, String content, DatasetBean datasetBean, long time,
			ExportFormatBean efb, boolean saveToDB) {
		ArchivedDatasetFileBean fbFinal = new ArchivedDatasetFileBean();
		name = name.replaceAll(" ", "_");
		fbFinal.setId(0);
		try {
			File complete = new File(dir);
			if (!complete.isDirectory()) {
				complete.mkdirs();
			}
			File newFile = new File(complete, name);
			newFile.setLastModified(System.currentTimeMillis());

			BufferedWriter w = new BufferedWriter(new FileWriter(newFile));
			w.write(content);
			w.close();
			logger.info("finished writing the text file...");
			// now, we write the file to the zip file
			FileInputStream is = new FileInputStream(newFile);

			@SuppressWarnings("resource")
			ZipOutputStream z = new ZipOutputStream(new FileOutputStream(new File(complete, name + ".zip")));
			logger.info("created zip output stream...");
			// we write over the content no matter what
			// we then check to make sure there are no duplicates
			// TODO need to change the above -- save all content!
			// z.write(content);
			z.putNextEntry(new java.util.zip.ZipEntry(name));
			int bytesRead;
			byte[] buff = new byte[512];
			while ((bytesRead = is.read(buff)) != -1) {
				z.write(buff, 0, bytesRead);
			}
			logger.info("writing buffer...");
			// }
			z.closeEntry();
			z.finish();
			if (is != null) {
				try {
					is.close();
				} catch (java.io.IOException ie) {
					ie.printStackTrace();
				}
			}
			logger.info("finished zipping up file...");
			// set up the zip to go into the database
			if (saveToDB) {
				ArchivedDatasetFileBean fb = new ArchivedDatasetFileBean();
				fb.setName(name + ".zip");
				fb.setFileReference(dir + name + ".zip");
				fb.setFileSize((int) newFile.length());
				fb.setRunTime((int) time);
				fb.setDatasetId(datasetBean.getId());
				fb.setExportFormatBean(efb);
				fb.setExportFormatId(efb.getId());
				fb.setOwner(userBean);
				fb.setOwnerId(userBean.getId());
				fb.setDateCreated(new Date(System.currentTimeMillis()));
				boolean write = true;
				ArchivedDatasetFileDAO asdfDAO = new ArchivedDatasetFileDAO(ds);
				if (write) {
					fbFinal = (ArchivedDatasetFileBean) asdfDAO.create(fb);
				} else {
					logger.info("duplicate found: " + fb.getName());
				}
			}
			// created in database!

		} catch (Exception e) {
			logger.warn(e.getMessage());
			System.out.println("-- exception thrown at createFile: " + e.getMessage());
			logger.info("-- exception thrown at createFile: " + e.getMessage());
			e.printStackTrace();
		}

		return fbFinal.getId();
	}

	public ExtractBean generateExtractBean(DatasetBean dsetBean, StudyBean currentStudy, StudyBean parentStudy) {
		ExtractBean eb = new ExtractBean(ds);
		eb.setDataset(dsetBean);
		eb.setShowUniqueId(CoreResources.getField("show_unique_id"));
		eb.setStudy(currentStudy);
		eb.setParentStudy(parentStudy);
		eb.setDateCreated(new java.util.Date());
		return eb;
	}

	/**
	 * To zip the xml files and delete the intermediate files.
	 * 
	 * @param name
	 * @param dir
	 * @throws IOException
	 */

	public void zipFile(String name, String dir) throws IOException {
		File complete = new File(dir);
		if (!complete.isDirectory()) {
			complete.mkdirs();
		}

		File[] interXMLS = complete.listFiles();
		List<File> temp = new LinkedList<File>(Arrays.asList(interXMLS));

		File oldFile = new File(complete, name);

		File newFile = null;
		if (oldFile.exists()) {
			newFile = oldFile;

		} else {
			newFile = new File(complete, name);
		}
		// now, we write the file to the zip file
		FileInputStream is = new FileInputStream(newFile);
		ZipOutputStream z = new ZipOutputStream(new FileOutputStream(new File(complete, name + ".zip")));
		if (oldFiles != null || !oldFiles.isEmpty()) {

			if (oldFiles.contains(new File(complete, name + ".zip"))) {
				oldFiles.remove(new File(complete, name + ".zip"));// Dont delete the files which u r just creating

			}
		}
		logger.info("created zip output stream...");
		// we write over the content no matter what
		// we then check to make sure there are no duplicates
		// TODO need to change the above -- save all content!
		z.putNextEntry(new java.util.zip.ZipEntry(name));
		int bytesRead;
		byte[] buff = new byte[512];
		while ((bytesRead = is.read(buff)) != -1) {
			z.write(buff, 0, bytesRead);
		}
		logger.info("writing buffer...");
		// }

		z.closeEntry();
		z.finish();
		if (z != null)
			z.close();
		if (is != null) {
			try {
				is.close();
			} catch (java.io.IOException ie) {
				ie.printStackTrace();
			}
		}
		// Adding the logic to delete the intermediate xmls
		oldFiles = temp;
		logger.info("finished zipping up file...");
	}
}
