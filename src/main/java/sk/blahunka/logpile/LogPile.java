package sk.blahunka.logpile;

import sk.blahunka.logpile.ast.*;
import sk.blahunka.logpile.dto.LogErrorSummary;
import sk.blahunka.logpile.dto.LogMessages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LogPile {

	private LineParser lineParser = new LineParser();

	/**
	 * This is the API!
	 *
	 * @param inputStream
	 * @return
	 */
	public List<LogErrorSummary> errors(InputStream inputStream) {
		List<LogStatement> statements = parseLogStatemens(inputStream);
		List<LogStatement> errors = filterErrors(statements);
		Map<LogPile.StackTraceKey, LogMessages> uniqueErrors = errorsWithSameOrigin(errors);
		return categorizeErrors(uniqueErrors);
	}

	protected List<LogStatement> parseLogStatemens(InputStream input) {
		List<LogStatement> statements = new LinkedList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

			String line = reader.readLine();
			LogStatement currentLog = null;

			while (line != null) {
				LogStatement logStatement = lineParser.parseLogStatement(line);
				if (logStatement != null) {
					statements.add(logStatement);
					currentLog = logStatement;

					line = reader.readLine();
					continue;
				}

				if (currentLog == null) {
					// TODO we have a problem here, just skipping line for now
					line = reader.readLine();
					continue;
				}

				CausedBy causedBy = lineParser.parseCausedBy(line, currentLog);
				if (causedBy != null) {
					currentLog.addCausedBy(causedBy);

					line = reader.readLine();
					continue;
				}

				if (currentLog != null) {
					AtLine atLine = lineParser.parseAtLine(line);
					if (atLine != null) {
						// assert that currentLog != null
						if (!currentLog.hasCausedBy()) {
							currentLog.addCausedBy(new CausedBy(null, null, true));
						}
						currentLog.firstCausedBy().addAtLine(atLine);

						line = reader.readLine();
						continue;
					}


					// othwerwise just extend the log message
					if (!lineParser.matchesMore(line)) {
						currentLog.addMultiLineMessage(line);
					}
				} else {
					// TODO what to do with such a line
				}

				line = reader.readLine();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return statements;
	}

	protected List<LogStatement> filterErrors(List<LogStatement> statements) {
		return statements.stream()
				.filter(log -> log.hasCausedBy())
				.collect(Collectors.toList());
	}

	protected Map<StackTraceKey, LogMessages> errorsWithSameOrigin(List<LogStatement> errors) {
		Map<StackTraceKey, LogMessages> result = new HashMap<>();

		for (LogStatement log : errors) {
			CausedBy causedBy = log.firstCausedBy();

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
