import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import opennlp.tools.stemmer.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * @author Cyrus
 *
 *         Class to open files, clean and parse each line, then feed them back
 *         into the inverted index
 */
public class IndexBuilder {

	/** The default stemmer algorithm used by this class. */
	public static final SnowballStemmer.ALGORITHM DEFAULT = SnowballStemmer.ALGORITHM.ENGLISH;

	/**
	 * The inverted index that words will be parsed into
	 */
	private final InvertedIndex index;

	/**
	 * Constructor
	 * 
	 * @param index a pointer to the inverted index to use for storage
	 */
	public IndexBuilder(InvertedIndex index) {
		this.index = index;
	}

	/**
	 * Reads a file line by line, parses each line into cleaned and stemmed words,
	 * and then adds those words to a set.
	 *
	 * @param inputFile the input file to parse
	 * @param index     the inverted index to stem into
	 * @throws IOException if unable to read or parse file
	 *
	 * @see TextParser#parse(String)
	 */
	public static void stemFile(Path inputFile, InvertedIndex index) throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(inputFile, StandardCharsets.UTF_8)) {
			int posCounter = 1;
			String currLine;
			Stemmer stemmer = new SnowballStemmer(DEFAULT);
			String location = inputFile.toString();
			while ((currLine = reader.readLine()) != null) {
				posCounter = parseHelper(stemmer, location, currLine, index, posCounter);
			}
		}
	}

	/**
	 * A non-static implementation of stemfile
	 * 
	 * @param path the file path
	 * @throws IOException
	 */
	public void stemFile(Path path) throws IOException {
		stemFile(path, index);
	}

	/**
	 * An assisting method to stemFile to help the parsing
	 * 
	 * @param stemmer    the stemmer to use
	 * @param location   the location of the file
	 * @param line       the line of text to parse
	 * @param index      the index to add into
	 * @param posCounter the current position for the word
	 * @return the new position that the line got up to
	 */
	public static int parseHelper(Stemmer stemmer, String location, String line, InvertedIndex index, int posCounter) {
		String[] cleanLine = TextParser.parse(line);
		for (String word : cleanLine) {
			index.add(stemmer.stem(word).toString(), location, posCounter);
			posCounter++;
		}
		return posCounter;
	}

	/**
	 * method used to follow a path and stem each file found
	 * 
	 * @param path the file path
	 * @throws IOException
	 */
	public void processPath(Path path) throws IOException {
		if (Files.isDirectory(path)) {
			List<Path> fileList = TextFileFinder.list(path);
			for (Path p : fileList) {
				stemFile(p);
			}
		} else if (TextFileFinder.TEXT_EXT.test(path)) {
			stemFile(path);
		}
	}

}
