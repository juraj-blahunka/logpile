package sk.blahunka.logpile.ast;

import org.joda.time.LocalTime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineParser {

	private static final String JAVA_CLASS = "(?<class>[a-zA-Z0-9$]+\\.[a-zA-Z0-9_$\\.]+)";

	// TODO @blj externalize regexes (GUI)
	private static final Pattern LOG_MESSAGE_PATTERN =
			Pattern.compile("(?<time>\\d{2}:\\d{2}:\\d{2},\\d{3})\\s+" + // 01:55:16,276
					"(?<level>[a-zA-Z]*)\\s+" + // INFO
					"\\[" + JAVA_CLASS + "\\]\\s+" + // [com.web.seam.action.AuthenticatorImpl]
					"\\((.*)\\)\\s+" + // (http-0.0.0.0-8443-1)
					"(?<message>.*)"); // Login successfull for user: someone@where.com;

	private static final Pattern CAUSED_BY_PATTERN =
			Pattern.compile("^(Caused by: )?" + // start of the line
					JAVA_CLASS + // class name
					"(: )?" + // possible ": " after caused by
					"(?<message>.*)?"); // possible message

	private static final Pattern AT_LINE_PATTERN =
			Pattern.compile("\\s*at " + // "  at "
					JAVA_CLASS + "\\.(?<method>[a-zA-Z0-9_]*)" + // sun.java.Class.methodName
					"\\((?<source>.+)\\)"); // (fileName.java:211)

	private static final Pattern MORE_PATTERN =
			Pattern.compile("(\\s*)(\\.){3} [0-9]+ more");

	private final ClassRepository classRepository = new ClassRepository();

	public LogStatement parseLogStatement(String line) {
		Matcher matcher = LOG_MESSAGE_PATTERN.matcher(line);
		if (matcher.matches()) {
			String time = matcher.group("time");
			LocalTime localTime = parseLocalTime(time);

			String levelString = matcher.group("level");
			Level level = parseLevel(levelString);

			String clazzString = matcher.group("class");
			Clazz clazz = classRepository.cachedClazz(clazzString);

			String message = matcher.group("message");

			return new LogStatement(localTime, level, clazz, message);
		}
		return null;
	}

	public CausedBy parseCausedBy(String line, LogStatement currentLogStatement) {
		Matcher matcher = CAUSED_BY_PATTERN.matcher(line);
		if (matcher.matches()) {
			String clazzString = matcher.group("class");
			String message = matcher.group("message");

			Clazz clazz = classRepository.cachedClazz(clazzString);
			boolean topLevelException = !currentLogStatement.hasCausedBy();

			return new CausedBy(clazz, message, topLevelException);
		}
		return null;
	}

	private Level parseLevel(String levelString) {
		return Level.valueOf(levelString);
	}

	public LocalTime parseLocalTime(String time) {
		return LocalTime.parse(time);
	}

	public AtLine parseAtLine(String line) {
		Matcher matcher = AT_LINE_PATTERN.matcher(line);
		if (matcher.matches()) {
			String clazzString = matcher.group("class");
			String method = matcher.group("method");
			String source = matcher.group("source");

			Clazz clazz = classRepository.cachedClazz(clazzString);

			return new AtLine(clazz, method, source);
		}
		return null;
	}

	public boolean matchesMore(String line) {
		return MORE_PATTERN.matcher(line).matches();
	}

}
