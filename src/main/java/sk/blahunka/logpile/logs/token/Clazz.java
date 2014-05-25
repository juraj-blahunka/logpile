package sk.blahunka.logpile.logs.token;

import java.util.regex.Pattern;

public class Clazz {

	private static final Pattern BY_DOTS =
			Pattern.compile("\\.");

	private final String fullyQualifiedName;

	public Clazz(String fullyQualifiedName) {
		this.fullyQualifiedName = fullyQualifiedName;
	}

	public String getSimpleName() {
		String[] strings = BY_DOTS.split(fullyQualifiedName);
		return strings[strings.length - 1];
	}

	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Clazz clazz = (Clazz) o;

		if (fullyQualifiedName != null ? !fullyQualifiedName.equals(clazz.fullyQualifiedName) : clazz.fullyQualifiedName != null)
			return false;

		return true;
	}

	@Override
	public int hashCode() {
		return fullyQualifiedName != null ? fullyQualifiedName.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Clazz{" +
				"name='" + fullyQualifiedName + '\'' +
				'}';
	}

}
