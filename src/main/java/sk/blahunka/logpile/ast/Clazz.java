package sk.blahunka.logpile.ast;

import java.util.regex.Pattern;

public class Clazz {

	private static final Pattern BY_DOTS =
			Pattern.compile("\\.");

	private final String name;

	public Clazz(String name) {
		this.name = name;
	}

	public String getSimpleName() {
		String[] strings = BY_DOTS.split(name);
		return strings[strings.length - 1];
	}

	public String getName() {
		return name;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Clazz clazz = (Clazz) o;

		if (name != null ? !name.equals(clazz.name) : clazz.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return name != null ? name.hashCode() : 0;
	}

	@Override
	public String toString() {
		return "Clazz{" +
				"name='" + name + '\'' +
				'}';
	}

}
