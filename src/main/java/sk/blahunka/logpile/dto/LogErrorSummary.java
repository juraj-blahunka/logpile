package sk.blahunka.logpile.dto;

import com.google.common.primitives.Ints;
import sk.blahunka.logpile.logs.token.AtLine;
import sk.blahunka.logpile.logs.token.Clazz;

import java.util.Comparator;

/**
 * An aggregate for a group of exceptions, which come from the same {@link sk.blahunka.logpile.logs.token.AtLine}
 */
public class LogErrorSummary {

	public static final Comparator<LogErrorSummary> BY_NUMBER_OF_TOTAL_LOG_MESSAGES = (o1, o2) -> Ints.compare(
			o1.getLogs().getNumberOfTotalLogMessages(),
			o2.getLogs().getNumberOfTotalLogMessages());

	private Clazz rootExceptionCause;
	private AtLine exceptionOriginAtLine;
	private LogMessages logs;

	public Clazz getRootExceptionCause() {
		return rootExceptionCause;
	}

	public void setRootExceptionCause(Clazz rootExceptionCause) {
		this.rootExceptionCause = rootExceptionCause;
	}

	public AtLine getExceptionOriginAtLine() {
		return exceptionOriginAtLine;
	}

	public void setExceptionOriginAtLine(AtLine exceptionOriginAtLine) {
		this.exceptionOriginAtLine = exceptionOriginAtLine;
	}

	public LogMessages getLogs() {
		return logs;
	}

	public void setLogs(LogMessages logs) {
		this.logs = logs;
	}

}
