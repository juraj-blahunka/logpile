package sk.blahunka.logpile.logs;

import sk.blahunka.logpile.dto.LogErrorSummary;
import sk.blahunka.logpile.dto.LogMessages;
import sk.blahunka.logpile.logs.token.AtLine;
import sk.blahunka.logpile.logs.token.CausedBy;
import sk.blahunka.logpile.logs.token.Clazz;
import sk.blahunka.logpile.logs.token.LogStatement;

import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogsService {

	/**
	 * This is the API!
	 *
	 * @param inputStream
	 * @return
	 */
	public List<LogErrorSummary> errors(InputStream inputStream) {
		TokenParser tokenParser = new TokenParser();
		List<LogStatement> statements = tokenParser.parseLogStatemens(inputStream);

		List<LogStatement> errors = filterErrors(statements);
		Map<LogsService.StackTraceKey, LogMessages> uniqueErrors = errorsWithSameOrigin(errors);
		return categorizeErrors(uniqueErrors);
	}


	protected List<LogStatement> filterErrors(List<LogStatement> statements) {
		return statements.stream()
				.filter(log -> log.hasCausedBy())
				.collect(Collectors.toList());
	}

	protected Map<StackTraceKey, LogMessages> errorsWithSameOrigin(List<LogStatement> errors) {
		Map<StackTraceKey, LogMessages> result = new HashMap<>();

		for (LogStatement log : errors) {
			CausedBy causedBy = log.lastCausedBy();

			Clazz causedByClazz = causedBy.getExceptionClazz();
			AtLine atLine = causedBy.getAtLines().size() > 0
					? causedBy.getAtLines().get(0)
					: null;

			StackTraceKey key = new StackTraceKey(causedByClazz, atLine);

			LogMessages logs = result.get(key);
			if (logs == null) {
				logs = new LogMessages();
				result.put(key, logs);
			}

			logs.put(log);
		}

		return result;
	}

	protected List<LogErrorSummary> categorizeErrors(Map<StackTraceKey, LogMessages> errors) {
		LinkedList<LogErrorSummary> result = new LinkedList<>();

		for (StackTraceKey key : errors.keySet()) {
			LogMessages logs = errors.get(key);

			LogErrorSummary summary = new LogErrorSummary();
			summary.setRootExceptionCause(key.getCausedByException());
			summary.setExceptionOriginAtLine(key.getAtLine());
			summary.setLogs(logs);

			result.add(summary);
		}

		return result;
	}

	protected static class StackTraceKey {

		private final Clazz causedByException;

		private final AtLine atLine;

		public StackTraceKey(Clazz causedByException, AtLine atLine) {
			this.causedByException = causedByException;
			this.atLine = atLine;
		}

		public Clazz getCausedByException() {
			return causedByException;
		}

		public AtLine getAtLine() {
			return atLine;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;

			StackTraceKey that = (StackTraceKey) o;

			if (atLine != null ? !atLine.equals(that.atLine) : that.atLine != null) return false;
			if (causedByException != null ? !causedByException.equals(that.causedByException) : that.causedByException != null)
				return false;

			return true;
		}

		@Override
		public int hashCode() {
			int result = causedByException != null ? causedByException.hashCode() : 0;
			result = 31 * result + (atLine != null ? atLine.hashCode() : 0);
			return result;
		}

		@Override
		public String toString() {
			return "StackTraceKey{" +
					"causedByException='" + causedByException + '\'' +
					", atLine='" + atLine + '\'' +
					'}';
		}

	}

}
