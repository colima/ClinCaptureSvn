package com.clinovo.service;

import com.clinovo.model.CRFMask;

import java.util.List;

/**
 * CRF Masking service interface.
 */
public interface CRFMaskingService {

	/**
	 * Find all CRF Masks.
	 *
	 * @return List of CRF Masks
	 */
	List<CRFMask> findAll();

	/**
	 * Find all CRF Masks by user ID.
	 *
	 * @param id User Account ID
	 * @return List of CRF Masks
	 */
	List<CRFMask> findByUserId(int id);

	/**
	 * Save CRF Mask.
	 *
	 * @param mask CRF Mask to be saved
	 */
	void saveCRFMask(CRFMask mask);

	/**
	 * Find CRF Mask by user ID, site ID, crf ID.
	 *
	 * @param userId int
	 * @param siteId int
	 * @param crfId  int
	 * @return CRF Mask
	 */
	CRFMask findByUserIdSiteIdAndCRFId(int userId, int siteId, int crfId);

	/**
	 * Delete CRF Mask.
	 *
	 * @param mask CRF Mask to be deleted
	 */
	void delete(CRFMask mask);
}



