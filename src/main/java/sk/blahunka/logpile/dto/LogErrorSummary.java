package sk.blahunka.logpile.dto;

import com.google.common.primitives.Ints;
import sk.blahunka.logpile.ast.AtLine;
import sk.blahunka.logpile.ast.Clazz;

import java.util.Comparator;

public class LogErrorSummary {

	public static final Comparator<LogErrorSummary> BY_NUMBER_OF_TOTAL_LOG_MESSAGES = new Comparator<LogErrorSummary>() {
		@Override
		public int compare(LogErrorSummary o1, LogErrorSummary o2) {
			return Ints.compare(
					o1.getLogs().getNumberOfTotalLogMessages(),
					o2.getLogs().getNumberOfTotalLogMessages());
		}
	};

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
