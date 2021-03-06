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

package com.clinovo.clincapture.dao.managestudy;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.exception.OpenClinicaException;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class StudyEventDefinitionDAOTest extends DefaultAppContextTest {

	@Test
	public void testFindAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId() throws OpenClinicaException {
		int studyId = 1;
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveNotClassGroupedAndFromRemovedGroupsByStudyId(studyId);
		assertEquals(2, result.size());
	}

	@Test
	public void testFindAllActiveNotClassGroupedByStudyId() throws OpenClinicaException {
		int studyId = 1;
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveNotClassGroupedByStudyId(studyId);
		assertEquals(1, result.size());
	}

	@Test
	public void testFindAllActiveBySubjectAndStudyId() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(3);
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllActiveBySubjectAndStudyId(ssb,
				studyId);
		assertEquals(7, result.size());
	}

	@Test
	public void testFindAllActiveBySubjectFromActiveDynGroupAndStudyId_1() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(3);
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveBySubjectFromActiveDynGroupAndStudyId(ssb, studyId);
		assertEquals(7, result.size());
	}

	@Test
	public void testFindAllActiveBySubjectFromActiveDynGroupAndStudyId_2() throws OpenClinicaException {
		int studyId = 1;
		StudySubjectBean ssb = new StudySubjectBean();
		ssb.setDynamicGroupClassId(4);
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllActiveBySubjectFromActiveDynGroupAndStudyId(ssb, studyId);
		assertEquals(2, result.size());
	}

	@Test
	public void testGetEventNamesFromStudyNotReturnNull() throws OpenClinicaException {
		int studyId = 1;
		List<String> result = studyEventDefinitionDAO.getEventNamesFromStudy(studyId);
		assertNotNull(result);
	}

	@Test
	public void testGetEventNamesFromStudyCorrectSize() throws OpenClinicaException {
		int studyId = 1;
		List<String> result = studyEventDefinitionDAO.getEventNamesFromStudy(studyId);
		assertEquals(9, result.size());
	}

	@Test
	public void testFindAllAvailableByStudy_excludingEventDefinitionsRemoved() throws OpenClinicaException {
		final int studyId = 1;
		final int expactedSize = 6;
		StudyBean sb = new StudyBean();
		sb.setId(studyId);
		ArrayList<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllAvailableByStudy(sb);
		assertEquals(expactedSize, result.size());
	}

	@Test
	public void testFindAllAvailableAndOrderedByStudyGroupClassId_excludingEventDefinitionsRemoved_Test1()
			throws OpenClinicaException {
		final int studyGroupClassId = 3;
		final int expactedSize = 4;
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllAvailableAndOrderedByStudyGroupClassId(studyGroupClassId);
		assertEquals(expactedSize, result.size());
	}

	@Test
	public void testFindAllAvailableAndOrderedByStudyGroupClassId_excludingEventDefinitionsRemoved_Test2()
			throws OpenClinicaException {
		final int studyGroupClassId = 4;
		final int expactedSize = 1;
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO
				.findAllAvailableAndOrderedByStudyGroupClassId(studyGroupClassId);
		assertEquals(expactedSize, result.size());
	}

	@Test
	public void testThatFindAllActiveByStudyIdAndCRFIdReturnsNotNull() {
		int crfId = 3;
		int studyId = 1;
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllActiveByStudyIdAndCRFId(crfId, studyId);
		assertNotNull(result);
	}

	@Test
	public void testThatFindAllActiveByStudyIdAndCRFIdReturnsCorrectValue() {
		int crfId = 3;
		int studyId = 1;
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllActiveByStudyIdAndCRFId(crfId, studyId);
		assertEquals(1, result.size());
	}

	@Test
	public void testThatFindAllAvailableWithEvaluableCRFByStudyReturnsCorrectValue() {
		StudyBean study = (StudyBean) studyDAO.findByPK(1);
		List<StudyEventDefinitionBean> result = studyEventDefinitionDAO.findAllAvailableWithEvaluableCRFByStudy(study);
		assertEquals(2, result.size());
	}

	@Test
	public void testThatUpdateStatusWorksFine() throws OpenClinicaException {
		StudyEventDefinitionBean studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO
				.findByPK(1);
		assertEquals(studyEventDefinitionBean.getStatus(), Status.AVAILABLE);
		studyEventDefinitionBean.setUpdater((UserAccountBean) userAccountDAO.findByPK(1));
		studyEventDefinitionBean.setStatus(Status.DELETED);
		studyEventDefinitionDAO.updateStatus(studyEventDefinitionBean);
		studyEventDefinitionBean = (StudyEventDefinitionBean) studyEventDefinitionDAO.findByPK(1);
		assertEquals(studyEventDefinitionBean.getStatus(), Status.DELETED);
	}
}
