package com.clinovo.util;

import org.akaza.openclinica.i18n.util.ResourceBundleProvider;

import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This class was created to avoid repeating of the same parts,
 * that are present in emails, like footer and others.
 */
public class EmailUtil {

	private EmailUtil() {
	}

	private static ResourceBundle resword;
	private static ResourceBundle resnotes;

	/**
	 * This method will return an opening tags of the main table
	 * for email body.
	 *
	 * @return html formatted email body opening tags.
	 */
	public static String getEmailBodyStart() {
		String text = "";
		return text
				.concat("<table width='700px' align='center' style='border:1px solid #cccccc;border-top:4px solid #893bc9'>")
				.concat("<tr><td colspan='2'>")
				.concat("<img src='https://ci6.googleusercontent.com/proxy/uSHHKqUdax2-dcP28gzmUvrePafRO0uQNWGvpGRKaq6V-tEyJahv3MyOe6cYEXIuDRqicKXBZYZQYEJRyc8fAtyi9KiroDRKySIZBA7EJlE7lXiSifm-f0d-SgL6_jUyxHYlSCQ=s0-d-e1-ft#https://clincapture.clinovo.com/builder/assets/images/email_template_header.jpg' />")
				.concat("</td></tr><tr><td style='vertical-align:top;font-family:Arial,Helvetica,sans-serif;font-size:12px;font-weight:normal;color:#333;line-height:20px; padding:10px'>");
	}

	/**

	/**
	 * This method will return an email footer, with
	 * social networks icons and privacy policy text.
	 *
	 * @param locale the locale of text.
	 * @return html formatted footer.
	 */
	public static String getEmailFooter(Locale locale) {
		ResourceBundleProvider.updateLocale(locale);
		resword = ResourceBundleProvider.getWordsBundle();
		resnotes = ResourceBundleProvider.getPageMessagesBundle();
		String text = "";
		int year = Calendar.getInstance().get(Calendar.YEAR);
		return text
				.concat("<table width='700px' align='center'>")
				.concat("<td style='height:60px; color:#333333;'><font face='helvetica' color='#454545'>")
				.concat(resnotes.getString("email_footer"))
				.concat(" Clinovo, 1208 E Arques Ave Suite 114, Sunnyvale, 94085 CA</font></td></tr>")
				.concat("<tr><td style='height:60px; color:#333333;'> <font face='helvetica' color='#454545'>")
				.concat(resword.getString("mail.respect_privacy"))
				.concat("<a style='color:#0088CF' target'_blank' href='https://www.clinovo.com/Privacy-Policy'> ")
				.concat(resword.getString("mail.privacy_policy"))
				.concat(" </a>")
				.concat(resword.getString("mail.more_information"))
				.concat(" <br />Copyright&copy; ")
				.concat(Integer.toString(year))
				.concat(" Clinovo, 1208 E Arques Ave Suite 114, Sunnyvale, 94085 CA</font></td></tr></table>");
	}

	/**
	 * This method will return closing tags of the main table
	 * for email body.
	 *
	 * @return html formatted email body closing tags.
	 */
	public static String getEmailBodyEnd() {
		String text = "";
		return text.concat("</td><td width='270px' align='center'><a href='http://www.linkedin.com/company/clinovo' target='_blank'>")
				.concat("<img src='https://ci3.googleusercontent.com/proxy/-AI4QwKi00YrGQtAsiczj4QswIZolhl0QTLDOUpk2kwB-0ueI79PP3CReJHleOOOk8OSYS_Rpl0MPms13tmAdTPPzQZawuBmZ1XDIs9K1qYq07BYsIpn1R2vmJtxFsUAd29L3yS5Zv5gXYzsfHwdezalfA=s0-d-e1-ft#https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_linkedin.jpg' class='CToWUd'/>")
				.concat("</a><a href='https://www.facebook.com/Clinovo' target='_blank'>")
				.concat("<img src='https://ci5.googleusercontent.com/proxy/kgcoisMGu1p6JDKQvrFUKnpplW66CByAcUz6p6fMMTPehFejc56EuygDmYKWgoJ5IFDIct2cXcq5__zY473q-XZom1xuobQ4OYKNGmlxnbuMCP1ZhZG4tdXP1T758XKeVA7v2Vv5-7llQxDiDAAY7lL4Lg=s0-d-e1-ft#https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_facebook.jpg'/>")
				.concat("</a><a href='https://twitter.com/Clinovo' target='_blank'>")
				.concat("<img src='https://ci6.googleusercontent.com/proxy/MLdjIotOBhNiJ-MMQfuwgYEF-eyO6V3cRXmpMlJAf6KrYDF1tiVtLJ40dZpU4MyIzQYd0_yJH-Fd7n-j8cQmebiN8Q5A-vJ0blq-ISHqRgfVLfN5zQ8iyHzmgJg7Y3ge7J8rADvVmsQ2cF0RVr57kBoL=s0-d-e1-ft#https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_twitter.jpg' class='CToWUd'/></a>")
				.concat("<img src='https://ci3.googleusercontent.com/proxy/2aSKQGnetlazEPm2DyYnAqxr8QYoTsiD_QIKtBuP05rKAbKw3JuPWzKiIXhCoxBvh_y2Dti5xXeB4AnhSWP-irT5346qKPfJjGlxqE3OVW88uKxQwPbJcKhvRNgWAtNh2vGuVJG5KK6kexPsLeSR=s0-d-e1-ft#https://clincapture.clinovo.com/builder/assets/images/email_template_social_link_call.jpg'/>")
				.concat("</td></tr></table>");
	}
}