package com.clinovo.lib.crf.service.impl;

import java.util.Locale;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import com.clinovo.lib.crf.builder.CrfBuilder;
import com.clinovo.lib.crf.enums.CRFSource;
import com.clinovo.lib.crf.factory.CrfBuilderFactory;

public class SpreadSheetImportCrfServiceImplTest extends DefaultAppContextTest {

	private StudyBean studyBean;

	private UserAccountBean owner;

	private CrfBuilder crfBuilder;

	@Autowired
	private CrfBuilderFactory crfBuilderFactory;

	@Mock
	private MessageSource messageSource;

	@Override
	protected void restoreDb() throws Exception {
		// do not restore db
	}

	@Before
	public void before() {
		studyBean = (StudyBean) studyDAO.findByPK(1);
		owner = (UserAccountBean) userAccountDAO.findByPK(1);
	}

	@After
	public void after() throws Exception {
		if (crfBuilder != null && crfBuilder.getCrfBean() != null && crfBuilder.getCrfBean().getId() > 0) {
			deleteCrfService.deleteCrf(crfBuilder.getCrfBean(), owner, Locale.ENGLISH, false);
		}
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfSections() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getSections().size(), 1);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectQuantityOfItemGroups() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItemGroups().size(), 3);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfJsonWithCorrectQuantityOfItems() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getItems().size(), 9);
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfName() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfBean().getName(), "FS Test CRF");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfVersion() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(crfBuilder.getCrfVersionBean().getName(), "v1.0");
	}

	@Test
	public void testThatCrfBuilderProcessesTheTestCrfWithCorrectCrfSource() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		assertEquals(CRFSource.SOURCE_FORM_EXCEL.getSourceName(), crfBuilder.getCrfBean().getSource());
	}

	@Test
	public void testThatCrfBuilderSavesDataFromTheTestCrfCorrectly() throws Exception {
		crfBuilder = crfBuilderFactory.getCrfBuilder(getWorkbook("testCrf.xls"), studyBean, owner, Locale.ENGLISH,
				messageSource);
		crfBuilder.build();
		crfBuilder.save();
		CRFBean crfBean = (CRFBean) crfdao.findByPK(crfBuilder.getCrfBean().getId());
		assertEquals(crfBean.getName(), "FS Test CRF");
		assertTrue(crfBean.getId() > 0);
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfBuilder.getCrfBean().getId());
		assertEquals(crfVersionBean.getName(), "v1.0");
		assertTrue(crfVersionBean.getId() > 0);
	}
}
