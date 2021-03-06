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

 * LIMITATION OF LIABILITY. IN NO EVENT SHALL CLINOVO BE LIABLE FOR ANY INDIRECT, INCIDENTAL, SPECIAL, PUNITIVE OR CONSEQUENTIAL DAMAGES, OR DAMAGES FOR LOSS OF PROFITS, REVENUE, DATA OR DATA USE, INCURRED BY YOU OR ANY THIRD PARTY, WHETHER IN AN ACTION IN CONTRACT OR TORT, EVEN IF ORACLE HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. CLINOVO'S ENTIRE LIABILITY FOR DAMAGES HEREUNDER SHALL IN NO EVENT EXCEED TWO HUNDRED DOLLARS (U.S. $200).
 *******************************************************************************/
package com.clinovo.controller;

import com.clinovo.dao.SystemDAO;
import com.clinovo.model.CodedItem;
import com.clinovo.model.CodedItemElement;
import com.clinovo.model.Term;
import com.clinovo.service.CodedItemService;
import com.clinovo.service.TermService;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.control.core.SpringController;
import org.akaza.openclinica.dao.submit.ItemDataDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.jmesa.view.html.HtmlBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Encapsulates functionality required to build coding result.
 */
@Controller
public class CodedItemAutoUpdater extends SpringController {

	@Autowired
	private SystemDAO systemDAO;

	@Autowired
	private DataSource datasource;

	@Autowired
	private TermService termService;

	@Autowired
	private CodedItemService codedItemService;

	private final String bioontologyUrlDefault = "http://bioportal.bioontology.org";
	private final String bioontologyWsUrl = "http://data.bioontology.org";

	/**
	 * Method checks coding results for coded items with in process status.
	 *
	 * @param request
	 *            The incoming request.
	 * @param response
	 *            The response to redirect to.
	 * @throws IOException
	 *             If an error occur during the returning of results.
	 */
	@RequestMapping(value = "/checkCodedItemsStatus")
	public void checkCodedItemsStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String codedItemIdList = request.getParameter("arr");
		List<String> codedItemIdListString = new ArrayList<String>(Arrays.asList(codedItemIdList.split(",")));
		List<Integer> codedItemIdListInt = convertStringListToIntList(codedItemIdListString);
		String showContext = request.getParameter("showContext");
		showContext = showContext == null ? "false" : showContext;

		response.getWriter().println(buildResponseBox(codedItemIdListInt, showContext));
	}

	private List<String> buildResponseBox(List<Integer> codedItemIdListInt, String showContext)
			throws MalformedURLException {

		List<String> codedItemToAppend = new ArrayList<String>();
		for (int codedItemId : codedItemIdListInt) {
			CodedItem codedItem = codedItemService.findCodedItem(codedItemId);
			if (codedItem.isCoded()) {
				ItemDataDAO itemDataDAO = new ItemDataDAO(datasource);
				ItemDataBean data = (ItemDataBean) itemDataDAO.findByPK(codedItem.getItemId());
				String dataValue = "";
				if (codedItem.getCodedItemElementByItemName("GR").getItemDataId() > 0) {
					dataValue = data.getValue() + " (Grade " + codedItem.getCodedItemElementByItemName("GR").getItemCode() + ")";
				} else {
					dataValue = data.getValue();
				}
				Term term = termService.findByAliasAndExternalDictionary(dataValue.toLowerCase(), codedItem.getDictionary());
				if (term != null) {
					codedItemToAppend.add(contextBoxBuilder(codedItem, term.getLocalAlias(), term.getPreferredName(),
							showContext));
				} else {
					codedItemToAppend.add(contextBoxBuilder(codedItem, "", "", showContext));
				}
			}
		}

		return codedItemToAppend;
	}

	private String contextBoxBuilder(CodedItem codedItem, String alise, String prefTerm, String showContext)
			throws MalformedURLException {

		com.clinovo.model.System bioontologyUrl = systemDAO.findByName("defaultBioontologyURL");
		String termToAppend = "";
		String prefToAppend = "";
		String displayStyle = "display:none;";
		String httpPathDisplay = codedItem.getDictionary().toUpperCase().contains("WHOD") || codedItem.getDictionary().toUpperCase().contains("MEDDRA") ? "display:none;" : "";
		if (!alise.isEmpty() && !prefTerm.isEmpty()) {
			termToAppend = alise;
			prefToAppend = prefTerm;
		}
		if (showContext.equals("true")) {
			displayStyle = "";
		}

		HtmlBuilder builder = new HtmlBuilder();
		builder.table(1).id("tablepaging").styleClass("itemsTable")
				.append(" idToAppend=\"" + codedItem.getItemId() + "\" ").style(displayStyle)
				.append(" termToAppend=\"" + termToAppend + "\" ").append(" prefToAppend=\"" + prefToAppend + "\" ").close()
				.tr(1).style(httpPathDisplay).close().td(1).close().append(ResourceBundleProvider.getResWord("http") + ": ")
				.tdEnd().td(2).close().a().style("color:" + getThemeColor() + "")
				.append(" target=\"_blank\" ").href(normalizeUrl(bioontologyUrl.getValue()) + "/ontologies/"
						+ codedItem.getDictionary().replace("_", "") + "?p=classes&conceptid="
						+ codedItem.getHttpPath().replace("#", "%23")).close().append(codedItem.getHttpPath()).aEnd().tdEnd().td(2)
				.width("360px").colspan("2").close().tdEnd().td(2).close().tdEnd().trEnd(1);

		for (CodedItemElement codedItemElement : codedItemElementsFilter(codedItem).getCodedItemElements()) {
			builder.tr(1).close().td(1).style("white-space: nowrap;").close()
					.append(" " + ResourceBundleProvider.getResWord(codedItemElement.getItemName().toLowerCase())
							+ ": ").tdEnd().td(2).width("360px").close().append(codedItemElement.getItemCode()).tdEnd().tdEnd().td(2)
					.colspan("2").close().tdEnd().td(2).close().tdEnd().trEnd(1).trEnd(1);
		}
		builder.tableEnd(1);
		builder.append("separatorMark");

		return builder.toString();
	}

	private String normalizeUrl(String bioontologyUrl) throws MalformedURLException {
		if (bioontologyUrl.equals(bioontologyWsUrl)) {
			return bioontologyUrlDefault;
		} else {
			URL url = new URL(bioontologyUrl);
			return url.getProtocol() + "://" + url.getHost();
		}
	}

	private CodedItem codedItemElementsFilter(CodedItem codedItem) {

		CodedItem codedItemWithFilterFields = new CodedItem();
		for (CodedItemElement codedItemElement : codedItem.getCodedItemElements()) {
			for (CodedItemElement codedItemIteration : codedItem.getCodedItemElements()) {
				if ((codedItemElement.getItemName() + "C").equals(codedItemIteration.getItemName())) {
					if (!codedItemElement.getItemCode().isEmpty()) {
						codedItemWithFilterFields.addCodedItemElements(codedItemElement);
						break;
					}
				} else if (codedItemElement.getItemName().equals("CMP")
						|| codedItemElement.getItemName().equals("CNTR")
						|| codedItemElement.getItemName().equals("MPNC")) {
					if (!codedItemElement.getItemCode().isEmpty()) {
						codedItemWithFilterFields.addCodedItemElements(codedItemElement);
						break;
					}
				}
			}
		}
		Collections.sort(codedItemWithFilterFields.getCodedItemElements(), new CodedElementSortById());
		return codedItemWithFilterFields;
	}

	private class CodedElementSortById implements Comparator<Object> {
		public int compare(Object o1, Object o2) {
			CodedItemElement p1 = (CodedItemElement) o1;
			CodedItemElement p2 = (CodedItemElement) o2;
			return p1.getId() - p2.getId();
		}
	}

	private List<Integer> convertStringListToIntList(List<String> codedItemIdListString) {
		List<Integer> intList = new ArrayList<Integer>();
		for (String s : codedItemIdListString) {
			intList.add(Integer.valueOf(s));
		}
		return intList;
	}
}
