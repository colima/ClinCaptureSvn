package com.clinovo.pages;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.clinovo.pages.beans.StudyEventDefinition;

import net.thucydides.core.annotations.findby.By;
import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

/**
 * Created by Anton on 18.07.2014.
 */
public class SubjectMatrixPage extends BasePage {

	public static final String PAGE_NAME = "SM page";
	public static final String PAGE_URL = "ListStudySubjects";

	public SubjectMatrixPage(WebDriver driver) {
		super(driver);
	}

	@FindBy(id = "findSubjects")
	private WebElementFacade tFindSubjects;

	@FindBy(xpath = ".//div[starts-with(@id, 'eventScheduleWrapper')]//*[@id='startdateField']")
	private WebElementFacade iStartDate;

	@FindBy(xpath = ".//div[starts-with(@id, 'eventScheduleWrapper')]//*[@id='enddateField']")
	private WebElementFacade iEndDate;

	@FindBy(xpath = ".//div[starts-with(@id, 'eventScheduleWrapper')]//*[@name='Schedule']")
	private WebElementFacade bScheduleEvent;

	@FindBy(xpath = ".//*[@class='crfListTable']//a[contains(@href,'UpdateStudyEvent')]/img[contains(@src,'icon_SignedBlue.gif')]")
	private WebElementFacade bSignEvent;

	@FindBy(xpath = ".//*[@class='crfListTable']//a[contains(@href,'SignStudySubject')]/img[contains(@src,'icon_SignedBlue.gif')]")
	private WebElementFacade bSignSubject;

	@FindBy(xpath = ".//*[@name='flag_start']")
	private WebElementFacade lStartDateFlag;

	@FindBy(xpath = ".//*[@name='flag_end']")
	private WebElementFacade lEndDateFlag;

	@FindBy(xpath = ".//*[@name='flag_location']")
	private WebElementFacade lLocationFlag;

	@FindBy(className = "crfListTable")
	private WebElementFacade tCRFList;

	@FindBy(xpath = ".//div[contains(@onclick,'studySubject.label')][@class='dynFilter']")
	private WebElementFacade divFindSubjects;

	@FindBy(id = "dynFilterInput")
	private WebElementFacade iFilterField;

	@FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('findSubjects','filter')\")]")
	private WebElementFacade lApplyFilter;

	@FindBy(xpath = ".//tr[@class='filter']//a[contains(@href,\"onInvokeAction('findSubjects','clear')\")]")
	private WebElementFacade lClearFilter;

	@FindBy(xpath = ".//*[@id='findSubjects_row1']")
	private WebElementFacade trFirstRow;

	@FindBy(id = "sel")
	private WebElementFacade bExit;

	@FindBy(id = "dlgBtnYes")
	private WebElementFacade bDlgYes;

	@Override
	public boolean isOnPage(WebDriver driver) {
		return tFindSubjects.isCurrentlyVisible();
	}

	public void filterSMByStudySubjectID(String sSubjectID) {
		enterStudySubjectIDToFilterField(sSubjectID);
		clickApplyFilterLink();
	}

	public void enterStudySubjectIDToFilterField(String sSubjectID) {
		divFindSubjects.waitUntilVisible();
		divFindSubjects.click();
		iFilterField.type(sSubjectID);
	}

	public void clickApplyFilterLink() {
		lApplyFilter.click();
	}

	public void clickClearFilterLink() {
		lClearFilter.click();
	}

	public void callPopupForSubjectAndEvent(String studySubjectID, String eventName) {
		List<WebElement> eventIcons = tFindSubjects.withTimeoutOf(60, TimeUnit.SECONDS)
				.findElements(By.xpath(".//td[text()='" + studySubjectID + "']"));
		if (eventIcons.size() == 0) {
			filterSMByStudySubjectID(studySubjectID);
		}
		WebElementFacade eventIcon = tFindSubjects.findBy(
				By.xpath(".//td[text()='" + studySubjectID + "']/..//div[@event_name='" + eventName + "']/../a"));
		eventIcon.click();
	}

	private void initElementsInPopup() {
		bScheduleEvent.withTimeoutOf(60, TimeUnit.SECONDS).waitUntilVisible();
	}

	public void fillInPopupToScheduleEvent(StudyEventDefinition event) {
		initElementsInPopup();
		if (iStartDate.isCurrentlyVisible() && !event.getStartDateTime().isEmpty()) {
			iStartDate.type(event.getStartDateTime());
		}
		if (iEndDate.isCurrentlyVisible() && !event.getEndDateTime().isEmpty()) {
			iEndDate.type(event.getEndDateTime());
		}
	}

	public void clickScheduleEventButtonInPopup() {
		bScheduleEvent.waitUntilVisible();
		bScheduleEvent.click();
	}

	public void eventIsScheduled(StudyEventDefinition event) {
		WebElementFacade eventIcon = findEventIconOnSM(event.getStudySubjectID(), event.getName());
		Assert.assertTrue(eventIcon.getAttribute("src").endsWith("icon_Scheduled.gif"));
	}

	public void clickEnterDataButtonInPopup(String aCRFName) {
		clickButtonInPopup(aCRFName, "Enter Data");
	}

	public void clickViewCRFButtonInPopup(String aCRFName) {
		clickButtonInPopup(aCRFName, "View CRF");
	}
	
	private void clickButtonInPopup(String aCRFName, String buttonName) {
		tCRFList.waitUntilVisible();
		List<WebElement> tds = tFindSubjects.findElements(
				By.xpath(".//div[starts-with(@id, 'crfListWrapper')]//td[contains(text(), '" + aCRFName + "')]"));
		for (WebElement td : tds) {
			if (td.getText().replaceFirst(aCRFName, "").trim().replace("*", "").isEmpty()) {
				switch (buttonName) {
	    		case "Enter Data":
	    			td.findElement(By.xpath("./..//img[contains(@name,'bt_EnterData')]")).click();
	    			break;
	    		case "View CRF":
	    			td.findElement(By.xpath("./..//img[contains(@src,'bt_View.gif')]")).click();
	        		break;
	        	default:
	        		Assert.assertTrue(false);
	    		}
				
				break;
			}
		}
	}
	
	public void clickSignEventButton() {
		bSignEvent.waitUntilVisible();
		bSignEvent.click();
	}

	public void filterSMPage(Map<String, String> map) {
		if (map.containsKey("Study Subject ID")) {
			enterStudySubjectIDToFilterField(map.get("Study Subject ID"));
		}

		clickApplyFilterLink();
	}

	public void checkSignEventStatus(Map<String, String> values) {
		if (values.containsKey("Study Subject ID") && values.containsKey("Event Name")) {
			WebElementFacade eventIcon = findEventIconOnSM(values.get("Study Subject ID"), values.get("Event Name"));
			Assert.assertTrue(eventIcon.getAttribute("src").endsWith("icon_Signed.gif"));
		}
	}

	public WebElementFacade findEventIconOnSM(String studySubjectID, String eventName) {
		return tFindSubjects.findBy(
				By.xpath(".//td[text()='" + studySubjectID + "']/..//div[@event_name='" + eventName + "']/../a/img"));
	}

	public void checkFirstRowIsPresent() {
		Assert.assertTrue(trFirstRow.isCurrentlyVisible());
	}

	public void clickStartDateFlagInPopup() {
		lStartDateFlag.waitUntilVisible();
		lStartDateFlag.click();
	}

	public void clickEndDateFlagInPopup() {
		lEndDateFlag.waitUntilVisible();
		lEndDateFlag.click();
	}

	public void clickLocationFlagInPopup() {
		lLocationFlag.waitUntilVisible();
		lLocationFlag.click();
	}

	public void clickViewIconForStudySubject(String studySubjectID) {
		tFindSubjects.withTimeoutOf(60, TimeUnit.SECONDS)
				.findElement(
						By.xpath(".//td[text()='" + studySubjectID + "']/..//a[contains(@href, 'ViewStudySubject')]"))
				.click();
	}
}
