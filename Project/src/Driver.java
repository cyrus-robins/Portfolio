import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

/**
 * Class responsible for running this project based on the provided command-line
 * arguments. See the README for details.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2019
 */
public class Driver {

	/**
	 * TreeSet of all valid flag values for this program
	 */
	public static final Set<String> flags = Set.of("-path", "-index", "-counts", "-query", "-results", "-exact",
			"-threads", "-url", "-limit");

	/**
	 * Initializes the classes necessary based on the provided command-line
	 * arguments. This includes (but is not limited to) how to build or search an
	 * inverted index.
	 *
	 * @param args flag/value pairs used to start this program
	 */
	public static void main(String[] args) {
		Instant start = Instant.now();
		boolean exactSearch = false;
		int limit = 50;

		ArgumentParser map = new ArgumentParser(args, flags);

		InvertedIndex index;
		IndexBuilder builder;
		Parser parser;
		WebCrawler crawler;
		String url;

		WorkQueue queue = null;

		if (map.hasFlag("-threads") || map.hasFlag("-url")) {
			int numThreads = 5;
			if (map.hasFlag("-threads")) {
				numThreads = -1;
				try {
					numThreads = Integer.parseInt(map.getString("-threads", "5"));
				} catch (NumberFormatException e) {
					System.out.println("That is not a valid integer!");
					numThreads = 5;
				}
				if (numThreads < 1) {
					numThreads = 5;
				}
			}
			queue = new WorkQueue(numThreads);
			ThreadSafeInvertedIndex threadSafeIndex = new ThreadSafeInvertedIndex();
			index = threadSafeIndex;
			builder = new ThreadSafeIndexBuilder(threadSafeIndex, queue);
			parser = new ThreadSafeQueryParser(threadSafeIndex, queue);
		}

		else {
			index = new InvertedIndex();
			builder = new IndexBuilder(index);
			parser = new QueryParser(index);
		}

		if (map.hasFlag("-path") && map.getPath("-path") != null) {
			try {
				builder.processPath(map.getPath("-path"));
			} catch (IOException e) {
				System.out.println("Unable to process path: " + map.getPath("-path"));
			}
		}

		if (map.hasFlag("-url")) {
			crawler = new WebCrawler(index, queue);
			url = map.getString("-url");
			if (map.hasFlag("-limit")) {
				try {
					limit = Integer.parseInt(map.getString("-limit"));
				} catch (NumberFormatException e) {
					limit = 50;
				}
			}
			try {
				crawler.buildIndex(new URL(url), limit);
			} catch (MalformedURLException e) {
				System.out.println("There was an error forming the URL");
			}
		}

		if (map.hasFlag("-index")) {
			try {
				index.indexWriter(map.getPath("-index", Path.of("index.json")));
			} catch (IOException e) {
				System.out.println("The inverted Index was unable to be written in JSON format to: index.json");
			}

		}

		if (map.hasFlag("-counts")) {
			try {
				index.locationsWriter(map.getPath("-counts"));
			} catch (IOException e) {
				System.out.println("There was an error when writing the locations file!");
			}
		}

		if (map.hasFlag("-exact")) {
			exactSearch = true;
		}

		if (map.hasFlag("-query") && map.getPath("-query") != null) {
			try {
				parser.parse(map.getPath("-query"), exactSearch);
			} catch (IOException e) {
				System.out.print("There was an error in producing your search results from: " + map.getPath("-query"));
			}
		}

		if (map.hasFlag("-results")) {
			try {
				parser.writeResultsToJson(map.getPath("-results", Path.of("results.json")));
			} catch (IOException e) {
				System.out.println("There was an error writing your results to "
						+ map.getPath("-results", Path.of("results.json")));
			}
		}

		if (queue != null) {
			queue.shutdown();
		}

		Duration elapsed = Duration.between(start, Instant.now());
		double seconds = (double) elapsed.toMillis() / Duration.ofSeconds(1).toMillis();
		System.out.printf("Elapsed: %f seconds%n", seconds);
	}

}