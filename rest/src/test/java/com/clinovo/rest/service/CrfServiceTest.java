package com.clinovo.rest.service;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.Role;
import org.akaza.openclinica.bean.core.UserType;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.test.web.servlet.ResultMatcher;

@SuppressWarnings({"unused", "unchecked"})
public class CrfServiceTest extends BaseServiceTest {

	// these fields should be the same as in the data/json/testCrf.json & data/excel/testCrf.xls
	private static final String CRF_VERSION = "v1.0";
	private static final String CRF_NAME = "FS Test CRF";

	private String getJsonData(String fileName) throws Exception {
		InputStream inputStream = null;
		try {
			inputStream = new DefaultResourceLoader().getResource("data/json/".concat(fileName)).getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
			StringBuilder out = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				out.append(line);
			}
			return out.toString();
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (Exception ex) {
				//
			}
		}
	}

	@After
	public void after() {
		CRFBean crfBean = (CRFBean) crfdao.findByName(CRF_NAME);
		if (crfBean != null && crfBean.getId() > 0) {
			deleteCrfService.deleteCrf(crfBean.getId());
		}
		super.after();
	}

	@Test
	public void testThatImportCrfServiceWorksFine() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
	}

	@Test
	public void testThatImportCrfServiceDoesNotAllowToImportSameCrfTwice() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceDoesNotAllowToImportSameCrfTwiceEvenIfVersionIsDifferent() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", "v2.0");
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", jsonObject.toString()).accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceWorksFine() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByFullName(newCrfVersion, CRF_NAME);
		assertTrue(crfVersionBean.getId() > 0);
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotAllowToImportSameCrfVersionTwice() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isOk());
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotAllowToImportCrfVersionIfCrfDoesNotExist() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsEmpty() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", "").accept(mediaType).secure(true)
				.session(session)).andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsWrongData() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", "wrong data").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfJsonDataIsMissing() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsEmpty() throws Exception {
		this.mockMvc.perform(
				post(API_CRF_JSON_IMPORT_CRF).param("jsondata", "").accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsWrongData() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", "wrong data").accept(mediaType)
				.secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceThrowsExceptionIfJsonDataIsMissing() throws Exception {
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).accept(mediaType).secure(true).session(session))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testThatImportCrfVersionServiceThrowsExceptionIfCrfNameIsEmpty() throws Exception {
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("name", "");
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfServiceDoesNotSupportHttpGet() throws Exception {
		this.mockMvc.perform(get(API_CRF_JSON_IMPORT_CRF).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatImportCrfVersionServiceDoesNotSupportHttpGet() throws Exception {
		this.mockMvc.perform(get(API_CRF_JSON_IMPORT_CRF_VERSION).accept(mediaType).secure(true).session(session))
				.andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCallImportCrfVServiceOnSiteLevel() throws Exception {
		createNewSite(studyBean.getId());
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newSite.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatItIsImpossibleToCallImportCrfVersionServiceOnSiteLevel() throws Exception {
		createNewSite(studyBean.getId());
		login(userName, UserType.SYSADMIN, Role.SYSTEM_ADMINISTRATOR, password, newSite.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(status().isInternalServerError());
	}

	@Test
	public void testThatStudyMonitorIsNotAbleToCallCrftAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewUser(UserType.SYSADMIN, Role.STUDY_MONITOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_MONITOR, newUser.getPasswd(), studyBean.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyEvaluatorIsNotAbleToCallCrftAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewUser(UserType.SYSADMIN, Role.STUDY_EVALUATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_EVALUATOR, newUser.getPasswd(), studyBean.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyCoderIsNotAbleToCallCrftAPI() throws Exception {
		ResultMatcher expectStatus = status().isForbidden();
		createNewUser(UserType.SYSADMIN, Role.STUDY_CODER);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_CODER, newUser.getPasswd(), studyBean.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyAdministratorWithoutAdministrativePrivilegesIsAbleToCallCrftAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		createNewUser(UserType.USER, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.USER, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), studyBean.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}

	@Test
	public void testThatStudyAdministratorWithAdministrativePrivilegesIsAbleToCallCrftAPI() throws Exception {
		ResultMatcher expectStatus = status().isOk();
		createNewUser(UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR);
		login(newUser.getName(), UserType.SYSADMIN, Role.STUDY_ADMINISTRATOR, newUser.getPasswd(), studyBean.getName());
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF).param("jsondata", getJsonData("testCrf.json"))
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
		String newCrfVersion = "v2.0";
		JSONObject jsonObject = new JSONObject(getJsonData("testCrf.json"));
		jsonObject.put("version", newCrfVersion);
		this.mockMvc.perform(post(API_CRF_JSON_IMPORT_CRF_VERSION).param("jsondata", jsonObject.toString())
				.accept(mediaType).secure(true).session(session)).andExpect(expectStatus);
	}
}