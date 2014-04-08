package sk.blahunka.logpile.ast;

import org.joda.time.LocalTime;
import sk.blahunka.logpile.dto.FullStackTrace;

import java.util.LinkedList;
import java.util.List;

public class LogStatement {

	private final LocalTime time;

	private final Level level;

	private final Clazz clazz;

	private String message;

	private CausedBy lastCausedBy;

	private List<CausedBy> causedBies;

	public LogStatement(LocalTime time, Level level, Clazz clazz, String message) {
		this.time = time;
		this.level = level;
		this.clazz = clazz;
		this.message = message;
	}

	public boolean hasCausedBy() {
		return lastCausedBy != null;
	}

	public CausedBy firstCausedBy() {
		if (lastCausedBy == null) {
			throw new IllegalStateException("No CausedBy element was inserted, can't retrieve last");
		}
		return lastCausedBy;
	}

	public void addCausedBy(CausedBy causedBy) {
		if (causedBies == null) {
			causedBies = new LinkedList<>();
		}
		lastCausedBy = causedBy;
		causedBies.add(causedBy);
	}

	// TODO verify this works correctly
	public void addMultiLineMessage(String message) {
		this.message += "\n" + message;
	}

	public FullStackTrace getFullStackTrace() {
		StringBuilder sb = new StringBuilder();
		String ls = System.lineSeparator();

		sb.append(time).append(" ").append(level)
				.append(" (").append(clazz.getName()).append(")")
				.append(" ").append(message)
				.append(ls);

		for (CausedBy causedBy : causedBies) {

			if (causedBy.getExceptionClazz() != null) {
				causedBy.appendReferenceRepresentation(sb);
				sb.append(ls);
			}

			for (AtLine atLine : causedBy.getAtLines()) {
				sb.append("\tat ").append(atLine.getReferenceRepresentation()).append(ls);
			}
		}

		String stackTrace = sb.toString();
		int numberOfLines = stackTrace.split(ls).length;

		return new FullStackTrace(stackTrace, numberOfLines);
	}

	public LocalTime getTime() {
		return time;
	}

	public Level getLevel() {
		return level;
	}

	public Clazz getClazz() {
		return clazz;
	}

	public String getMessage() {
		return message;
	}

	public List<CausedBy> getCausedBies() {
		return causedBies;
	}

	@Override
	public String toString() {
		return "LogStatement{" +
				"time=" + time +
				", level=" + level +
				", clazz='" + clazz + '\'' +
				", message='" + message + '\'' +
				", causedBies=" + causedBies +
				'}';
	}

}