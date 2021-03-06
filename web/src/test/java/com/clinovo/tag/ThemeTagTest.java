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

package com.clinovo.tag;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import org.akaza.openclinica.control.core.OCServletFilter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ThemeTag.class)
public class ThemeTagTest {

	@Mock
	private ThemeTag themeTag;

	@Mock
	private PageContext pageContext;

	@Mock
	private ServletContext servletContext;

	@Mock
	private MockHttpServletRequest request;

	@Mock
	private MockHttpSession session;

	@Mock
	private JspWriter jspWriter;

	@Before
	public void setUp() throws JspException {
		Mockito.when(pageContext.getOut()).thenReturn(jspWriter);
		Mockito.when(pageContext.getRequest()).thenReturn(request);
		Mockito.when(pageContext.getSession()).thenReturn(session);
		Mockito.when(pageContext.getServletContext()).thenReturn(servletContext);
		Mockito.when(servletContext.getContextPath()).thenReturn("/clincapture");
		Whitebox.setInternalState(themeTag, "pageContext", pageContext);
		Mockito.when(themeTag.doStartTag()).thenCallRealMethod();
		Mockito.when(request.getAttribute(OCServletFilter.REVISION_NUMBER)).thenReturn("123456789");
	}

	@Test
	public void testThatDoStartTagDoesNotThrowAnExceptionIfSessionIsEmpty() throws JspException {
		themeTag.doStartTag();
	}
}
