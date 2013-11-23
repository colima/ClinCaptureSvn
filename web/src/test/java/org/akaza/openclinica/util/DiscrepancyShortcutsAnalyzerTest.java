package org.akaza.openclinica.util;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.akaza.openclinica.bean.managestudy.DiscrepancyNoteBean;
import org.akaza.openclinica.bean.submit.DisplayItemBean;
import org.akaza.openclinica.bean.submit.ItemBean;
import org.akaza.openclinica.bean.submit.ItemDataBean;
import org.akaza.openclinica.service.DiscrepancyNoteThread;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class DiscrepancyShortcutsAnalyzerTest {

	private HttpServletRequest request;
	private DisplayItemBean displayItemBean;
	private DiscrepancyNoteBean discrepancyNoteBean;
	private List<DiscrepancyNoteThread> noteThreads;

	@Before
	public void setUp() throws Exception {
		request = new MockHttpServletRequest();
		ItemDataBean itemDataBean = Mockito.mock(ItemDataBean.class);
		itemDataBean.setId(1);
		displayItemBean = new DisplayItemBean();
		displayItemBean.setData(itemDataBean);
		displayItemBean.setDbData(itemDataBean);
		ItemBean itemBean = new ItemBean();
		itemBean.setId(1);
		displayItemBean.setItem(itemBean);
		noteThreads = new ArrayList<DiscrepancyNoteThread>();
		ArrayList<DiscrepancyNoteBean> discrepancyNotes = new ArrayList<DiscrepancyNoteBean>();
		discrepancyNoteBean = new DiscrepancyNoteBean();
		discrepancyNoteBean.setItemId(1);
		discrepancyNoteBean.setEntityType("itemData");
		discrepancyNoteBean.setParentDnId(0);
		discrepancyNotes.add(discrepancyNoteBean);
		DiscrepancyNoteThread discrepancyNoteThread = new DiscrepancyNoteThread();
		discrepancyNoteThread.setLinkedNoteList(new LinkedList<DiscrepancyNoteBean>(discrepancyNotes));
		displayItemBean.setDiscrepancyNotes(discrepancyNotes);
		noteThreads.add(discrepancyNoteThread);
		request.setAttribute("discrepancyShortcutsAnalyzer", new DiscrepancyShortcutsAnalyzer());
	}

	@Test
	public void testThatIsFirstNewDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(1);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstNewDn());
	}

	@Test
	public void testThatIsFirstUpdatedDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(2);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstUpdatedDn());
	}

	@Test
	public void testThatIsFirstResolutionProposedReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(3);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstResolutionProposed());
	}

	@Test
	public void testThatIsFirstClosedDnReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(4);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstClosedDn());
	}

	@Test
	public void testThatIsFirstAnnotationReturnsCorrectValue() throws Exception {
		discrepancyNoteBean.setResolutionStatusId(5);
		DiscrepancyShortcutsAnalyzer.prepareDnShortcutAnchors(request, displayItemBean, noteThreads);
		assertTrue(displayItemBean.isFirstAnnotation());
	}
}
