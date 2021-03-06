package com.clinovo.pages;

import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import net.thucydides.core.annotations.findby.FindBy;
import net.thucydides.core.pages.WebElementFacade;

/**
 * Created by Anton on 07.07.2014.
 */
public class BasePage extends AbstractPage {
	@FindBy(jquery = "a[href$='j_spring_security_logout']")
	private WebElementFacade lLogOut;

	@FindBy(jquery = "a[href='ViewNotes?module=submit']")
	private WebElementFacade lNDs;

	@FindBy(jquery = "a[href$='ListSubject']")
	private WebElementFacade lSubjects;

	@FindBy(jquery = "a[href$='system']")
	private WebElementFacade lSystem;

	@FindBy(id = "nav_Tasks_link")
	private WebElementFacade lTasksMenu;

	@FindBy(jquery = "a[href$='ListUserAccounts']")
	private WebElementFacade lUsers;

	@FindBy(jquery = "a[href='ListCRF']")
	private WebElementFacade lCRFs;

	@FindBy(jquery = "a[href$='ChangeStudy']")
	private WebElementFacade lChangeStudy;

	@FindBy(jquery = "a[href*='ViewSite']")
	private WebElementFacade lCurrentSite;

	@FindBy(jquery = "a[href*='ViewStudy']")
	private WebElementFacade lCurrentStudy;

	@FindBy(jquery = "a[href*='ViewRuleAssignment']")
	private WebElementFacade lRules;

	@FindBy(jquery = "a[href$='AddNewSubject']")
	private WebElementFacade lAddSubject;

	@FindBy(jquery = "a[href$='studymodule']")
	private WebElementFacade lBuildStudy;

	@FindBy(linkText = "Study Audit Log")
	private WebElementFacade lStudyAuditLog;

	@FindBy(linkText = "Subject Matrix")
	private WebElementFacade lSubjectMatrix;

	@FindBy(jquery = "a[href*='pages/viewAllSubjectSDVtmp']")
	private WebElementFacade lSourceDataVerification;

	@FindBy(id = "Submit")
	private WebElementFacade bSubmitA;

	@FindBy(jquery = "input[name$='ubmit'],input[id$='ubmit'],input[name$='onfirm']")
	private WebElementFacade bSubmitU;

	@FindBy(jquery = "input[name$='ontinue'],input[id$='ontinue'],input[name$='onfirm']")
	private WebElementFacade bContinueU;

	@FindBy(id = "sideBarTable")
	protected WebElementFacade tSideBar;

	@FindBy(xpath = "//div[@class='alert']")
	protected WebElementFacade dAlert;

	public boolean taskMenuIsVisible() {
		return lTasksMenu.isVisible();
	}

	public void goToManageRulesPage() {
		lTasksMenu.click();
		lRules.click();
	}

	public void goToSDVPage() {
		lTasksMenu.click();
		lSourceDataVerification.click();
	}

	public void goToBuildStudyPage() {
		lTasksMenu.click();
		lBuildStudy.click();
	}

	public void goToConfigureSystemPropertiesPage() {
		lTasksMenu.click();
		lSystem.click();
	}

	public void goToStudyAuditLog() {
		lTasksMenu.click();
		lStudyAuditLog.click();
	}

	public void goToSubjectMatrix() {
		lSubjectMatrix.click();
	}

	public void goToAdministerUsersPage() {
		lTasksMenu.click();
		lUsers.click();
	}

	public void goToAdministerCRFsPage() {
		lTasksMenu.click();
		lCRFs.click();
	}

	public void clickSubmit() {
		if (bSubmitA.isCurrentlyVisible()) {
			bSubmitA.click();
		} else {
			bSubmitU.click();
		}
	}

	public void clickContinue() {
		bContinueU.click();
	}

	public BasePage(WebDriver driver) {
		super(driver);
	}

	public void logOut() {
		lLogOut.click();
	}

	@Override
	public boolean isOnPage(WebDriver driver) {
		return false;
	}

	public void clickChangeStudyLink() {
		lChangeStudy.click();
	}

	public String getCurrentStudyName() {
		if (lCurrentSite.isCurrentlyVisible()) {
			return lCurrentSite.getText();
		} else {
			return lCurrentStudy.getText();
		}
	}

	public String getCurrentParentStudyName() {
		return lCurrentStudy.getText();
	}

	public void isOnStudyLevel() {
		Assert.assertTrue(!lCurrentSite.isCurrentlyVisible());
	}

	public void clickAddSubjectLink() {
		lAddSubject.click();
	}

	public void goToNDsPage() {
		lNDs.click();
	}

	public void goToAdministerSubjectsPage() {
		lTasksMenu.click();
		lSubjects.click();
	}
	
	public void message_is_shown(String message) {
		//in Alerts&Messages section
		Assert.assertTrue(tSideBar.find(By.xpath(".//div[contains(text(),'" + message + "')]")).isVisible());
	}

	public void verifyAlertMessage(String alert) {
		//in div near the field
		String alertMessage;
		alertMessage = dAlert.getText();
		Assert.assertTrue(alertMessage.equalsIgnoreCase(alert));
	}
}
