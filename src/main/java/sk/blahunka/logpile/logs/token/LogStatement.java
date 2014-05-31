package sk.blahunka.logpile.logs.token;

import sk.blahunka.logpile.dto.FullStackTrace;

import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Usage is very general (can be DEBUG, INFO, ERROR), but has all the attributes necessary for ERROR.
 * It's the root collection of this package.
 */
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

	public CausedBy lastCausedBy() {
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

	public void addMultiLineMessage(String message) {
		this.message += "\n" + message;
	}

	// TODO not finished and used yet
	public FullStackTrace getFullStackTrace() {
		StringBuilder sb = new StringBuilder();
		String ls = System.lineSeparator();

		sb.append(time).append(" ").append(level)
				.append(" (").append(clazz.getFullyQualifiedName()).append(")")
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		LogStatement that = (LogStatement) o;

		if (causedBies != null ? !causedBies.equals(that.causedBies) : that.causedBies != null) return false;
		if (clazz != null ? !clazz.equals(that.clazz) : that.clazz != null) return false;
		if (lastCausedBy != null ? !lastCausedBy.equals(that.lastCausedBy) : that.lastCausedBy != null) return false;
		if (level != that.level) return false;
		if (message != null ? !message.equals(that.message) : that.message != null) return false;
		if (time != null ? !time.equals(that.time) : that.time != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = time != null ? time.hashCode() : 0;
		result = 31 * result + (level != null ? level.hashCode() : 0);
		result = 31 * result + (clazz != null ? clazz.hashCode() : 0);
		result = 31 * result + (message != null ? message.hashCode() : 0);
		result = 31 * result + (lastCausedBy != null ? lastCausedBy.hashCode() : 0);
		result = 31 * result + (causedBies != null ? causedBies.hashCode() : 0);
		return result;
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
