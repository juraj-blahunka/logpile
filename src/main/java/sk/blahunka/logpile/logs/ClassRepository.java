package sk.blahunka.logpile.logs;

import sk.blahunka.logpile.logs.token.Clazz;

import java.util.HashMap;
import java.util.Map;

public class ClassRepository {

	private final Map<String, Clazz> byName = new HashMap<>();

	public Clazz cachedClazz(String name) {
		Clazz clazz = byName.get(name);
		if (clazz == null) {
			clazz = new Clazz(name);
			byName.put(name, clazz);
		}
		return clazz;
	}

}
