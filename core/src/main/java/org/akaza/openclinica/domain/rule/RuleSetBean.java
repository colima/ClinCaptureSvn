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
 *
 * Copyright 2003-2008 Akaza Research 
 */
package org.akaza.openclinica.domain.rule;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.managestudy.StudyBean;
import org.akaza.openclinica.bean.managestudy.StudyEventDefinitionBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemGroupBean;
import org.akaza.openclinica.domain.AbstractAuditableMutableDomainObject;
import org.akaza.openclinica.domain.rule.expression.ExpressionBean;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * <p>
 * RuleSetBean, Holds a collection of Rules & Actions
 * </p>
 * 
 * @author Krikor Krumlian
 */
@Entity
@Table(name = "rule_set")
@GenericGenerator(name = "id-generator", strategy = "native", parameters = { @Parameter(name = "sequence_name", value = "rule_set_id_seq") })
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class RuleSetBean extends AbstractAuditableMutableDomainObject {

	private StudyEventDefinitionBean studyEventDefinition;
	private StudyBean study;
	private CRFBean crf;
	private CRFVersionBean crfVersion;
	private ItemBean item;

	private List<RuleSetRuleBean> ruleSetRules;
	private ExpressionBean target;
	private ExpressionBean originalTarget;

	// transient properties
	private List<ExpressionBean> expressions; // itemGroup & item populated when RuleSets are retrieved
	private ItemGroupBean itemGroup;

	// TODO : Pending conversion of the objects below to use Hibernate
	private Integer studyEventDefinitionId;
	private Integer studyId;
	private Integer crfId;
	private Integer crfVersionId;
	private Integer itemId;
	private Integer itemGroupId;

	// Business

	@Transient
	public void addRuleSetRule(RuleSetRuleBean ruleSetRuleBean) {
		if (this.ruleSetRules == null)
			this.ruleSetRules = new ArrayList<RuleSetRuleBean>();
		ruleSetRuleBean.setRuleSetBean(this);
		ruleSetRules.add(ruleSetRuleBean);
	}

	@Transient
	public void addRuleSetRuleForDisplay(RuleSetRuleBean ruleSetRuleBean) {
		if (this.ruleSetRules == null)
			this.ruleSetRules = new ArrayList<RuleSetRuleBean>();
		// ruleSetRuleBean.setRuleSetBean(this); Need to comment this so no bi direction established
		ruleSetRules.add(ruleSetRuleBean);
	}

	@Transient
	public void addRuleSetRule(RuleBean ruleBean) {
		if (this.ruleSetRules == null)
			this.ruleSetRules = new ArrayList<RuleSetRuleBean>();
		RuleSetRuleBean ruleSetRuleBean = new RuleSetRuleBean();
		ruleSetRuleBean.setRuleBean(ruleBean);
		ruleSetRuleBean.setRuleSetBean(this);
	}

	@Transient
	public void addRuleSetRules(List<RuleSetRuleBean> ruleSetRuleBeans) {
		if (this.ruleSetRules == null)
			this.ruleSetRules = new ArrayList<RuleSetRuleBean>();

		for (RuleSetRuleBean ruleSetRuleBean : ruleSetRuleBeans) {
			ruleSetRuleBean.setRuleSetBean(this);
		}
		this.ruleSetRules.addAll(ruleSetRuleBeans);
	}

	@Transient
	public void addExpression(ExpressionBean expressionBean) {
		if (this.expressions == null)
			this.expressions = new ArrayList<ExpressionBean>();
		expressions.add(expressionBean);
	}

	@Transient
	public String getStudyEventDefinitionName() {
		return getStudyEventDefinition() != null ? getStudyEventDefinition().getName() : "All";
	}

	@Transient
	public String getStudyEventDefinitionNameWithOID() {
		return getStudyEventDefinition() == null ? "" : getStudyEventDefinitionName() + " ("
				+ getStudyEventDefinition().getOid() + ")";
	}

	@Transient
	public String getCrfWithVersionName() {
		String crfVersionName = getCrfVersion() != null ? " - " + getCrfVersion().getName() : "";
		String crfName = getCrf() != null ? getCrf().getName() : "";
		return crfName + crfVersionName;
	}

	@Transient
	public String getCrfName() {
		return getCrf() != null ? getCrf().getName() : "";
	}

	@Transient
	public String getCrfVersionName() {
		return getCrfVersion() != null ? getCrfVersion().getName() : "All";
	}

	@Transient
	public String getCrfWithVersionNameWithOid() {
		String oid = getCrfVersion() != null ? getCrfVersion().getOid() : getCrf() != null ? getCrf().getOid() : "";
		return getCrfWithVersionName() + (oid != null && !oid.equals("") ? " (" + oid + ")" : "");
	}

	@Transient
	public int getRuleSetRuleSize() {
		return this.ruleSetRules.size();
	}

	@Transient
	public String getGroupLabel() {
		return getItemGroup() != null ? getItemGroup().getName() : "";
	}

	@Transient
	public String getItemName() {
		return getItem().getName();
	}

	@Transient
	public String getGroupLabelWithOid() {
		return getItemGroup() == null ? "" : getGroupLabel() + " (" + getItemGroup().getOid() + ")";
	}

	@Transient
	public String getItemNameWithOid() {
		return getItemName() + " (" + getItem().getOid() + ")";
	}

	// Getters & Setters
	@Transient
	public StudyEventDefinitionBean getStudyEventDefinition() {
		return studyEventDefinition;
	}

	public void setStudyEventDefinition(StudyEventDefinitionBean studyEventDefinition) {
		if (studyEventDefinition != null && studyEventDefinition.getId() > 0) {
			this.studyEventDefinitionId = studyEventDefinition.getId();
		}
		this.studyEventDefinition = studyEventDefinition;
	}

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@Fetch(value = FetchMode.SUBSELECT)
	@JoinColumn(name = "rule_set_id", nullable = false)
	public List<RuleSetRuleBean> getRuleSetRules() {
		return ruleSetRules;
	}

	public void setRuleSetRules(List<RuleSetRuleBean> ruleSetRuleAssignment) {
		this.ruleSetRules = ruleSetRuleAssignment;
	}

	@Transient
	public StudyBean getStudy() {
		return study;
	}

	public void setStudy(StudyBean study) {
		if (study.getId() > 0) {
			this.studyId = study.getId();
		}
		this.study = study;
	}

	@Transient
	public ItemGroupBean getItemGroup() {
		return itemGroup;
	}

	public void setItemGroup(ItemGroupBean itemGroup) {
		if (itemGroup != null && itemGroup.getId() > 0) {
			this.itemGroupId = itemGroup.getId();
		}
		this.itemGroup = itemGroup;
	}

	@Transient
	public ItemBean getItem() {
		return item;
	}

	public void setItem(ItemBean item) {
		if (item != null && item.getId() > 0) {
			this.itemId = item.getId();
		}
		this.item = item;
	}

	@Transient
	public CRFBean getCrf() {
		return crf;
	}

	public void setCrf(CRFBean crf) {
		if (crf != null && crf.getId() > 0) {
			this.crfId = crf.getId();
		}
		this.crf = crf;
	}

	@Transient
	public CRFVersionBean getCrfVersion() {
		return crfVersion;
	}

	public void setCrfVersion(CRFVersionBean crfVersion) {
		if (crfVersion != null && crfVersion.getId() > 0) {
			this.crfVersionId = crfVersion.getId();
		}
		this.crfVersion = crfVersion;
	}

	@Transient
	public List<ExpressionBean> getExpressions() {
		return expressions;
	}

	public void setExpressions(List<ExpressionBean> expressions) {
		this.expressions = expressions;
	}

	@Transient
	public ExpressionBean getTarget() {
		if (this.target == null) {
			target = originalTarget;
		}
		return target;
	}

	public void setTarget(ExpressionBean target) {
		this.target = target;
	}

	/**
	 * @return originalTarget
	 */
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "rule_expression_id")
	public ExpressionBean getOriginalTarget() {
		return originalTarget;
	}

	/**
	 * @param originalTarget
	 */
	public void setOriginalTarget(ExpressionBean originalTarget) {
		this.originalTarget = originalTarget;
	}

	/**
	 * @return the studyEventDefinitionId
	 */
	public Integer getStudyEventDefinitionId() {
		return studyEventDefinitionId;
	}

	/**
	 * @param studyEventDefinitionId
	 *            the studyEventDefinitionId to set
	 */
	public void setStudyEventDefinitionId(Integer studyEventDefinitionId) {
		this.studyEventDefinitionId = studyEventDefinitionId;
	}

	/**
	 * @return the studyId
	 */
	public Integer getStudyId() {
		return studyId;
	}

	/**
	 * @param studyId
	 *            the studyId to set
	 */
	public void setStudyId(Integer studyId) {
		this.studyId = studyId;
	}

	/**
	 * @return the crfId
	 */
	public Integer getCrfId() {
		return crfId;
	}

	/**
	 * @param crfId
	 *            the crfId to set
	 */
	public void setCrfId(Integer crfId) {
		this.crfId = crfId;
	}

	/**
	 * @return the crfVersionId
	 */
	public Integer getCrfVersionId() {
		return crfVersionId;
	}

	/**
	 * @param crfVersionId
	 *            the crfVersionId to set
	 */
	public void setCrfVersionId(Integer crfVersionId) {
		this.crfVersionId = crfVersionId;
	}

	/**
	 * @return the itemId
	 */
	public Integer getItemId() {
		return itemId;
	}

	/**
	 * @param itemId
	 */
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	/**
	 * @return the itemGroupId
	 */
	public Integer getItemGroupId() {
		return itemGroupId;
	}

	/**
	 * @param itemGroupId
	 */
	public void setItemGroupId(Integer itemGroupId) {
		this.itemGroupId = itemGroupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (target == null ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RuleSetBean other = (RuleSetBean) obj;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}
