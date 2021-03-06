package org.akaza.openclinica.control.managestudy;

import static org.junit.Assert.assertNull;

import java.util.Locale;

import javax.sql.DataSource;

import org.akaza.openclinica.bean.login.UserAccountBean;
import org.akaza.openclinica.bean.managestudy.StudySubjectBean;
import org.akaza.openclinica.core.SecurityManager;
import org.akaza.openclinica.dao.managestudy.StudySubjectDAO;
import org.akaza.openclinica.i18n.util.ResourceBundleProvider;
import org.akaza.openclinica.view.Page;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.core.userdetails.UserDetails;

import com.clinovo.i18n.LocaleResolver;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ResourceBundleProvider.class)
public class SignStudySubjectServletTest {

	public static final int ID = 1;
	@Mock
	private SignStudySubjectServlet signStudySubjectServlet;
	@Mock
	private MockHttpSession session;
	@Mock
	private MockHttpServletRequest request;
	@Mock
	private MockHttpServletResponse response;
	@Mock
	private DataSource dataSource;
	@Mock
	private StudySubjectDAO mockedStudySubjectDAO;
	@Mock
	private StudySubjectBean mockedSSBean;
	@Mock
	private UserAccountBean userAccountBean;

	@Before
	public void setUp() throws Exception {
		request.setAttribute("id", "1");
		Locale locale = new Locale("en");
		PowerMockito.when(request.getSession()).thenReturn(session);
		LocaleResolver.updateLocale(session, locale);
		ResourceBundleProvider.updateLocale(locale);
	}

	@Test
	public void testThatEnteringWrongCredentialsKeepsUserOnCorrectPage() throws Exception {
		SecurityManager securityManager = PowerMockito.mock(SecurityManager.class);
		UserDetails userDetails = PowerMockito.mock(UserDetails.class);
		request.addParameter("j_user", "test_pi");
		request.addParameter("j_pass", "pass");
		userAccountBean.setId(ID);
		userAccountBean.setName("test_pi");
		PowerMockito.doCallRealMethod().when(signStudySubjectServlet).authenticateUser(request, response,
				userAccountBean, mockedStudySubjectDAO, 1, mockedSSBean);
		PowerMockito.doReturn(userAccountBean).when(signStudySubjectServlet).getUserAccountBean(request);
		PowerMockito.doReturn(dataSource).when(signStudySubjectServlet).getDataSource();
		PowerMockito.doReturn(securityManager).when(signStudySubjectServlet).getSecurityManager();
		PowerMockito.doReturn(false).when(securityManager).isPasswordValid("test_pi", "pass", userDetails);
		Page page = signStudySubjectServlet.authenticateUser(request, response, userAccountBean, mockedStudySubjectDAO,
				1, mockedSSBean);
		assertNull(page);
	}
}
