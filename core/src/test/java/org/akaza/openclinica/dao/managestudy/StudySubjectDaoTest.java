package org.akaza.openclinica.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.dao.StudySubjectSDVFilter;
import org.akaza.openclinica.dao.StudySubjectSDVSort;
import org.akaza.openclinica.dao.submit.ListSubjectFilter;
import org.akaza.openclinica.dao.submit.ListSubjectSort;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.Mockito.*;

/**
 * StudySubjectDaoTest class that tests the StudySubjectDao's methods.
 */
@SuppressWarnings("rawtypes")
public class StudySubjectDaoTest extends DefaultAppContextTest {

	/**
	 * Test that isStudySubjectReadyToBeSDVed returns correct value.
	 * 
	 * @throws OpenClinicaException
	 *             the suctom OpenClinicaException
	 */
	@Test
	public void testTthatisStudySubjectReadyToBeSDVedReturnsCorrectValue() throws OpenClinicaException {
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setId(1);
		boolean result = studySubjectDAO.isStudySubjectReadyToBeSDVed(currentStudy, studySubject);
		assertEquals(result, false);
	}

	/**
	 * Test that isStudySubjectSDVed returns correct value.
	 * 
	 * @throws OpenClinicaException
	 *             the suctom OpenClinicaException
	 */
	@Test
	public void testTthatIsStudySubjectSDVedReturnsCorrectValue() throws OpenClinicaException {
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		StudySubjectBean studySubject = new StudySubjectBean();
		studySubject.setId(1);
		boolean result = studySubjectDAO.isStudySubjectSDVed(currentStudy, studySubject);
		assertEquals(result, false);
	}

	/**
	 * Test that method findAllByStudySDV returns the correct collection size.
	 * 
	 * @throws OpenClinicaException
	 *             the suctom OpenClinicaException
	 */
	@Test
	public void testThatMethodFindAllByStudySDVReturnsTheCorrectCollectionSize() throws OpenClinicaException {
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		final int rowEnd = 15;
		final int rowStart = 0;
		StudySubjectSDVSort sort = new StudySubjectSDVSort();
		StudySubjectSDVFilter filter = new StudySubjectSDVFilter();
		ArrayList result = studySubjectDAO.findAllByStudySDV(currentStudy, filter, sort, rowStart, rowEnd);
		assertEquals(result.size(), 0);
	}

	/**
	 * Test that method countAllByStudySDV returns the correct value.
	 * 
	 * @throws OpenClinicaException
	 *             the suctom OpenClinicaException
	 */
	@Test
	public void testThatMethodCountAllByStudySDVReturnsTheCorrectValue() throws OpenClinicaException {
		StudyBean currentStudy = new StudyBean();
		currentStudy.setId(1);
		StudySubjectSDVFilter filter = new StudySubjectSDVFilter();
		int result = studySubjectDAO.countAllByStudySDV(currentStudy, filter);
		assertEquals(result, 0);
	}

	/**
	 * Test that the allowSDVSubject method return correct value.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testAllowSDVSubject() throws OpenClinicaException {
		boolean result = studySubjectDAO.allowSDVSubject(1, 1, 1);
		assertFalse(result);
	}

	/**
	 * Test that findByLabelAndStudy method returns a subject with randomization date.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testThatFindByLabelAndStudyReturnsSubjectWithRandomizationDate() throws OpenClinicaException {
		StudyBean sb = (StudyBean) studyDAO.findByPK(1);
		StudySubjectBean ss = studySubjectDAO.findByLabelAndStudy("ssID1", sb);
		assertNotNull(ss.getRandomizationDate());
	}

	/**
	 * Test that findByPK method returns a subject with randomization result.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testThatFindByPKReturnsSubjectWithRandomizationResult() throws OpenClinicaException {
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertNotNull(ss.getRandomizationResult());
	}

	/**
	 * Test that findByPK method returns a subject with correct randomization date.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 * @throws ParseException
	 *             the custom ParseException
	 */
	@Test
	public void testThatFindByPKReturnsSubjectWithCorrectRandomizationDate() throws OpenClinicaException,
			ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy");
		String dateInString = "17-12-2013";
		Date expectedDate = sdf.parse(dateInString);
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertEquals(expectedDate, ss.getRandomizationDate());
	}

	/**
	 * Test that findByPK method returns a subject with correct randomization result.
	 * 
	 * @throws OpenClinicaException
	 *             the custom OpenClinicaException
	 */
	@Test
	public void testThatFindByPKReturntSubjectWithCorrectRandomizationResult() throws OpenClinicaException {
		StudySubjectBean ss = (StudySubjectBean) studySubjectDAO.findByPK(1);
		assertEquals("Surgery", ss.getRandomizationResult());
	}

	/**
	 * Test that findAllByStudyIdAndLimit method returns correct collection's size.
	 */
	@Test
	public void checkThatFindAllWithAllStatesByStudyIdMethodReturnsTheCorrectCollectionsSize() {
		List<StudySubjectBean> list = studySubjectDAO.findAllWithAllStatesByStudyId(1);
		assertEquals(list.size(), 1);
	}

	/**
	 * Test that findAllByStudyIdAndLimit method returns correct collection's size.
	 */
	@Test
	public void checkThatFindAllByStudyIdAndLimitMethodReturnsTheCorrectCollectionsSize() {
		List<StudySubjectBean> list = studySubjectDAO.findAllByStudyIdAndLimit(1, false);
		assertEquals(list.size(), 1);
		list = studySubjectDAO.findAllByStudyIdAndLimit(1, true);
		assertEquals(list.size(), 1);
	}

	/**
	 * Test that getWithFilterAndSort method returns correct subjects amount.
	 */
	@Test
	public void testThatGetWithFilterAndSortReturnsCorrectAmountOfStudySubjects() {

		CriteriaCommand mockFilter = mock(ListSubjectFilter.class);
		when(mockFilter.execute("")).thenReturn("");

		CriteriaCommand mockSort = mock(ListSubjectSort.class);
		when(mockSort.execute("")).thenReturn("");

		StudyBean study = new StudyBean();
		study.setId(1);

		List<StudySubjectBean> listOfSubjects =
				studySubjectDAO.getWithFilterAndSort(study, mockFilter, mockSort, 0, 15);

		int expectedSubjectsAmount = 1;

		assertEquals(expectedSubjectsAmount, listOfSubjects.size());
	}

	@Test
	public void testThatGetCountOfStudySubjectsByStudyIdAndDynamicGroupClassIdReturnsCorrectCount() {

		int studyId = 1;
		int dynamicGroupClassId = 4;
		int actualCount;
		int expectedCount = 1;

		actualCount = studySubjectDAO.getCountOfStudySubjectsByStudyIdAndDynamicGroupClassId(studyId, dynamicGroupClassId);

		assertEquals(expectedCount, actualCount);
	}

	@Test
	public void testThatGetCountOfStudySubjectsByStudyIdAndDynamicGroupClassIdReturnsZero() {

		int studyId = 1;
		int dynamicGroupClassId = 3;
		int actualCount;
		int expectedCount = 0;

		actualCount = studySubjectDAO.getCountOfStudySubjectsByStudyIdAndDynamicGroupClassId(studyId, dynamicGroupClassId);

		assertEquals(expectedCount, actualCount);
	}
}
