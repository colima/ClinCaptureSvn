/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 *
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer.
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clinovo.com/contact for pricing information.
 *
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use.
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.EventDefinitionCRFBean;
import org.akaza.openclinica.bean.submit.CRFVersionBean;
import org.akaza.openclinica.dao.managestudy.EventDefinitionCRFDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinovo.service.EventDefinitionCrfService;

/**
 * EventDefinitionCrfServiceImpl.
 */
@Service
@SuppressWarnings("unchecked")
public class EventDefinitionCrfServiceImpl implements EventDefinitionCrfService {

	public static final String ARRAY_TO_STRING_PATTERN = "\\]|\\[| ";

	@Autowired
	private DataSource dataSource;

	/**
	 * {@inheritDoc}
	 */
	public void updateChildEventDefinitionCrfsForNewCrfVersion(CRFVersionBean crfVersionBean, UserAccountBean updater) {
		if (crfVersionBean != null && crfVersionBean.getId() > 0) {
			EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);
			for (EventDefinitionCRFBean edcb : (List<EventDefinitionCRFBean>) eventDefinitionCrfDao
					.findAllByCRF(crfVersionBean.getCrfId())) {
				if (edcb.getParentId() > 0 && edcb.isAcceptNewCrfVersions()) {
					String versionIds = edcb.getSelectedVersionIds();
					if (versionIds != null && !versionIds.trim().isEmpty()) {
						List<String> idList = new ArrayList<String>(Arrays.asList(versionIds.trim().split(",")));
						String crfVersionIdStr = Integer.toString(crfVersionBean.getId());
						if (!idList.contains(crfVersionIdStr)) {
							idList.add(crfVersionIdStr);
							edcb.setSelectedVersionIds(idList.toString().replaceAll(ARRAY_TO_STRING_PATTERN, ""));
							edcb.setUpdatedDate(new Date());
							edcb.setUpdater(updater);
							eventDefinitionCrfDao.update(edcb);
						}
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateChildEventDefinitionCRFs(List<EventDefinitionCRFBean> childEventDefinitionCRFsToUpdate,
			Map<Integer, EventDefinitionCRFBean> parentsMap, UserAccountBean updater) {
		EventDefinitionCRFDAO eventDefinitionCrfDao = new EventDefinitionCRFDAO(dataSource);
		for (EventDefinitionCRFBean childEdc : childEventDefinitionCRFsToUpdate) {
			EventDefinitionCRFBean parentEdc = parentsMap.get(childEdc.getParentId());
			if (parentEdc != null) {
				String versionIds = childEdc.getSelectedVersionIds();
				childEdc.setDefaultVersionId(parentEdc.getDefaultVersionId());
				childEdc.setAcceptNewCrfVersions(parentEdc.isAcceptNewCrfVersions());
				if (versionIds != null && !versionIds.trim().isEmpty()) {
					List<String> idList = new ArrayList<String>(Arrays.asList(versionIds.trim().split(",")));
					String parentDefaultVersionId = Integer.toString(parentEdc.getDefaultVersionId());
					if (!idList.contains(parentDefaultVersionId)) {
						idList.add(parentDefaultVersionId);
						childEdc.setSelectedVersionIds(idList.toString().replaceAll(ARRAY_TO_STRING_PATTERN, ""));
					}
				}
				childEdc.setUpdater(updater);
				childEdc.setUpdatedDate(new Date());
				eventDefinitionCrfDao.update(childEdc);
			}
		}
	}
}