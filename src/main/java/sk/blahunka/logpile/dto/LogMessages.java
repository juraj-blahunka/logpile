package sk.blahunka.logpile.dto;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import sk.blahunka.logpile.logs.token.LogStatement;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A mapping of Message -> List(LogStatements)
 */
public class LogMessages {

	private final Multimap<String, LogStatement> logsByMessage = ArrayListMultimap.create();

	public void put(LogStatement log) {
		logsByMessage.put(log.getMessage(), log);
	}

	public int getNumberOfTotalLogMessages() {
		return logsByMessage.size();
	}

	public int getNumberOfUniqueLogMessages() {
		return logsByMessage.keySet().size();
	}

	public Set<String> getUniqueMessageStrings() {
		return logsByMessage.keySet();
	}

	public Collection<LogStatement> getLogStatements(String message) {
		return logsByMessage.get(message);
	}

	public int getNumberOfStatements(String message) {
		return getLogStatements(message).size();
	}

	public Collection<LogStatement> first10LogStatements(String message) {
		return logsByMessage.get(message).stream()
				.limit(10)
				.collect(Collectors.toList());
	}

}
