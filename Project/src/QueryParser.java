import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A class to parse, clean, and stem queries into just the unique stems
 * 
 * @author Cyrus
 *
 */
public class QueryParser implements Parser {

	/**
	 * The map of all queries with their search results
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> allQueries;

	/**
	 * The inverted index to search through while
	 */
	private final InvertedIndex index;

	/**
	 * Constructor
	 * 
	 * @param index The index to search through
	 */
	public QueryParser(InvertedIndex index) {
		this.allQueries = new TreeMap<String, List<InvertedIndex.SearchResult>>();
		this.index = index;
	}

	@Override
	public void parse(String line, boolean exact) throws IOException {
		TreeSet<String> parsedLine = Parser.parseLine(line);

		if (parsedLine.isEmpty()) {
			return;
		}

		String joinedLine = String.join(" ", parsedLine);

		if (allQueries.containsKey(joinedLine)) {
			return;
		}

		List<InvertedIndex.SearchResult> results = index.search(parsedLine, exact);
		this.allQueries.put(joinedLine, results);
	}

	@Override
	public void writeResultsToJson(Path resultsPath) throws IOException {
		SimpleJsonWriter.writeQueries(resultsPath, this.allQueries, 0);
	}
}