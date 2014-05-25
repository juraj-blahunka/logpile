package sk.blahunka.logpile;

import org.junit.Test;
import sk.blahunka.logpile.logs.TokenParser;
import sk.blahunka.logpile.logs.token.CausedBy;
import sk.blahunka.logpile.logs.token.Level;
import sk.blahunka.logpile.logs.token.LogStatement;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.Assert.*;

public class TokenParserTest {

	private static final String LINES_SIMPLE = "09:31:07,268 SEVERE [facelets.viewhandler] (http-0.0.0.0-8443-6) Error Rendering View[/spot.xhtml]\n" +
			"java.lang.ArrayIndexOutOfBoundsException";

	private static final String ERROR_WITH_CAUSED_BY_IN_THE_END = "10:49:58,279 ERROR [org.jboss.seam.exception.Exceptions] (http-0.0.0.0-8443-8) handled and logged exception\n" +
			"javax.servlet.ServletException\n" +
			"	at javax.faces.webapp.FacesServlet.service(FacesServlet.java:277)\n" +
			"	at java.lang.Thread.run(Thread.java:662)\n" +
			"Caused by: java.lang.ArrayIndexOutOfBoundsException";

	private static final String COMPLEX_ERROR = "10:03:05,646 ERROR [org.jboss.seam.exception.Exceptions] (http-0.0.0.0-8443-3) handled and logged exception\n" +
			"javax.servlet.ServletException: javax.el.ELException: /incl/mainmenu.xhtml @10,260 rendered=\"#{identity.loggedIn and identity.hasRole(applicationService.showRoleId('ADMINL')) eq false}\": org.jboss.seam.RequiredException: @In attribute requires non-null value: applicationService.entityManager\n" +
			"	at javax.faces.webapp.FacesServlet.service(FacesServlet.java:277)\n" +
			"	at com.sun.faces.lifecycle.Phase.doPhase(Phase.java:100)\n" +
			"	at com.sun.faces.lifecycle.LifecycleImpl.execute(LifecycleImpl.java:118)\n" +
			"	at javax.faces.webapp.FacesServlet.service(FacesServlet.java:265)\n" +
			"	... 59 more\n" +
			"Caused by: javax.el.ELException: /incl/mainmenu.xhtml @10,260 rendered=\"#{identity.loggedIn and identity.hasRole(applicationService.showRoleId('ADMIN')) eq false}\": org.jboss.seam.RequiredException: @In attribute requires non-null value: applicationService.entityManager\n" +
			"	at com.sun.facelets.el.TagValueExpression.getValue(TagValueExpression.java:76)\n" +
			"	at javax.faces.component.UIComponentBase.isRendered(UIComponentBase.java:390)\n" +
			"	... 67 more\n" +
			"Caused by: org.jboss.seam.RequiredException: @In attribute requires non-null value: applicationService.entityManager\n" +
			"	at org.jboss.el.ValueExpressionImpl.getValue(ValueExpressionImpl.java:186)\n" +
			"	at com.sun.facelets.el.TagValueExpression.getValue(TagValueExpression.java:71)\n" +
			"	... 68 more";

	private static final String COMPLEX_INFO = "03:37:42,533 INFO  [javax.enterprise.resource.webcontainer.jsf.lifecycle] (http-0.0.0.0-8443-15) WARNING: FacesMessage(s) have been enqueued, but may not have been displayed.\n" +
			"sourceId=mlb_reportTypeChooser[severity=(ERROR 2), summary=(mlb_reportTypeChooser: Error de Validación: Valor no es correcto.), detail=(mlb_reportTypeChooser: Error de Validación: Valor no es correcto.)]";

	private static final String SIMPLE_INFO_WITH_EXCEPTION = "12:01:54,373 INFO  [org.jboss.resource.connectionmanager.TxConnectionManager] (http-0.0.0.0-8443-4) throwable from unregister connection\n" +
			"java.lang.IllegalStateException: Trying to return an unknown connection2! org.jboss.resource.adapter.jdbc.jdk6.WrappedConnectionJDK6@392ee40a\n" +
			"\tat org.jboss.resource.connectionmanager.CachedConnectionManager.unregisterConnection(CachedConnectionManager.java:330)\n" +
			"\tat org.apache.tomcat.util.net.JIoEndpoint$Worker.run(JIoEndpoint.java:447)\n" +
			"\tat java.lang.Thread.run(Thread.java:662)";

	private TokenParser tokenParser = new TokenParser();

	@Test
	public void testSimpleTwoLineLog() {
		LogStatement log = parseOneLogFromString(LINES_SIMPLE);

		assertEquals("Error Rendering View[/spot.xhtml]", log.getMessage());
		assertEquals(1, log.getCausedBies().size());
		assertEquals(1, log.getCausedBies().size());
	}

	@Test
	public void testLinesCausedByInTheEnd() {
		LogStatement log = parseOneLogFromString(ERROR_WITH_CAUSED_BY_IN_THE_END);

		System.out.println(log);

		assertEquals("handled and logged exception", log.getMessage());

		assertEquals(2, log.getCausedBies().size());
		assertEquals(2, log.getCausedBies().get(0).getAtLines().size());

		assertEquals("", log.lastCausedBy().getMessage());
	}

	@Test
	public void testPrettyComplex() {
		LogStatement log = parseOneLogFromString(COMPLEX_ERROR);

		assertEquals(3, log.getCausedBies().size());

		CausedBy causedBy = log.getCausedBies().get(1);

		assertEquals("javax.el.ELException", causedBy.getExceptionClazz().getFullyQualifiedName());
		assertEquals("/incl/mainmenu.xhtml @10,260 rendered=\"#{identity.loggedIn and identity.hasRole(applicationService.showRoleId('ADMIN')) eq false}\": org.jboss.seam.RequiredException: @In attribute requires non-null value: applicationService.entityManager", causedBy.getMessage());
	}

	@Test
	public void testComplexInfo() {
		LogStatement log = parseOneLogFromString(COMPLEX_INFO);

		assertNull(log.getCausedBies());
		assertTrue(log.getMessage().contains("sourceId=mlb_reportTypeChooser"));
	}

	@Test
	public void testSimpleInfoWithException() {
		LogStatement log = parseOneLogFromString(SIMPLE_INFO_WITH_EXCEPTION);

		System.out.println(log);
		assertEquals("throwable from unregister connection", log.getMessage());
		assertEquals(Level.INFO, log.getLevel());

		assertEquals(1, log.getCausedBies().size());
		assertEquals("java.lang.IllegalStateException", log.lastCausedBy().getExceptionClazz().getFullyQualifiedName());
		assertEquals("Trying to return an unknown connection2! org.jboss.resource.adapter.jdbc.jdk6.WrappedConnectionJDK6@392ee40a", log.lastCausedBy().getMessage());

		assertEquals(3, log.lastCausedBy().getAtLines().size());
		assertEquals("org.jboss.resource.connectionmanager.CachedConnectionManager", log.lastCausedBy().getAtLines().get(0).getClazz().getFullyQualifiedName());
		assertEquals("unregisterConnection", log.lastCausedBy().getAtLines().get(0).getMethod());
		assertEquals("CachedConnectionManager.java:330", log.lastCausedBy().getAtLines().get(0).getSource());
	}

	private LogStatement parseOneLogFromString(String input) {
		List<LogStatement> logStatements = tokenParser.parseLogStatemens(new ByteArrayInputStream(input.getBytes()));
		assertEquals(1, logStatements.size());
		return logStatements.get(0);
	}

}
