package sk.blahunka.logpile.ast;

import com.google.common.base.Strings;

import java.util.LinkedList;
import java.util.List;

public class CausedBy {

	private static final int MAX_TRACES = 3;

	private final Clazz exceptionClazz;
	private final String message;
	private final boolean topLevelException;
	private List<AtLine> atLines = new LinkedList<>();

	public CausedBy(Clazz exceptionClazz, String message, boolean topLevelException) {
		this.exceptionClazz = exceptionClazz;
		this.message = message;
		this.topLevelException = topLevelException;
	}

	public void addAtLine(AtLine atLine) {
		if (atLines.size() <= MAX_TRACES) {
			atLines.add(atLine);
		}
	}

	public void appendReferenceRepresentation(StringBuilder sb) {
		if (!topLevelException) {
			sb.append("Caused by: ");
		}
		sb.append(exceptionClazz.getName());
		if (!Strings.isNullOrEmpty(message)) {
			sb.append(": ").append(message);
		}
	}

	public List<AtLine> getAtLines() {
		return atLines;
	}

	public Clazz getExceptionClazz() {
		return exceptionClazz;
	}

	public String getMessage() {
		return message;
	}

	public boolean isTopLevelException() {
		return topLevelException;
	}

	@Override
	public String toString() {
		return "CausedBy{" +
				"exceptionClazz=" + exceptionClazz +
				", message='" + message + '\'' +
				", topLevelException=" + topLevelException +
				", atLines=" + atLines +
				'}';
	}

}
