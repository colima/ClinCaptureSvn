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

package org.akaza.openclinica.control.admin;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@SuppressWarnings({ "rawtypes" })
public final class SpreadsheetPreview implements Preview {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());

	public static final String ITEMS = "Items";
	public static final String SECTIONS = "Sections";

	public Map<String, Map> createCrfMetaObject(Workbook workbook) {
		if (workbook == null)
			return new HashMap<String, Map>();
		Map<String, Map> spreadSheetMap = new HashMap<String, Map>();
		Map<Integer, Map<String, String>> sections = createItemsOrSectionMap(workbook, SECTIONS);
		Map<Integer, Map<String, String>> items = createItemsOrSectionMap(workbook, ITEMS);
		Map<String, String> crfInfo = createCrfMap(workbook);
		if (sections.isEmpty() && items.isEmpty() && crfInfo.isEmpty()) {
			return spreadSheetMap;
		}
		spreadSheetMap.put("sections", sections);
		spreadSheetMap.put("items", items);
		spreadSheetMap.put("crf_info", crfInfo);
		return spreadSheetMap;
	}

	/**
	 * This method searches for a sheet named "Items" or "Sections" in an Excel Spreadsheet object, then creates a
	 * sorted Map whose members represent a row of data for each "Item" or "Section" on the sheet. This method was
	 * created primarily to get Items and section data for previewing a CRF.
	 * 
	 * @return A SortedMap implementation (TreeMap) containing row numbers, each pointing to a Map. The Maps represent
	 *         each Item or section row in a spreadsheet. The items or sections themselves are in rows 1..N. An example
	 *         data value from a Section row is: 1: {page_number=1.0, section_label=Subject Information,
	 *         section_title=SimpleSection1} Returns an empty Map if the spreadsheet does not contain any sheets named
	 *         "sections" or "items" (case insensitive).
	 * @param workbook
	 *            is an object representing a spreadsheet.
	 * @param itemsOrSection
	 *            should specify "items" or "sections" or the associated static variable, i.e. SpreadsheetPreview.ITEMS
	 */
	public Map<Integer, Map<String, String>> createItemsOrSectionMap(Workbook workbook, String itemsOrSection) {
		if (workbook == null || workbook.getNumberOfSheets() == 0) {
			return new HashMap<Integer, Map<String, String>>();
		}
		if (itemsOrSection == null || !itemsOrSection.equalsIgnoreCase(ITEMS)
				&& !itemsOrSection.equalsIgnoreCase(SECTIONS)) {
			return new HashMap<Integer, Map<String, String>>();
		}
		Sheet sheet;
		Row row;
		Cell cell;

		String[] itemHeaders = { "item_name", "description_label", "left_item_text", "units", "right_item_text",
				"section_label", "header", "subheader", "parent_item", "column_number", "page_number",
				"question_number", "response_type", "response_label", "response_options_text", "response_values",
				"data_type", "validation", "validation_error_message", "phi", "required", "code_ref" };
		String[] sectionHeaders = { "section_label", "section_title", "subtitle", "instructions", "page_number",
				"parent_section" };
		Map<String, String> rowCells = new HashMap<String, String>();
		SortedMap<Integer, Map<String, String>> allRows = new TreeMap<Integer, Map<String, String>>();
		String str;
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheet = workbook.getSheetAt(i);
			str = workbook.getSheetName(i);
			if (str.equalsIgnoreCase(itemsOrSection)) {
				for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
					String[] headers = itemsOrSection.equalsIgnoreCase(ITEMS) ? itemHeaders : sectionHeaders;
					// create a new Map to add to the allRows Map
					// rowCells has already been initialized in a higher code
					// block
					// so if j == 1 we don't have to init the new Map the first
					// time again.
					if (j > 1)
						rowCells = new HashMap<String, String>();
					row = sheet.getRow(j);
					for (int k = 0; k < headers.length; k++) {
						cell = row.getCell((short) k);
						if (headers[k].equalsIgnoreCase("left_item_text")
								|| headers[k].equalsIgnoreCase("right_item_text")
								|| headers[k].equalsIgnoreCase("header") || headers[k].equalsIgnoreCase("subheader")
								|| headers[k].equalsIgnoreCase("question_number")
								|| headers[k].equalsIgnoreCase("section_title")
								|| headers[k].equalsIgnoreCase("subtitle")
								|| headers[k].equalsIgnoreCase("instructions")
								|| headers[k].equalsIgnoreCase("code_ref")) {
							rowCells.put(headers[k], getCellValue(cell));
						} else {
							rowCells.put(headers[k], getCellValue(cell).replaceAll("<[^>]*>", ""));
						}
					}
					// item_name

					allRows.put(j, rowCells);
				}// end inner for loop
			}// end if
		}// end outer for
		return allRows;
	}

	public Map<Integer, Map<String, String>> createGroupsMap(Workbook workbook) {
		if (workbook == null || workbook.getNumberOfSheets() == 0) {
			return new HashMap<Integer, Map<String, String>>();
		}
		Sheet sheet;
		Row row;
		Cell cell;
		// static group headers for a CRF; TODO: change these so they are not
		// static and hard-coded
		String[] groupHeaders = { "group_label", "group_layout", "group_header", "group_sub_header",
				"group_repeat_number", "group_repeat_max", "group_repeat_array", "group_row_start_number" };
		Map<String, String> rowCells = new HashMap<String, String>();
		SortedMap<Integer, Map<String, String>> allRows = new TreeMap<Integer, Map<String, String>>();
		String str;
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheet = workbook.getSheetAt(i);
			str = workbook.getSheetName(i);
			if (str.equalsIgnoreCase("Groups")) {
				for (int j = 1; j < sheet.getPhysicalNumberOfRows(); j++) {
					// create a new Map to add to the allRows Map
					// rowCells has already been initialized in a higher code
					// block
					// so if j == 1 we don't have to init the new Map the first
					// time again.
					if (j > 1)
						rowCells = new HashMap<String, String>();
					row = sheet.getRow(j);
					for (int k = 0; k < groupHeaders.length; k++) {
						cell = row.getCell((short) k);
						if (groupHeaders[k].equalsIgnoreCase("group_header")) {
							rowCells.put(groupHeaders[k], getCellValue(cell).replaceAll("<[^>]*>", ""));
						}
					}

					allRows.put(j, rowCells);
				}// end inner for loop
			}// end if
		}// end outer for
		return allRows;
	}

	private String getCellValue(Cell cell) {
		if (cell == null)
			return "";
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			return Double.toString(cell.getNumericCellValue());
		case Cell.CELL_TYPE_BOOLEAN:
			return Boolean.toString(cell.getBooleanCellValue());
		case Cell.CELL_TYPE_FORMULA:
			return cell.getCellFormula();
		}
		return "";
	}

	@SuppressWarnings("unused")
	public static void main(String[] args) throws IOException {

		// Simple3.xls , Cancer_History5.xls , Can3.xls
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(new File(
				"/Users/bruceperry/work/OpenClinica-Cancer-Demo-Study/Cancer_History5.xls")));
		Workbook wb = new HSSFWorkbook(fs);
		SpreadsheetPreview prev = new SpreadsheetPreview();
	}

	/*
	 * This method searches for a sheet named "Sections" in an Excel Spreadsheet object, then creates a HashMap
	 * containing that sheet's data. The HashMap contains the sheet name as the key, and a List of cells (only the ones
	 * that contain data, not blank ones). This method was created primarly to get the section names for a CRF preview
	 * page. The Map does not contain data for any sections that have duplicate names; just one section per section
	 * name. This method does not yet validate the spreadsheet as a CRF.
	 * 
	 * @author Bruce Perry @returns A HashMap containing CRF section names as keys. Returns an empty HashMap if the
	 * spreadsheet does not contain any sheets named "Sections."
	 */
	public Map<String, String> createCrfMap(Workbook workbook) {
		if (workbook == null || workbook.getNumberOfSheets() == 0) {
			return new HashMap<String, String>();
		}
		Sheet sheet;
		Row row;
		Cell cell;
		Map<String, String> crfInfo = new HashMap<String, String>();
		String mapKey;
		String val = "";
		String str;
		String[] crfHeaders = { "crf_name", "version", "version_description", "revision_notes" };
		for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
			sheet = workbook.getSheetAt(i);
			str = workbook.getSheetName(i);
			if (str.equalsIgnoreCase("CRF")) {
				row = sheet.getRow(1);
				for (int k = 0; k < crfHeaders.length; k++) {
					// The first cell in the row contains the header CRF_NAME
					mapKey = crfHeaders[k];
					cell = row.getCell((short) k);
					if (cell != null) { // the cell does not have a blank value
						// Set the Map key to the crf header

						switch (cell.getCellType()) {
						case Cell.CELL_TYPE_STRING:
							val = cell.getStringCellValue();
							break;
						case Cell.CELL_TYPE_NUMERIC:
							val = Double.toString(cell.getNumericCellValue());
							break;
						case Cell.CELL_TYPE_BOOLEAN:
							val = Boolean.toString(cell.getBooleanCellValue());
							break;
						case Cell.CELL_TYPE_FORMULA:
							cell.getCellFormula();
							break;
						}
					}
					crfInfo.put(mapKey, val);
				}
			}// end if
		}// end outer for
		return crfInfo;
	}
}
