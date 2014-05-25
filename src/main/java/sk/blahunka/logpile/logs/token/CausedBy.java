package sk.blahunka.logpile.logs.token;

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
		if (atLines.size() < MAX_TRACES) {
			atLines.add(atLine);
		}
	}

	public void appendReferenceRepresentation(StringBuilder sb) {
		if (!topLevelException) {
			sb.append("Caused by: ");
		}
		sb.append(exceptionClazz.getFullyQualifiedName());
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		CausedBy causedBy = (CausedBy) o;

		if (topLevelException != causedBy.topLevelException) return false;
		if (atLines != null ? !atLines.equals(causedBy.atLines) : causedBy.atLines != null) return false;
		if (exceptionClazz != null ? !exceptionClazz.equals(causedBy.exceptionClazz) : causedBy.exceptionClazz != null)
			return false;
		if (message != null ? !message.equals(causedBy.message) : causedBy.message != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = exceptionClazz != null ? exceptionClazz.hashCode() : 0;
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (topLevelException ? 1 : 0);
		result = 31 * result + (atLines != null ? atLines.hashCode() : 0);
		return result;
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
