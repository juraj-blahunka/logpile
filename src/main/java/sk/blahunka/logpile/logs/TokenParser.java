package sk.blahunka.logpile.logs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.blahunka.logpile.logs.token.AtLine;
import sk.blahunka.logpile.logs.token.CausedBy;
import sk.blahunka.logpile.logs.token.LogStatement;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * A new instance of {@link TokenParser} should be created with every parsed stream.
 * A token is every line of log file (AtLine, CausedBy, LogStatement)
 */
public class TokenParser {

	private static final Logger LOG = LoggerFactory.getLogger(TokenParser.class);

	private LineParser lineParser = new LineParser();

	private Tokenizer tokenizer;

	public List<LogStatement> parseLogStatemens(InputStream input) {

		List<LogStatement> result = new ArrayList<>();

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {

			tokenizer = new Tokenizer(reader);

			repeatUntilFalse(() -> parseLogStatement(result));

		} catch (EndOfStreamException e) {
			// do nothing
		} catch (IOException e) {
			throw new RuntimeException("Can't tokenize the stream", e);
		} finally {
			tokenizer.clear();
		}

		return result;
	}

	/**
	 * Calls passed in block repeatedly until <code>false</code> is returned.
	 */
	private void repeatUntilFalse(Supplier<Boolean> block) {
		while (block.get()) {
			// do nothing, just repeat until 'false'
		}
	}

	private boolean parseLogStatement(List<LogStatement> result) {
		LogStatement logStatement = lineParser.parseLogStatement(tokenizer.peekNextLine());

		if (logStatement != null) {
			result.add(logStatement);
			tokenizer.advanceNextLine();

			// sometimes there is "at line" before "caused by".. if there is an artificial "caused by" is created
			repeatUntilFalse(() -> parseAtLine(logStatement));

			repeatUntilFalse(() -> parseCausedBy(logStatement));

		} else {
			if (result.size() > 0) {
				LogStatement lastLogStatement = result.get(result.size() - 1);
				lastLogStatement.addMultiLineMessage(tokenizer.peekNextLine());
			} else {
				LOG.warn("Uknown line detected: {}", tokenizer.peekNextLine());
			}
			tokenizer.advanceNextLine();
		}

		return true;
	}

	private boolean parseCausedBy(LogStatement logStatement) {
		CausedBy causedBy = lineParser.parseCausedBy(tokenizer.peekNextLine(), logStatement);
		if (causedBy != null) {
			tokenizer.advanceNextLine();

			logStatement.addCausedBy(causedBy);

			repeatUntilFalse(() -> parseAtLine(logStatement));

			parseMoreLine();

			return true;
		}

		return false;
	}

	private boolean parseAtLine(LogStatement logStatement) {
		AtLine atLine = lineParser.parseAtLine(tokenizer.peekNextLine());
		if (atLine != null) {
			tokenizer.advanceNextLine();

			// when there was no caused by, just "at line" is found, we add an empty caused by
			if (!logStatement.hasCausedBy()) {
				logStatement.addCausedBy(new CausedBy(null, null, true));
			}
			logStatement.lastCausedBy().addAtLine(atLine);
			return true;
		}
		return false;
	}

	private boolean parseMoreLine() {
		if (lineParser.matchesMore(tokenizer.peekNextLine())) {
			tokenizer.advanceNextLine();
			return true;
		}
		return false;
	}

	/**
	 * Used for navigation inside the token stream (lines).
	 */
	private static class Tokenizer {

		private BufferedReader reader;

		private String current;
		private String next;

		Tokenizer(BufferedReader reader) {
			this.reader = reader;
		}

		void clear() {
			reader = null;
			current = null;
			next = null;
		}

		String advanceNextLine() {
			if (next != null) {
				current = next;
				next = null;
			} else {
				current = readNextLine();
			}

			return current;
		}

		String peekNextLine() {
			if (next == null) {
				next = readNextLine();
			}
			return next;
		}

		private String readNextLine() {
			try {
				String line = reader.readLine();
				if (line == null) {
					throw new EndOfStreamException();
				}
				return line.trim();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Terminates reading of stream / file.
	 */
	private static class EndOfStreamException extends RuntimeException {
	}

}
