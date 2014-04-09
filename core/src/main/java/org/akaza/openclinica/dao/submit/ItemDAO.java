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
package org.akaza.openclinica.dao.submit;

import org.akaza.openclinica.bean.admin.CRFBean;
import org.akaza.openclinica.bean.core.EntityBean;
import org.akaza.openclinica.bean.core.ItemDataType;
import org.akaza.openclinica.bean.core.Status;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.bean.submit.ItemFormMetadataBean;
import org.akaza.openclinica.core.util.ItemGroupCrvVersionUtil;
import org.akaza.openclinica.dao.core.AuditableEntityDAO;
import org.akaza.openclinica.dao.core.DAODigester;
import org.akaza.openclinica.dao.core.PreparedStatementFactory;
import org.akaza.openclinica.dao.core.SQLFactory;
import org.akaza.openclinica.dao.core.TypeNames;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ItemDAO extends AuditableEntityDAO {
	// private DAODigester digester;
	public ItemDAO(DataSource ds) {
		super(ds);
	}

	public ItemDAO(DataSource ds, DAODigester digester) {
		super(ds);
		this.digester = digester;
	}

	// This constructor sets up the Locale for JUnit tests; see the locale
	// member variable in EntityDAO, and its initializeI18nStrings() method
	public ItemDAO(DataSource ds, DAODigester digester, Locale locale) {

		this(ds, digester);
		this.locale = locale;
	}

	@Override
	protected void setDigesterName() {
		digesterName = SQLFactory.getInstance().DAO_ITEM;
	}

	@Override
	public void setTypesExpected() {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		this.setTypeExpected(2, TypeNames.STRING);
		this.setTypeExpected(3, TypeNames.STRING);
		this.setTypeExpected(4, TypeNames.STRING);
		this.setTypeExpected(5, TypeNames.BOOL);// phi status
		this.setTypeExpected(6, TypeNames.INT);// data type id
		this.setTypeExpected(7, TypeNames.INT);// reference type id
		this.setTypeExpected(8, TypeNames.INT);// status id
		this.setTypeExpected(9, TypeNames.INT);// owner id
		this.setTypeExpected(10, TypeNames.DATE);// created
		this.setTypeExpected(11, TypeNames.DATE);// updated
		this.setTypeExpected(12, TypeNames.INT);// update id
		this.setTypeExpected(13, TypeNames.STRING);// oc_oid
	}

	public EntityBean update(EntityBean eb) {
		ItemBean ib = (ItemBean) eb;
		HashMap variables = new HashMap();
		variables.put(1, ib.getName());
		variables.put(2, ib.getDescription());
		variables.put(3, ib.getUnits());
		variables.put(4, ib.isPhiStatus());
		variables.put(5, ib.getItemDataTypeId());
		variables.put(6, ib.getItemReferenceTypeId());
		variables.put(7, ib.getStatus().getId());
		variables.put(8, ib.getUpdaterId());
		variables.put(9, ib.getId());
		this.execute(digester.getQuery("update"), variables);
		return eb;
	}

	public EntityBean create(EntityBean eb) {
		ItemBean ib = (ItemBean) eb;
		// per the create sql statement
		HashMap variables = new HashMap();
		variables.put(1, ib.getName());
		variables.put(2, ib.getDescription());
		variables.put(3, ib.getUnits());
		variables.put(4, ib.isPhiStatus());
		variables.put(5, ib.getItemDataTypeId());
		variables.put(6, ib.getItemReferenceTypeId());
		variables.put(7, ib.getStatus().getId());
		variables.put(8, ib.getOwnerId());
		this.execute(digester.getQuery("create"), variables);
		// set the id here????
		return eb;
	}

	private String getOid(ItemBean itemBean, String crfName, String itemLabel) {

		String oid;
		try {
			oid = itemBean.getOid() != null ? itemBean.getOid() : itemBean.getOidGenerator(ds).generateOid(crfName,
					itemLabel);
			return oid;
		} catch (Exception e) {
			throw new RuntimeException("CANNOT GENERATE OID");
		}
	}

	public Integer getCountofActiveItems() {
		setTypesExpected();

		String sql = digester.getQuery("getCountofItems");

		ArrayList rows = this.select(sql);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			return (Integer) ((HashMap) it.next()).get("count");
		} else {
			return null;
		}
	}

	public String getValidOid(ItemBean itemBean, String crfName, String itemLabel, ArrayList<String> oidList) {

		String oid = getOid(itemBean, crfName, itemLabel);
		logger.info(oid);
		String oidPreRandomization = oid;
		while (findByOid(oid).size() > 0 || oidList.contains(oid)) {
			oid = itemBean.getOidGenerator(ds).randomizeOid(oidPreRandomization);
		}
		return oid;

	}

	@SuppressWarnings("deprecation")
	public Object getEntityFromHashMap(HashMap hm) {
		ItemBean eb = new ItemBean();
		// below inserted to find out a class cast exception, tbh
		Date dateCreated = (Date) hm.get("date_created");
		Date dateUpdated = (Date) hm.get("date_updated");
		Integer statusId = (Integer) hm.get("status_id");
		Integer ownerId = (Integer) hm.get("owner_id");
		Integer updateId = (Integer) hm.get("update_id");

		eb.setCreatedDate(dateCreated);
		eb.setUpdatedDate(dateUpdated);
		eb.setStatus(Status.get(statusId));
		eb.setOwnerId(ownerId);
		eb.setUpdaterId(updateId);
		eb.setName((String) hm.get("name"));
		eb.setId((Integer) hm.get("item_id"));
		eb.setDescription((String) hm.get("description"));
		eb.setUnits((String) hm.get("units"));
		eb.setPhiStatus((Boolean) hm.get("phi_status"));
		eb.setItemDataTypeId((Integer) hm.get("item_data_type_id"));
		eb.setItemReferenceTypeId((Integer) hm.get("item_reference_type_id"));
		eb.setDataType(ItemDataType.get(eb.getItemDataTypeId()));
		eb.setOid((String) hm.get("oc_oid"));
		// the rest should be all set
		return eb;
	}

	public List<ItemBean> findByOid(String oid) {
		this.setTypesExpected();
		HashMap<Integer, String> variables = new HashMap<Integer, String>();
		variables.put(1, oid);
		List listofMaps = this.select(digester.getQuery("findItemByOid"), variables);

		List<ItemBean> beanList = new ArrayList<ItemBean>();
		ItemBean bean;
		for (Object map : listofMaps) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	public Collection findAll() {
		this.setTypesExpected();
		ArrayList alist = this.select(digester.getQuery("findAll"));
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			ItemBean eb = (ItemBean) this.getEntityFromHashMap((HashMap) anAlist);
			al.add(eb);
		}
		return al;
	}

	public Collection findAll(String strOrderByColumn, boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public ArrayList findAllParentsBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllParentsBySectionId", variables);
	}

	public ArrayList findAllNonRepeatingParentsBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllNonRepeatingParentsBySectionId", variables);
	}

	public ArrayList findAllBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllBySectionId", variables);
	}

	public ArrayList findAllUngroupedParentsBySectionId(int sectionId, int crfVersionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);
		variables.put(2, crfVersionId);

		return this.executeFindAllQuery("findAllUngroupedParentsBySectionId", variables);
	}

	public ArrayList findAllItemsByVersionId(int versionId) {
		HashMap variables = new HashMap();
		variables.put(1, versionId);

		return this.executeFindAllQuery("findAllItemsByVersionId", variables);
	}

	public ArrayList findAllVersionsByItemId(int itemId) {
		HashMap variables = new HashMap();
		variables.put(1, itemId);
		String sql = digester.getQuery("findAllVersionsByItemId");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			Integer versionId = (Integer) ((HashMap) anAlist).get("crf_version_id");
			al.add(versionId);
		}
		return al;

	}

	public List<ItemBean> findAllItemsByGroupId(int id, int crfVersionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		variables.put(2, crfVersionId);
		String sql = digester.getQuery("findAllItemsByGroupId");
		List listofMaps = this.select(sql, variables);
		List<ItemBean> beanList = new ArrayList<ItemBean>();
		ItemBean bean;
		for (Object map : listofMaps) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	public List<ItemBean> findAllItemsByGroupIdForPrint(int id, int crfVersionId, int sectionId) {
		this.setTypesExpected();
		HashMap<Integer, Integer> variables = new HashMap<Integer, Integer>();
		variables.put(1, id);
		variables.put(2, crfVersionId);
		variables.put(3, sectionId);
		String sql = digester.getQuery("findAllItemsByGroupIdForPrint");
		List listofMaps = this.select(sql, variables);
		List<ItemBean> beanList = new ArrayList<ItemBean>();
		ItemBean bean;
		for (Object map : listofMaps) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) map);
			beanList.add(bean);
		}
		return beanList;
	}

	public ItemBean findItemByGroupIdandItemOid(int id, String itemOid) {
		ItemBean bean;
		this.setTypesExpected();
		HashMap variables = new HashMap();
		variables.put(1, id);
		variables.put(2, itemOid);
		String sql = digester.getQuery("findItemByGroupIdandItemOid");

		ArrayList rows = this.select(sql, variables);
		Iterator it = rows.iterator();

		if (it.hasNext()) {
			bean = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
			return bean;
		} else {
			return null;
		}
	}

	public ArrayList findAllActiveByCRF(CRFBean crf) {
		HashMap variables = new HashMap();
		this.setTypesExpected();
		this.setTypeExpected(14, TypeNames.INT);// crf_version_id
		this.setTypeExpected(15, TypeNames.STRING);// version name
		variables.put(1, crf.getId());
		String sql = digester.getQuery("findAllActiveByCRF");
		ArrayList alist = this.select(sql, variables);
		ArrayList al = new ArrayList();
		for (Object anAlist : alist) {
			HashMap hm = (HashMap) anAlist;
			ItemBean eb = (ItemBean) this.getEntityFromHashMap(hm);
			Integer versionId = (Integer) hm.get("crf_version_id");
			String versionName = (String) hm.get("cvname");
			ItemFormMetadataBean imf = new ItemFormMetadataBean();
			imf.setCrfVersionName(versionName);
			imf.setCrfVersionId(versionId);
			eb.setItemMeta(imf);
			al.add(eb);
		}
		return al;

	}

	public EntityBean findByPK(int ID) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, ID);

		String sql = digester.getQuery("findByPK");

		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	public EntityBean findByName(String name) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);

		String sql = digester.getQuery("findByName");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	public EntityBean findByNameAndCRFId(String name, int crfId) {
		ItemBean eb = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, name);
		variables.put(2, crfId);

		String sql = digester.getQuery("findByNameAndCRFId");
		ArrayList alist = this.select(sql, variables);
		Iterator it = alist.iterator();

		if (it.hasNext()) {
			eb = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}
		return eb;
	}

	public EntityBean findByNameAndCRFVersionId(String itemName, int crfVersionId) {

		ItemBean itemBean = new ItemBean();
		this.setTypesExpected();

		HashMap variables = new HashMap();
		variables.put(1, itemName);
		variables.put(2, crfVersionId);

		String sql = digester.getQuery("findByNameAndCRFVersionId");
		ArrayList items = this.select(sql, variables);

		Iterator it = items.iterator();
		if (it.hasNext()) {
			itemBean = (ItemBean) this.getEntityFromHashMap((HashMap) it.next());
		}

		return itemBean;

	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType, String strOrderByColumn,
			boolean blnAscendingSort, String strSearchPhrase) {
		return new ArrayList();
	}

	public Collection findAllByPermission(Object objCurrentUser, int intActionType) {
		return new ArrayList();
	}

	/**
	 * Finds the children of an item in a given CRF Version, sorted by columnNumber in ascending order.
	 * 
	 * @param parentId
	 *            The id of the children's parent.
	 * @param crfVersionId
	 *            The id of the event CRF in which the children belong to this parent.
	 * @return An array of ItemBeans, where each ItemBean represents a child of the parent and the array is sorted by
	 *         columnNumber in ascending order.
	 */
	public ArrayList findAllByParentIdAndCRFVersionId(int parentId, int crfVersionId) {
		HashMap variables = new HashMap();
		variables.put(1, parentId);
		variables.put(2, crfVersionId);

		return this.executeFindAllQuery("findAllByParentIdAndCRFVersionId", variables);
	}

	public int findAllRequiredByCRFVersionId(int crfVersionId) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.INT);
		int answer = 0;

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);
		String sql = digester.getQuery("findAllRequiredByCRFVersionId");

		ArrayList rows = select(sql, variables);

		if (rows.size() > 0) {
			HashMap row = (HashMap) rows.get(0);
			answer = (Integer) row.get("number");
		}

		return answer;
	}

	public ArrayList findAllRequiredBySectionId(int sectionId) {
		HashMap variables = new HashMap();
		variables.put(1, sectionId);

		return this.executeFindAllQuery("findAllRequiredBySectionId", variables);
	}

	@Override
	public ArrayList select(String query, Map variables) {
		clearSignals();

		ArrayList results = new ArrayList();
		Object key;
		ResultSet rs = null;
		Connection con = null;
		PreparedStatementFactory psf = new PreparedStatementFactory(variables);
		PreparedStatement ps = null;

		try {
			con = ds.getConnection();
			if (con.isClosed()) {
				if (logger.isWarnEnabled())
					logger.warn("Connection is closed: GenericDAO.select!");
				throw new SQLException();
			}

			ps = con.prepareStatement(query);

			ps = psf.generate(ps);// enter variables here!
			logger.info("query is..." + ps.toString());
			key = ps.toString();
			if ((results = (ArrayList) cache.get(key)) == null) {
				rs = ps.executeQuery();
				results = this.processResultRows(rs);
				if (results != null) {
					cache.put(key, results);
				}
			}

			if (logger.isInfoEnabled()) {
				logger.info("Executing dynamic query, EntityDAO.select:query " + query);
			}
			signalSuccess();

		} catch (SQLException sqle) {
			signalFailure(sqle);
			if (logger.isWarnEnabled()) {
				logger.warn("Exception while executing dynamic query, GenericDAO.select: " + query + ":message: "
						+ sqle.getMessage());
				sqle.printStackTrace();
			}
		} finally {
			this.closeIfNecessary(con, rs, ps);
		}
		return results;

	}

	public ArrayList<ItemBean> findAllWithItemDataByCRFVersionId(int crfVersionId, int eventCRFId) {
		this.unsetTypeExpected();

		this.setTypeExpected(1, TypeNames.STRING);// (item)name
		this.setTypeExpected(2, TypeNames.INT);// ordinal
		this.setTypeExpected(3, TypeNames.STRING);// oc_oid
		this.setTypeExpected(4, TypeNames.INT);// item_data_id
		this.setTypeExpected(5, TypeNames.INT);// item_id
		this.setTypeExpected(6, TypeNames.STRING);// (item)value

		ArrayList<ItemBean> answer = new ArrayList<ItemBean>();

		HashMap variables = new HashMap();
		variables.put(1, crfVersionId);
		variables.put(2, eventCRFId);

		String sql = digester.getQuery("findAllWithItemDataByCRFVersionId");

		ArrayList rows = super.select(sql, variables);
		Iterator it = rows.iterator();
		int cur_item_id = 0;
		ItemBean item_bean = null;
		ItemDataBean item_data_bean;
		while (it.hasNext()) {
			HashMap row = (HashMap) it.next();
			Integer id = (Integer) row.get("item_id");
			if (cur_item_id != id) {
				item_bean = new ItemBean();
				answer.add(item_bean);
				cur_item_id = id;
				item_bean.setId(cur_item_id);
				item_bean.setName((String) row.get("name"));
				item_bean.setOid((String) row.get("oc_oid"));

			}
			if (item_bean != null) {
				item_data_bean = new ItemDataBean();
				item_data_bean.setValue((String) row.get("value"));
				item_data_bean.setOrdinal((Integer) row.get("ordinal"));
				item_data_bean.setId((Integer) row.get("item_data_id"));
				item_data_bean.setItemId(cur_item_id);
				item_bean.addItemDataElement(item_data_bean);
			}

		}

		return answer;
	}

	public ArrayList<ItemGroupCrvVersionUtil> findAllWithItemGroupCRFVersionMetadataByCRFId(String crfName) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);// (crf)name
		this.setTypeExpected(2, TypeNames.STRING);// (crf)name
		this.setTypeExpected(3, TypeNames.STRING);// (crf)name
		this.setTypeExpected(4, TypeNames.STRING);// (crf)name
		this.setTypeExpected(5, TypeNames.INT);// (crf)name

		HashMap variables = new HashMap();
		variables.put(1, crfName);

		String sql = digester.getQuery("findAllWithItemGroupCRFVersionMetadataByCRFId");
		ArrayList rows = this.select(sql, variables);
		ItemGroupCrvVersionUtil item;
		ArrayList<ItemGroupCrvVersionUtil> item_group_crfversion_info = new ArrayList<ItemGroupCrvVersionUtil>();
		for (Object row1 : rows) {
			HashMap row = (HashMap) row1;
			item = new ItemGroupCrvVersionUtil();
			item.setItemName((String) row.get("item_name"));
			item.setCrfVersionName((String) row.get("version_name"));
			item.setCrfVersionStatus((Integer) row.get("v_status"));
			item.setGroupName((String) row.get("group_name"));
			item.setGroupOID((String) row.get("group_oid"));
			item_group_crfversion_info.add(item);

		}
		return item_group_crfversion_info;
	}

	public ArrayList<ItemGroupCrvVersionUtil> findAllWithItemDetailsGroupCRFVersionMetadataByCRFId(String crfName) {
		this.unsetTypeExpected();
		this.setTypeExpected(1, TypeNames.STRING);// (crf)name
		this.setTypeExpected(2, TypeNames.STRING);// (crf)description
		this.setTypeExpected(3, TypeNames.INT);// (crf)item_id
		this.setTypeExpected(4, TypeNames.INT);// (crf)data_type
		this.setTypeExpected(5, TypeNames.STRING);// (crf)item_oid
		this.setTypeExpected(6, TypeNames.STRING);// (crf)group_name
		this.setTypeExpected(7, TypeNames.STRING);// (crf)group_oid
		this.setTypeExpected(8, TypeNames.STRING);// (crf)version_name
		this.setTypeExpected(9, TypeNames.INT);// (crf)v_status

		HashMap variables = new HashMap();
		variables.put(1, crfName);

		String sql = digester.getQuery("findAllWithItemDetailsGroupCRFVersionMetadataByCRFId");
		ArrayList rows = this.select(sql, variables);
		ItemGroupCrvVersionUtil item;
		ItemDataType itemDT;
		ArrayList<ItemGroupCrvVersionUtil> item_group_crfversion_info = new ArrayList<ItemGroupCrvVersionUtil>();
		for (Object row1 : rows) {
			HashMap row = (HashMap) row1;
			item = new ItemGroupCrvVersionUtil();
			item.setItemName((String) row.get("item_name"));
			item.setCrfVersionName((String) row.get("version_name"));
			item.setCrfVersionStatus((Integer) row.get("v_status"));
			item.setGroupName((String) row.get("group_name"));
			item.setGroupOID((String) row.get("group_oid"));
			this.setTypeExpected(2, TypeNames.STRING);// (crf)
			this.setTypeExpected(3, TypeNames.INT);// (crf)item_id
			this.setTypeExpected(4, TypeNames.INT);// (crf)data_type
			this.setTypeExpected(5, TypeNames.STRING);// (crf)item_oid

			item.setItemDescription((String) row.get("description"));
			item.setItemOID((String) row.get("item_oid"));
			itemDT = ItemDataType.get((Integer) row.get("data_type"));

			item.setItemDataType(itemDT.getDescription());
			item.setId((Integer) row.get("item_id"));
			item_group_crfversion_info.add(item);

		}
		return item_group_crfversion_info;
	}
}
