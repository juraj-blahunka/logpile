package sk.blahunka.logpile.dto;

/**
 * Work in progress.
 * Planned to use for line numbering of the whole stacktrace.
 */
public class FullStackTrace {

	private final String lines;
	private final int numberOfLines;

	public FullStackTrace(String lines, int numberOfLines) {
		this.lines = lines;
		this.numberOfLines = numberOfLines;
	}

	public String getLines() {
		return lines;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	@Override
	public String toString() {
		return "FullStackTrace{" +
				"lines='" + lines + '\'' +
				", numberOfLines=" + numberOfLines +
				"} " + super.toString();
	}

}
