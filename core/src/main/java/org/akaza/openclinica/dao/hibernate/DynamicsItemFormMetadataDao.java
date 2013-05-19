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

package org.akaza.openclinica.dao.hibernate;

import java.util.List;

import org.akaza.openclinica.bean.submit.EventCRFBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.dao.core.CoreResources;
import org.akaza.openclinica.domain.crfdata.DynamicsItemFormMetadataBean;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Doug Rodrigues (douglas.rodrigues@openclinica.com)
 * 
 */
public class DynamicsItemFormMetadataDao extends AbstractDomainDao<DynamicsItemFormMetadataBean> {

	protected static final Logger LOG = LoggerFactory.getLogger(DynamicsItemFormMetadataDao.class);

	@Override
	public Class<DynamicsItemFormMetadataBean> domainClass() {
		return DynamicsItemFormMetadataBean.class;
	}

	public DynamicsItemFormMetadataBean findByMetadataBean(ItemFormMetadataBean metadataBean,
			EventCRFBean eventCrfBean, ItemDataBean itemDataBean) {

		String query = "from DynamicsItemFormMetadataBean metadata where metadata.itemFormMetadataId = :id and "
				+ "metadata.itemId = :item_id and metadata.eventCrfId = :event_crf_id and "
				+ "metadata.itemDataId = :item_data_id ";
		Query q = getCurrentSession().createQuery(query);
		q.setInteger("id", new Integer(metadataBean.getId()));
		q.setInteger("item_id", new Integer(metadataBean.getItemId()));
		q.setInteger("event_crf_id", new Integer(eventCrfBean.getId()));
		q.setInteger("item_data_id", new Integer(itemDataBean.getId()));
		return (DynamicsItemFormMetadataBean) q.uniqueResult();
	}

	public DynamicsItemFormMetadataBean findByItemDataBean(ItemDataBean itemDataBean) {
		String query = "from " + getDomainClassName() + " metadata where metadata.itemDataId = :item_data_id ";
		Query q = getCurrentSession().createQuery(query);

		q.setInteger("item_data_id", new Integer(itemDataBean.getId()));
		return (DynamicsItemFormMetadataBean) q.uniqueResult();
	}

	/**
	 * The reason we overwrite this method is to make it non transactional. and mainly to make saves faster.
	 */
	@Override
	public DynamicsItemFormMetadataBean saveOrUpdate(DynamicsItemFormMetadataBean domainObject) {

		try {
			LOG.trace("******>>>>>>>Current thread Running>>>>>>>>>>>>" + Thread.currentThread() + "DomainObj="
					+ domainObject);
			getCurrentSession().beginTransaction();
			getCurrentSession().saveOrUpdate(domainObject);

			LOG.trace("******>>>>>>>Current thread Running>>>>>>>>>>>>flushing >>>>>>>>>" + Thread.currentThread()
					+ "/n crfVersionId=" + domainObject.getCrfVersionId() + "eventCrfId:"
					+ domainObject.getEventCrfId() + "itemFormMetadataId:" + domainObject.getItemFormMetadataId()
					+ "Id:" + domainObject.getId() + "Item Data Id:" + domainObject.getItemDataId()
					+ "ItemFormMetaDataId:" + domainObject.getItemFormMetadataId());
			LOG.trace("******>>>>>>>Current thread Running>>>>>>>>>>>>flushing >>>>>>>>>" + Thread.currentThread()
					+ "DomainObj=" + getCurrentSession().getEntityName(domainObject));
			getCurrentSession().getTransaction().commit();

			getCurrentSession().flush();
			getCurrentSession().evict(domainObject);
		} catch (Exception e) {
			LOG.trace("******>>>>>>>Current thread Running>>>> " + Thread.currentThread());
			e.printStackTrace();
			throw new RuntimeException("Error saving DynamicsItemFormMetadataBean", e);
		}

		return domainObject;
	}

	public List<Integer> findItemIdsForAGroupInSection(int groupId, int sectionId, int crfVersionId, int eventCrfId) {
		String oracle = "select distinct ditem.item_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item=0 and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )";

		String postgres = "select distinct ditem.item_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item='false' and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )";

		return queryForIDs(oracle, postgres, groupId, sectionId, eventCrfId, crfVersionId);
	}

	public List<Integer> findShowItemIdsForAGroupInSection(int groupId, int sectionId, int crfVersionId, int eventCrfId) {
		String oracle = "select distinct ditem.item_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item=0 and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )" + " and ditem.show_item=1";
		String postgres = "select distinct ditem.item_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item='false' and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )" + " and ditem.show_item='true'";
		return queryForIDs(oracle, postgres, groupId, sectionId, eventCrfId, crfVersionId);
	}

	public List<Integer> findShowItemDataIdsForAGroupInSection(int groupId, int sectionId, int crfVersionId,
			int eventCrfId) {
		String oracle = "select ditem.item_data_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item=0 and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )" + " and ditem.show_item=1";

		String postgres = "select ditem.item_data_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item='false' and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )" + " and ditem.show_item='true'";

		return queryForIDs(oracle, postgres, groupId, sectionId, eventCrfId, crfVersionId);
	}

	public List<Integer> findHideItemDataIdsForAGroupInSection(int groupId, int sectionId, int crfVersionId,
			int eventCrfId) {
		String oracle = "select ditem.item_data_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item=0 and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )" + " and ditem.show_item=0";

		String postgres = "select ditem.item_data_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in (select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select distinct igm.item_id from item_group_metadata igm"
				+ " where igm.item_group_id = :groupId and igm.crf_version_id = :crfVersionId and igm.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item='false' and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId))"
				+ " and (idata.status_id != 5 and idata.status_id != 7) )" + " and ditem.show_item='false'";

		return queryForIDs(oracle, postgres, groupId, sectionId, eventCrfId, crfVersionId);
	}

	public List<Integer> findShowItemDataIdsInSection(int sectionId, int crfVersionId, int eventCrfId) {
		String oracle = "select ditem.item_data_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in ( select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item=0 and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId)" + " and (idata.status_id != 5 and idata.status_id != 7) )"
				+ " and ditem.show_item=1";

		String postgres = "select ditem.item_data_id from dyn_item_form_metadata ditem"
				+ " where ditem.item_data_id in ( select idata.item_data_id from item_data idata"
				+ " where idata.event_crf_id = :eventCrfId and idata.item_id in ("
				+ " select ifm.item_id from item_form_metadata ifm where ifm.show_item='false' and ifm.section_id = :sectionId"
				+ " and ifm.crf_version_id = :crfVersionId)" + " and (idata.status_id != 5 and idata.status_id != 7) )"
				+ " and ditem.show_item='true'";

		return queryForIDs(oracle, postgres, null, sectionId, eventCrfId, crfVersionId);
	}

	public Boolean hasShowingInSection(int sectionId, int crfVersionId, int eventCrfId) {
		String oracle = "select di.item_id from dyn_item_form_metadata di where di.item_data_id in ("
				+ " select ida.item_data_id from item_data ida where ida.event_crf_id = :eventCrfId and ida.item_id in"
				+ "       (select ifm.item_id from item_form_metadata ifm where ifm.section_id = :sectionId and ifm.crf_version_id = :crfVersionId"
				+ "          and ifm.item_id not in  (select distinct igm.item_id from item_group_metadata igm where igm.crf_version_id = :crfVersionId"
				+ "          and igm.show_group = 0"
				+ "          and igm.item_id in (select im.item_id from item_form_metadata im where im.section_id = :sectionId and im.crf_version_id = :crfVersionId))"
				+ "        )and (ida.status_id != 5 and ida.status_id != 7) ) and di.show_item = 1 and rownum = 1";
		String postgres = "select di.item_id from dyn_item_form_metadata di where di.item_data_id in ("
				+ " select ida.item_data_id from item_data ida where ida.event_crf_id = :eventCrfId and ida.item_id in"
				+ "       (select ifm.item_id from item_form_metadata ifm where ifm.section_id = :sectionId and ifm.crf_version_id = :crfVersionId"
				+ "          and ifm.item_id not in  (select distinct igm.item_id from item_group_metadata igm where igm.crf_version_id = :crfVersionId"
				+ "          and igm.show_group = 'false'"
				+ "          and igm.item_id in (select im.item_id from item_form_metadata im where im.section_id = :sectionId and im.crf_version_id = :crfVersionId))"
				+ "        )and (ida.status_id != 5 and ida.status_id != 7) ) and di.show_item = 'true' limit 1";

		return CollectionUtils.isNotEmpty(queryForIDs(oracle, postgres, null, sectionId, eventCrfId, crfVersionId));
	}

	/**
	 * Executes a SQL query to retrieve a list of IDs
	 * 
	 * @param oracleQuery
	 *            The Oracle version of the query
	 * @param postgresQuery
	 *            The Postgres version of the query
	 * @param groupId
	 * @param sectionId
	 * @param eventCrfId
	 * @param crfVersionId
	 * @return
	 */
	protected List<Integer> queryForIDs(String oracleQuery, String postgresQuery, Integer groupId, Integer sectionId,
			Integer eventCrfId, Integer crfVersionId) {
		String query = "oracle".equalsIgnoreCase(CoreResources.getDBName()) ? oracleQuery : postgresQuery;
		Query q = getCurrentSession().createSQLQuery(query);
		if (groupId != null) {
			q.setInteger("groupId", groupId);
		}
		if (sectionId != null) {
			q.setInteger("sectionId", sectionId);
		}
		if (eventCrfId != null) {
			q.setInteger("eventCrfId", eventCrfId);
		}
		if (crfVersionId != null) {
			q.setInteger("crfVersionId", crfVersionId);
		}
		return HibernateUtil.queryIDsList(q);
	}

}
