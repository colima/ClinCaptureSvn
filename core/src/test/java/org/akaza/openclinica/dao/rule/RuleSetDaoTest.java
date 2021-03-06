/*******************************************************************************
 * Copyright (C) 2009-2013 Clinovo Inc.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the Lesser GNU General Public License as published by the Free Software Foundation, either version 2.1 of the License, or(at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the Lesser GNU General Public License for more details.
 * 
 * You should have received a copy of the Lesser GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package org.akaza.openclinica.dao.rule;

import java.util.List;

import org.akaza.openclinica.DefaultAppContextTest;
import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.domain.rule.RuleBean;
import org.akaza.openclinica.domain.rule.RuleSetBean;
import org.akaza.openclinica.domain.rule.RuleSetRuleBean;
import org.akaza.openclinica.domain.rule.action.ActionType;
import org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean;
import org.akaza.openclinica.domain.rule.expression.Context;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.junit.Test;

public class RuleSetDaoTest extends DefaultAppContextTest {

	@Test
	public void testFindById() {

		RuleSetBean ruleSet = null;
		ruleSet = ruleSetDao.findById(1);

		// Test RuleSet
		assertNotNull("RuleSet is null", ruleSet);
		assertEquals("The id of the retrieved RuleSet should be 1", new Integer(1), ruleSet.getId());
		assertNotNull("The Expression is null", ruleSet.getTarget());
		assertNotNull("The Context is null", ruleSet.getTarget().getContext());
		assertEquals("The context should be 1", new Integer(1), ruleSet.getTarget().getContext().getCode());

		// Test RuleSetRules
		assertEquals("The size of the RuleSetRules is not 1", new Integer(1),
				Integer.valueOf(ruleSet.getRuleSetRules().size()));

		// Test RuleActions in RuleSetRules
		assertEquals("The ActionType should be FILE_DISCREPANCY_NOTE", ActionType.FILE_DISCREPANCY_NOTE,
				ruleSet.getRuleSetRules().get(0).getActions().get(0).getActionType());
		assertEquals("The type of the Action should be DiscrepancyNoteAction",
				"org.akaza.openclinica.domain.rule.action.DiscrepancyNoteActionBean",
				ruleSet.getRuleSetRules().get(0).getActions().get(0).getClass().getName());
		assertEquals("The size of the RuleSetRules is not 2", new Integer(2),
				Integer.valueOf(ruleSet.getRuleSetRules().get(0).getActions().size()));
	}

	@Test
	public void testSaveOrUpdate() {

		RuleBean persistantRuleBean = ruleDao.findById(1);
		RuleSetBean ruleSetBean = createStubRuleSetBean(persistantRuleBean);
		ruleSetBean = ruleSetDao.saveOrUpdate(ruleSetBean);
		assertNotNull("Persistant id is null", ruleSetBean.getId());
	}

	@Test
	public void testFindByCrfEmptyResultSet() {

		CRFBean crfBean = new CRFBean();
		crfBean.setId(10);
		StudyBean studyBean = new StudyBean();
		studyBean.setId(1);
		List<RuleSetBean> persistentRuleSets = ruleSetDao.findByCrf(crfBean, studyBean);
		assertNotNull("The returned ruleSet was null", persistentRuleSets);
		assertEquals("The List size of ruleset objects should be 0 ", persistentRuleSets.size(), 0);

	}

	@Test
	public void testFindByExpression() {

		RuleSetBean ruleSet = createStubRuleSetBean();
		RuleSetBean persistentRuleSet = ruleSetDao.findByExpression(ruleSet);
		assertNotNull("The returned ruleSet was null", persistentRuleSet);
		assertEquals("The id of returned object should be 3 ", persistentRuleSet.getId(), new Integer(3));

	}

	private RuleSetBean createStubRuleSetBean(RuleBean ruleBean) {

		RuleSetBean ruleSet = new RuleSetBean();
		ruleSet.setTarget(createExpression(Context.OC_RULES_V1,
				"SE_ED2REPEA.F_CONC_V20.IG_CONC_CONCOMITANTMEDICATIONS.I_CONC_CON_MED_N"));
		RuleSetRuleBean ruleSetRule = createRuleSetRule(ruleSet, ruleBean);
		ruleSet.addRuleSetRule(ruleSetRule);
		return ruleSet;
	}

	private RuleSetBean createStubRuleSetBean() {

		RuleSetBean ruleSet = new RuleSetBean();
		ruleSet.setTarget(createExpression(Context.OC_RULES_V1,
				"SE_ED2REPEA.F_CONC_V20.IG_CONC_CONCOMITANTMEDICATIONS.I_CONC_CON_MED_NAME"));
		RuleSetRuleBean ruleSetRule = createRuleSetRule(ruleSet, null);
		ruleSet.addRuleSetRule(ruleSetRule);
		return ruleSet;
	}

	private RuleSetRuleBean createRuleSetRule(RuleSetBean ruleSet, RuleBean ruleBean) {

		RuleSetRuleBean ruleSetRule = new RuleSetRuleBean();
		DiscrepancyNoteActionBean ruleAction = new DiscrepancyNoteActionBean();
		ruleAction.setMessage("HELLO WORLD");
		ruleAction.setExpressionEvaluatesTo(true);
		ruleSetRule.addAction(ruleAction);
		ruleSetRule.setRuleSetBean(ruleSet);
		ruleSetRule.setRuleBean(ruleBean);

		return ruleSetRule;
	}

	private ExpressionBean createExpression(Context context, String value) {

		ExpressionBean expression = new ExpressionBean();
		expression.setContext(context);
		expression.setValue(value);
		return expression;
	}

	@Test
	public void testThatFindByCrfIdAndCrfOidReturnsCorrectCollectionSize() {
		CRFBean crfBean = (CRFBean) crfdao.findByPK(1);
		assertEquals(ruleSetDao.findByCrfIdAndCrfOid(crfBean).size(), 4);
	}

	@Test
	public void testThatFindByCrfVersionIdAndCrfVersionOidReturnsCorrectCollectionSize() {
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(1);
		assertEquals(ruleSetDao.findByCrfVersionIdAndCrfVersionOid(crfVersionBean).size(), 1);
	}

	@Test
	public void testThatUnbindRulesFromCrfVersionMethodWorksFine() {
		final int ruleSetId = 7;
		final int crfVersionId = 8;
		CRFVersionBean crfVersionBean = (CRFVersionBean) crfVersionDao.findByPK(crfVersionId);
		RuleSetBean ruleSetBean = ruleSetDao.findById(ruleSetId);
		assertTrue(ruleSetBean.getCrfVersionId().equals(crfVersionId));
		ruleSetDao.unbindRulesFromCrfVersion(crfVersionBean);
		ruleSetDao.getSessionFactory().getCurrentSession().evict(ruleSetBean);
		ruleSetBean = ruleSetDao.findById(ruleSetId);
		assertNull(ruleSetBean.getCrfVersionId());
	}
}
