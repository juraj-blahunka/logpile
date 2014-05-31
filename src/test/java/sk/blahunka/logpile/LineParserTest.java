package sk.blahunka.logpile;

import org.junit.Before;
import org.junit.Test;
import sk.blahunka.logpile.logs.LineParser;
import sk.blahunka.logpile.logs.token.*;

import java.time.LocalTime;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LineParserTest {

	private static final String CAUSED_BY =
			"Caused by: javax.ejb.EJBTransactionRolledbackException";

	private static final String CAUSED_BY_ONLY_CLASS =
			"javax.servlet.ServletException";

	private static final String CAUSED_BY_LONG =
			"Caused by: javax.faces.FacesException: #{facilityChooserAction.facilitySelected}: javax.ejb.EJBTransactionRolledbackException";

	private static final String INFO_LOG =
			"01:55:16,276 INFO  [com.web.seam.action.AuthenticatorImpl] (http-0.0.0.0-8443-1) Login successfull for user: info@someone.com";

	private static final String ERROR_LOG =
			"09:39:28,613 ERROR [org.jboss.aspects.tx.TxPolicy] (http-0.0.0.0-8443-8) javax.ejb.EJBTransactionRolledbackException";

	private static final String SEVERE_LOG =
			"06:02:50,086 SEVERE [javax.enterprise.resource.webcontainer.jsf.application] (http-0.0.0.0-8443-9) javax.ejb.EJBTransactionRolledbackException";

	private static final String AT_LINE =
			"   at org.apache.catalina.core.ApplicationFilterChain.doFilter(ApplicationFilterChain.java:206)";

	private static final String MORE_LINE =
			"\t... 67 more";

	private static final String CAUSED_BY_WITH_DOUBLE_PATTERN = "Caused by: javax.el.ELException: /incl/mainmenu.xhtml @10,260 rendered=\"#{identity.loggedIn and identity.hasRole(applicationService.showRoleId('ADMIN')) eq false}\": org.jboss.seam.RequiredException: @In attribute requires non-null value: applicationService.entityManager";

	private LineParser lineParser = new LineParser();
	private LogStatement currentLogStatement;

	@Before
	public void setup() {
		currentLogStatement = mock(LogStatement.class);
		when(currentLogStatement.hasCausedBy()).thenReturn(true);
	}

	@Test
	public void parseCausedBy() {
		CausedBy causedBy = lineParser.parseCausedBy(CAUSED_BY, currentLogStatement);

		assertEquals(new Clazz("javax.ejb.EJBTransactionRolledbackException"), causedBy.getExceptionClazz());
	}

	@Test
	public void parseCausedByWithException() {
		CausedBy causedBy = lineParser.parseCausedBy(CAUSED_BY_LONG, currentLogStatement);

		assertEquals(new Clazz("javax.faces.FacesException"), causedBy.getExceptionClazz());
		assertEquals("#{facilityChooserAction.facilitySelected}: javax.ejb.EJBTransactionRolledbackException", causedBy.getMessage());
	}

	@Test
	public void testCausedByOnlyClass() {
		CausedBy causedBy = lineParser.parseCausedBy(CAUSED_BY_ONLY_CLASS, currentLogStatement);

		assertEquals("javax.servlet.ServletException", causedBy.getExceptionClazz().getFullyQualifiedName());
		assertEquals("", causedBy.getMessage());
	}

	@Test
	public void testLongLineWithDoubleRegexPattern() {
		CausedBy causedBy = lineParser.parseCausedBy(CAUSED_BY_WITH_DOUBLE_PATTERN, currentLogStatement);

		assertEquals("javax.el.ELException", causedBy.getExceptionClazz().getFullyQualifiedName());
		assertEquals("/incl/mainmenu.xhtml @10,260 rendered=\"#{identity.loggedIn and identity.hasRole(applicationService.showRoleId('ADMIN')) eq false}\": org.jboss.seam.RequiredException: @In attribute requires non-null value: applicationService.entityManager", causedBy.getMessage());
	}

	@Test
	public void parseLogStatement_INFO() {
		LogStatement log = lineParser.parseLogStatement(INFO_LOG);

		assertEquals(LocalTime.of(1, 55, 16), log.getTime());
		assertEquals(Level.INFO, log.getLevel());
		assertEquals(new Clazz("com.web.seam.action.AuthenticatorImpl"), log.getClazz());
		assertEquals("Login successfull for user: info@someone.com", log.getMessage());
	}

	@Test
	public void parseLogStatement_ERROR() {
		LogStatement log = lineParser.parseLogStatement(ERROR_LOG);

		assertNotNull(log);
		assertEquals(Level.ERROR, log.getLevel());
		// TODO finish testSimpleInfoWithException
	}

	@Test
	public void parseLogStatement_SEVERE() {
		LogStatement log = lineParser.parseLogStatement(SEVERE_LOG);

		assertNotNull(log);
		assertEquals(Level.SEVERE, log.getLevel());
		// TODO finish testSimpleInfoWithException
	}

	@Test
	public void parseAtLine() {
		AtLine atLine = lineParser.parseAtLine(AT_LINE);

		assertNotNull(atLine);
		assertEquals(new Clazz("org.apache.catalina.core.ApplicationFilterChain"), atLine.getClazz());
		assertEquals("doFilter", atLine.getMethod());
		assertEquals("ApplicationFilterChain.java:206", atLine.getSource());
	}

	@Test
	public void testMathesMore() {
		assertTrue(lineParser.matchesMore(MORE_LINE));
	}

}
