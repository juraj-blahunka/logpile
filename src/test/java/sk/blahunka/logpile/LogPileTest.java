package sk.blahunka.logpile;

import org.junit.Test;
import sk.blahunka.logpile.ast.LogStatement;

import java.io.ByteArrayInputStream;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class LogPileTest {

	private static final String LINES_SIMPLE = "09:31:07,268 SEVERE [facelets.viewhandler] (http-0.0.0.0-8443-6) Error Rendering View[/spot.xhtml]\n" +
			"java.lang.ArrayIndexOutOfBoundsException";

	private static final String LINES_CAUSED_BY_IN_THE_END = "10:49:58,279 ERROR [org.jboss.seam.exception.Exceptions] (http-0.0.0.0-8443-8) handled and logged exception\n" +
			"javax.servlet.ServletException\n" +
			"	at javax.faces.webapp.FacesServlet.service(FacesServlet.java:277)\n" +
			"	at java.lang.Thread.run(Thread.java:662)\n" +
			"Caused by: java.lang.ArrayIndexOutOfBoundsException";

	private static final String PRETTY_COMPLEX = "10:03:05,646 ERROR [org.jboss.seam.exception.Exceptions] (http-0.0.0.0-8443-3) handled and logged exception\n" +
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

	private static final String STUPID_INFO = "03:37:42,533 INFO  [javax.enterprise.resource.webcontainer.jsf.lifecycle] (http-0.0.0.0-8443-15) WARNING: FacesMessage(s) have been enqueued, but may not have been displayed.\n" +
			"sourceId=mlb_reportTypeChooser[severity=(ERROR 2), summary=(mlb_reportTypeChooser: Error de Validación: Valor no es correcto.), detail=(mlb_reportTypeChooser: Error de Validación: Valor no es correcto.)]";

	private LogPile logPile = new LogPile();

	@Test
	public void test2LineMessageIsASimpleLog() {
		LogStatement log = parseOneLogFromString(LINES_SIMPLE);

		assertEquals("Error Rendering View[/spot.xhtml]", log.getMessage());
		assertEquals(1, log.getCausedBies().size());
		assertEquals(1, log.getCausedBies().size());
	}

	@Test
	public void testFuck() {
		LogStatement log = parseOneLogFromString(LINES_CAUSED_BY_IN_THE_END);

		assertEquals("handled and logged exception", log.getMessage());

		assertEquals(2, log.getCausedBies().size());

		assertEquals(2, log.getCausedBies().get(0).getAtLines().size());

		assertEquals("", log.firstCausedBy().getMessage());
	}

	@Test
	public void reallyFuck() {
		LogStatement log = parseOneLogFromString(PRETTY_COMPLEX);

		assertEquals(3, log.getCausedBies().size());
	}

	@Test
	public void testStupidInfo() {
		LogStatement log = parseOneLogFromString(STUPID_INFO);

		assertNull(log.getCausedBies());
	}

	private LogStatement parseOneLogFromString(String input) {
		List<LogStatement> logStatements = logPile.parseLogStatemens(new ByteArrayInputStream(input.getBytes()));
		assertEquals(1, logStatements.size());
		return logStatements.get(0);
	}

}
