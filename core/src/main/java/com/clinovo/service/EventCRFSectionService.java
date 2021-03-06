/*******************************************************************************
 * CLINOVO RESERVES ALL RIGHTS TO THIS SOFTWARE, INCLUDING SOURCE AND DERIVED BINARY CODE. BY DOWNLOADING THIS SOFTWARE YOU AGREE TO THE FOLLOWING LICENSE:
 * 
 * Subject to the terms and conditions of this Agreement including, Clinovo grants you a non-exclusive, non-transferable, non-sublicenseable limited license without license fees to reproduce and use internally the software complete and unmodified for the sole purpose of running Programs on one computer. 
 * This license does not allow for the commercial use of this software except by IRS approved non-profit organizations; educational entities not working in joint effort with for profit business.
 * To use the license for other purposes, including for profit clinical trials, an additional paid license is required. Please contact our licensing department at http://www.clincapture.com/contact for pricing information.
 * 
 * You may not modify, decompile, or reverse engineer the software.
 * Clinovo disclaims any express or implied warranty of fitness for use. 
 * No right, title or interest in or to any trademark, service mark, logo or trade name of Clinovo or its licensors is granted under this Agreement.
 * THIS SOFTWARE IS PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND. CLINOVO FURTHER DISCLAIMS ALL WARRANTIES, EXPRESS AND IMPLIED, INCLUDING WITHOUT LIMITATION, ANY IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE OR NONINFRINGEMENT.

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO’S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.service;

import java.util.List;
import java.util.Map;

import com.clinovo.model.EventCRFSectionBean;

/**
 * Service contract specification for the DiscrepancyDescription service to the DAO.
 *
 */
public interface EventCRFSectionService {
	
	/**
	 * Retrieves EventCRFSectionBean with specified id.
	 * 
	 * @param id The id of EventCRFSectionBean.
	 * 
	 * @return EventCRFSectionBean.
	 */
	public EventCRFSectionBean findById(int id);
	
	/**
	 * Retrieves EventCRFSectionBean with specified eventCRFId and sectionId.
	 * 
	 * @param eventCRFId The id of eventCRF.
	 * 
	 * @param sectionId The id of crf section.
	 * 
	 * @return EventCRFSectionBean.
	 */
	public EventCRFSectionBean findByEventCRFIdAndSectionId(int eventCRFId, int sectionId);
	
	/**
	 * Persists a valid EventCRFSectionBean to storage.
	 * 
	 * @param eventCRFSectionBean EventCRFSectionBean to persist.
	 * 
	 * @return Persisted EventCRFSectionBean, null in case it was invalid
	 */
	public EventCRFSectionBean saveEventCRFSectionBean(EventCRFSectionBean eventCRFSectionBean);

	/**
	 * Deletes a specified EventCRFSectionBean from storage.
	 * 
	 * @param eventCRFSectionBean EventCRFSectionBean to delete.
	 */
	public void deleteEventCRFSectionBean(EventCRFSectionBean eventCRFSectionBean);
	
	/**
	 * Retrieves EventCRFSectionBeans with specified eventCRFId.
	 * 
	 * @param eventCRFId The id of eventCRF.
	 * 
	 * @return List<EventCRFSectionBean>.
	 */
	public List<EventCRFSectionBean> findAllPartiallySavedByEventCRFId(int eventCRFId);

	/**
	 * Retrieves map with EventCRFSectionBeans with specified eventCRFId.
	 * 
	 * @param list The list of EventCRFSectionBeans.
	 * 
	 * @return Map<Integer, EventCRFSectionBean>.
	 */
	public Map<Integer, EventCRFSectionBean> getSectionIdToEvCRFSectionMap(List<EventCRFSectionBean> list);
}

