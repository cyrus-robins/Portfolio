import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Outputs several tree-based data structures in "pretty" JSON format where
 * newlines are used to separate elements, and nested elements are indented.
 *
 * Warning: This class is not thread-safe. If multiple threads access this class
 * concurrently, access must be synchronized externally.
 *
 * @author CS 212 Software Development
 * @author University of San Francisco
 * @version Spring 2019
 */
public class SimpleJsonWriter {

	/**
	 * The specified decimal format for the score
	 */
	public static final DecimalFormat FORMATTER = new DecimalFormat("0.00000000");

	/**
	 * Writes the elements as a pretty JSON array to file.
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 */
	public static void asArray(Collection<Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asArray(elements, writer, 0);
		}
	}

	/**
	 * Writes a collection line by line into a given writer with proper JSON
	 * formatting
	 * 
	 * @param elements the list of positions
	 * @param writer   the writer to use
	 * @param level    the initial indentation level
	 * @throws IOException
	 */
	public static void asArray(Collection<Integer> elements, Writer writer, int level) throws IOException {
		writer.write("[\n");
		if (!elements.isEmpty()) {
			Iterator<Integer> it = elements.iterator();
			indent(it.next(), writer, level + 1);
			while (it.hasNext()) {
				writer.write(",\n");
				indent(it.next(), writer, level + 1);
			}
		}
		writer.write("\n");
		indent("]", writer, level);
	}

	/**
	 * Writes the elements as a pretty JSON array.
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asMap(Map<String, ? extends Collection<Integer>> elements, Writer writer, int level)
			throws IOException {
		String curr;
		Set<String> keys = elements.keySet();
		if (keys.size() > 0) {
			Iterator<String> it = keys.iterator();
			curr = it.next();
			writer.write("\n");
			quote(curr, writer, level + 1);
			writer.write(": ");
			asArray(elements.get(curr), writer, level + 1);
			while (it.hasNext()) {
				curr = it.next();
				writer.write(",\n");
				quote(curr, writer, level + 1);
				writer.write(": ");
				asArray(elements.get(curr), writer, level + 1);
			}
		}
		indent("\n", writer, level);
	}

	/**
	 * Writes the elements as a simple JSON object to file
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asObject(Map<String, Integer> elements, Writer writer, int level) throws IOException {
		writer.write("{\n");
		Set<String> keys = elements.keySet();
		if (keys.size() > 0) {
			Iterator<String> it = keys.iterator();
			String currKey = it.next();
			quote(currKey, writer, level + 1);
			writer.write(": " + elements.get(currKey));
			while (it.hasNext()) {
				writer.write(",\n");
				currKey = it.next();
				quote(currKey, writer, level + 1);
				writer.write(": " + elements.get(currKey));
			}
			writer.write("\n}");
		}
	}

	/**
	 * Creates a writer to a given file path, and calls the method to write to that
	 * file
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 */
	public static void asObject(Map<String, Integer> elements, Path path) throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asObject(elements, writer, 0);
		}
	}

	/**
	 * Writes the elements as a nested simple JSON object to file
	 *
	 * @param elements the elements to write
	 * @param writer   the writer to use
	 * @param level    the initial indent level
	 * @throws IOException
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Writer writer,
			int level) throws IOException {
		writer.write("{\n");
		Set<String> keys = elements.keySet();
		if (keys.size() > 0) {
			Iterator<String> it = keys.iterator();
			String currKey = it.next();
			quote(currKey, writer, level + 1);
			writer.write(": {");
			asMap(elements.get(currKey), writer, level + 1);
			while (it.hasNext()) {
				indent("},\n", writer, 1);
				currKey = it.next();
				quote(currKey, writer, level + 1);
				writer.write(": {");
				asMap(elements.get(currKey), writer, level + 1);
			}
			indent("}\n", writer, level);
		}
		writer.write("}");
	}

	/**
	 * Creates a writer to a given file path and then calls a method to write the
	 * nested object in simple JSON format to that file
	 *
	 * @param elements the elements to write
	 * @param path     the file path to use
	 * @throws IOException
	 *
	 * 
	 */
	public static void asNestedObject(TreeMap<String, TreeMap<String, TreeSet<Integer>>> elements, Path path)
			throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
			asNestedObject(elements, writer, 0);
		}
	}

	/**
	 * A function to format and write to file the list of search results for a given
	 * query
	 * 
	 * @param result      the given search result to write
	 * @param writer
	 * @param finalResult a boolean to determine whether it is the final search
	 *                    result to be written
	 * @param level       indentation level
	 * @throws IOException
	 */
	public static void writeSearchResult(InvertedIndex.SearchResult result, Writer writer, boolean finalResult,
			int level) throws IOException {

		indent("{\n", writer, level);
		SimpleJsonWriter.quote("where", writer, level + 1);
		writer.write(": ");
		SimpleJsonWriter.quote(result.getLocation().toString(), writer);
		writer.write(",\n");
		SimpleJsonWriter.quote("count", writer, level + 1);
		writer.write(": " + Integer.toString(result.getQueriesAtLocation()) + ",\n");
		SimpleJsonWriter.quote("score", writer, level + 1);
		writer.write(": " + FORMATTER.format(result.getScore()));
		if (finalResult) {
			writer.write("\n");
			indent("}\n", writer, level);
		} else {
			writer.write("\n");
			indent("},\n", writer, level);
		}
	}

	/**
	 * A method to write the queries and call the nested methods to write the search
	 * results
	 * 
	 * @param resultsPath the path to write to
	 * @param queryMap    the map of queries to their search results
	 * @param level       indentation level
	 * @throws IOException
	 */
	public static void writeQueries(Path resultsPath, Map<String, List<InvertedIndex.SearchResult>> queryMap, int level)
			throws IOException {

		try (BufferedWriter writer = Files.newBufferedWriter(resultsPath, StandardCharsets.UTF_8)) {
			int counter = 0;
			writer.write("{\n");
			if (queryMap != null) {
				for (String query : queryMap.keySet()) {
					if (counter == queryMap.keySet().size() - 1) {
						SimpleJsonWriter.writePaths(query, writer, queryMap.get(query), true, level);
					} else {
						SimpleJsonWriter.writePaths(query, writer, queryMap.get(query), false, level);
						counter++;
					}
				}
			}
			writer.write("}");
		}
	}

	/**
	 * This function coordinates the writing of the search results to a file
	 * 
	 * @param query    The query string used for the search
	 * @param writer   the writer to use
	 * @param results  the list of results for that query
	 * @param lastItem
	 * @param level    indentation level
	 * @throws IOException
	 */
	public static void writePaths(String query, Writer writer, List<InvertedIndex.SearchResult> results,
			boolean lastItem, int level) throws IOException {

		SimpleJsonWriter.quote(query, writer, 1);
		writer.write(": [\n");
		if (results != null) {
			for (int i = 0; i < results.size(); i++) {
				if (i == results.size() - 1) {
					SimpleJsonWriter.writeSearchResult(results.get(i), writer, true, level + 2);
				} else {
					SimpleJsonWriter.writeSearchResult(results.get(i), writer, false, level + 2);
				}
			}
			if (lastItem) {
				indent("]\n", writer, level + 1);
			} else {
				indent("],\n", writer, level + 1);
			}
		}
	}

	/**
	 * Writes the {@code \t} tab symbol by the number of times specified.
	 *
	 * @param writer the writer to use
	 * @param times  the number of times to write a tab symbol
	 * @throws IOException
	 */
	public static void indent(Writer writer, int times) throws IOException {
		for (int i = 0; i < times; i++) {
			writer.write('\t');
		}
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(String, Writer, int)
	 * @see #indent(Writer, int)
	 */
	public static void indent(Integer element, Writer writer, int times) throws IOException {
		indent(element.toString(), writer, times);
	}

	/**
	 * Indents and then writes the element.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 */
	public static void indent(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		writer.write(element);
	}

	/**
	 * Writes the element surrounded by a{@code " "} quotation marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @throws IOException
	 */
	public static void quote(String element, Writer writer) throws IOException {
		writer.write('"');
		writer.write(element);
		writer.write('"');
	}

	/**
	 * Indents and then writes the element surrounded by {@code " "} quotation
	 * marks.
	 *
	 * @param element the element to write
	 * @param writer  the writer to use
	 * @param times   the number of times to indent
	 * @throws IOException
	 *
	 * @see #indent(Writer, int)
	 * @see #quote(String, Writer)
	 */
	public static void quote(String element, Writer writer, int times) throws IOException {
		indent(writer, times);
		quote(element, writer);
	}
}
