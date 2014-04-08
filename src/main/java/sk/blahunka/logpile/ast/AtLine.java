package sk.blahunka.logpile.ast;

public class AtLine {

	private final Clazz clazz;
	private final String method;
	private final String source;

	public AtLine(Clazz clazz, String method, String source) {
		this.clazz = clazz;
		this.method = method;
		this.source = source;
	}

	public String getReferenceRepresentation() {
		return clazz.getName() + "#" + method + " (" + source + ")";
	}

	public Clazz getClazz() {
		return clazz;
	}

	public String getMethod() {
		return method;
	}

	public String getSource() {
		return source;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AtLine atLine = (AtLine) o;

		if (clazz != null ? !clazz.equals(atLine.clazz) : atLine.clazz != null) return false;
		if (method != null ? !method.equals(atLine.method) : atLine.method != null) return false;
		if (source != null ? !source.equals(atLine.source) : atLine.source != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		int result = clazz != null ? clazz.hashCode() : 0;
		result = 31 * result + (method != null ? method.hashCode() : 0);
		result = 31 * result + (source != null ? source.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "AtLine{" +
				"clazz=" + clazz +
				", method='" + method + '\'' +
				", source='" + source + '\'' +
				"} " + super.toString();
	}

}
