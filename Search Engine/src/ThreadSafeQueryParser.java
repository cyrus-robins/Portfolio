import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * A thread-safe implementation of the query parser class
 * 
 * @author Cyrus
 *
 */
public class ThreadSafeQueryParser implements Parser {

	/**
	 * The queue of workers to use for multithreading
	 */
	private final WorkQueue queue;

	/**
	 * The map of query strings to a list of search results for that query
	 */
	private final Map<String, List<InvertedIndex.SearchResult>> allQueries;

	/**
	 * A thread safe inverted index to use to search through
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Constructor
	 * 
	 * @param index the ThreadSafeInvertedIndex to use
	 * @param queue the workqueue to use
	 */
	public ThreadSafeQueryParser(ThreadSafeInvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		this.allQueries = new TreeMap<String, List<InvertedIndex.SearchResult>>();
	}

	@Override
	public void parse(String line, boolean exact) throws IOException {
		Runnable r = () -> {
			TreeSet<String> parsedLine = Parser.parseLine(line);
			String joinedLine = String.join(" ", parsedLine);

			if (parsedLine.isEmpty()) {
				return;
			}

			synchronized (allQueries) {
				if (allQueries.containsKey(joinedLine)) {
					return;
				}
			}

			List<InvertedIndex.SearchResult> results = null;
			try {
				results = index.search(parsedLine, exact);
			} catch (IOException e) {
				System.out.println("There was an error producing search results!");
			}
			synchronized (allQueries) {
				this.allQueries.put(joinedLine, results);
			}
		};
		queue.execute(r);
	}

	@Override
	public void parse(Path queryPath, boolean exact) throws IOException {
		Parser.super.parse(queryPath, exact);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	@Override
	public void writeResultsToJson(Path resultsPath) throws IOException {
		synchronized (allQueries) {
			SimpleJsonWriter.writeQueries(resultsPath, this.allQueries, 0);
		}
	}
}
