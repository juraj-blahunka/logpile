package sk.blahunka.logpile.dto;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import sk.blahunka.logpile.ast.LogStatement;

import java.util.Collection;
import java.util.Set;

public class LogMessages {

	private final Multimap<String, LogStatement> byMessage = ArrayListMultimap.create();

	public void arrange(LogStatement log) {
		byMessage.put(log.getMessage(), log);
	}

	public int getNumberOfTotalLogMessages() {
		return byMessage.size();
	}

	public int getNumberOfUniqueLogMessages() {
		return byMessage.keySet().size();
	}

	public Set<String> getUniqueMessageStrings() {
		return byMessage.keySet();
	}

	public Collection<LogStatement> getLogStatements(String message) {
		return byMessage.get(message);
	}

	public int getNumberOfStatements(String message) {
		return getLogStatements(message).size();
	}

	public Collection<LogStatement> first10LogStatements(String message) {
		Collection<LogStatement> collection = byMessage.get(message);
		return Lists.newCopyOnWriteArrayList(Iterables.limit(collection, 10));
	}

}
