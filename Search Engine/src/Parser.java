import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.TreeSet;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * An interface for all types of parsers
 * 
 * @author Cyrus
 *
 */
public interface Parser {

	/**
	 * Cleans, stems and parses a single line of text
	 * 
	 * @param line  the line to parse
	 * @param exact whether an exact or partial search will be performed with the
	 *              queries
	 * @throws IOException
	 */
	public void parse(String line, boolean exact) throws IOException;

	/**
	 * Cleans, stems and parsers a file
	 * 
	 * @param queryPath the path to the file
	 * @param exact     whether an exact or partial search will be performed
	 * @throws IOException
	 */
	public default void parse(Path queryPath, boolean exact) throws IOException {
		String currLine;
		try (BufferedReader b = Files.newBufferedReader(queryPath, StandardCharsets.UTF_8)) {
			while ((currLine = b.readLine()) != null) {
				parse(currLine, exact);
			}
		}
	}

	/**
	 * Stems and cleans a line
	 * 
	 * @param line the line to parse
	 * @return the set of cleaned and stemmed strings
	 */
	public static TreeSet<String> parseLine(String line) {
		Stemmer stemmer = new SnowballStemmer(IndexBuilder.DEFAULT);
		TreeSet<String> query = new TreeSet<String>();
		String[] parsedLine = TextParser.parse(line);
		for (String word : parsedLine) {
			String stemmedWord = stemmer.stem(word).toString();
			query.add(stemmedWord);
		}
		return query;
	}

	/**
	 * Writes all of the search results to file
	 * 
	 * @param resultsPath the file to write to
	 * @throws IOException
	 */
	public void writeResultsToJson(Path resultsPath) throws IOException;
}
