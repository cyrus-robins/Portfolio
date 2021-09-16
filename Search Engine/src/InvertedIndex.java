import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Creates and manipulates an inverted index object
 * 
 * @author Cyrus
 *
 */
public class InvertedIndex {
	/**
	 * the map that defines the inverted index
	 */
	private final TreeMap<String, TreeMap<String, TreeSet<Integer>>> index;

	/**
	 * The map that keeps track of the number of words at each location
	 */
	private final Map<String, Integer> counts;

	/**
	 * Constructor
	 */
	public InvertedIndex() {
		index = new TreeMap<String, TreeMap<String, TreeSet<Integer>>>();
		counts = new TreeMap<String, Integer>();
	}

	/**
	 * Adds elements to the inverted index if they do not yet exist in the index,
	 * and changes counts if a new highest position is found at a given location
	 * 
	 * @param element  the element to be added
	 * @param location the path in which the element was found
	 * @param position the position in the text file
	 * @return true if it was newly added, false if it already existed
	 */
	public boolean add(String element, String location, int position) {
		if (counts.getOrDefault(location, 0) < position) {
			counts.put(location, position);
		}
		index.putIfAbsent(element, new TreeMap<String, TreeSet<Integer>>());
		index.get(element).putIfAbsent(location, new TreeSet<Integer>());
		return index.get(element).get(location).add(position);
	}

	/**
	 * Adds all elements of the sub index into the inverted index
	 * 
	 * @param subIndex the inverted index that has a subset of all of the words in
	 *                 the inverted index
	 */
	public void addAll(InvertedIndex subIndex) {
		for (String word : subIndex.index.keySet()) {
			if (!this.index.containsKey(word)) {
				this.index.put(word, subIndex.index.get(word));
			} else {
				for (String location : subIndex.index.get(word).keySet()) {
					if (index.get(word).containsKey(location)) {
						index.get(word).get(location).addAll(subIndex.index.get(word).get(location));
					} else {
						index.get(word).putAll(subIndex.index.get(word));
					}
				}
			}
		}

		for (String location : subIndex.counts.keySet()) {
			int currPos = subIndex.counts.get(location);
			if (this.counts.getOrDefault(location, 0) < currPos) {
				this.counts.put(location, currPos);
			}
		}
	}

	/**
	 * Returns the number of keys in the inverted index
	 * 
	 * @return the number of elements in the map
	 */
	public int numElements() {
		return index.size();
	}

	/**
	 * Checks if a given element is already in the inverted index
	 * 
	 * @param element the element to check
	 * @return {@code true} if the element is in the inverted index
	 */
	public boolean contains(String element) {
		return index.containsKey(element);
	}

	/**
	 * Checks to see if an element at a given location already exists in the
	 * inverted index
	 * 
	 * @param element  a word in the index
	 * @param location a file path the word was found at
	 * @return true if the element with a given location exists in the inverted
	 *         index
	 */
	public boolean contains(String element, String location) {
		return contains(element) && index.get(element).containsKey(location);
	}

	/**
	 * Checks to see if a given element is already in the inverted index with a
	 * given location and position
	 * 
	 * @param element  a word in the index
	 * @param location a file path that word was found at
	 * @param position the position in the file the word was found at
	 * @return {@code true} if the element with the given location and position
	 *         already exist
	 */
	public boolean contains(String element, String location, int position) {
		return contains(element) && index.get(element).containsKey(location)
				&& index.get(element).get(location).contains(position);
	}

	/**
	 * Returns an unmodifiable collection of the keys of the inverted index
	 * 
	 * @return the unmodifiable collection of keys
	 */
	public Collection<String> getWords() {
		return Collections.unmodifiableCollection(index.keySet());
	}

	/**
	 * Returns an unmodifiable collection of the locations of a given key in the
	 * inverted index, or an empty set if no locations exist
	 * 
	 * @param element a word in the index
	 * @return the unmodifiable collection of locations for a given element
	 */
	public Collection<String> getLocations(String element) {
		if (index.containsKey(element)) {
			return Collections.unmodifiableCollection(index.get(element).keySet());
		}

		return Collections.emptySet();
	}

	/**
	 * @return the unmodifiable map of counts
	 */
	public Map<String, Integer> getCounts() {
		return Collections.unmodifiableMap(counts);
	}

	/**
	 * Returns an unmodifiable collection of the positions of a key in the inverted
	 * index, or an empty set if no such positions exist
	 * 
	 * @param element  a given word in the index
	 * @param location the file path that the element was found at
	 * @return the unmodifiable collection of positions that a given element in a
	 *         location exists in
	 */
	public Collection<Integer> getPositions(String element, String location) {
		if (!index.containsKey(element) || !index.get(element).containsKey(location)) {
			return Collections.emptySet();
		} else {
			return Collections.unmodifiableCollection(index.get(element).get(location));
		}
	}

	/**
	 * Writes the locations file with proper syntax including each path and the
	 * number of words found in those text files
	 * 
	 * @param locationsPath the path to write the locations and numbers to
	 * 
	 * @throws IOException
	 */
	public void locationsWriter(Path locationsPath) throws IOException {
		SimpleJsonWriter.asObject(counts, locationsPath);
	}

	/**
	 * Write the contents of the inverted index in JSON format to the given file
	 * location
	 * 
	 * @param indexPath the path that the JSON form of the inverted index will be
	 *                  written to
	 * 
	 * @throws IOException
	 */
	public void indexWriter(Path indexPath) throws IOException {
		SimpleJsonWriter.asNestedObject(this.index, indexPath);
	}

	/**
	 * A method to perform search operations with both exact and partial search
	 * 
	 * @param resultList the list of search results for the current query
	 * @param resultMap  the map of queries to their search results
	 * @param word       the current query word
	 * @throws IOException
	 */
	private void searchHelper(List<SearchResult> resultList, Map<String, SearchResult> resultMap, String word)

			throws IOException {

		for (String location : index.get(word).keySet()) {
			if (!resultMap.containsKey(location)) {
				SearchResult currResult = new SearchResult(location);
				resultMap.put(location, currResult);
				resultList.add(currResult);
			}

			resultMap.get(location).update(word);
		}

	}

	/**
	 * Utilizes exact searching to find search results for input queries
	 * 
	 * @param parsedQuery a cleaned and stemmed query to use for searching
	 * @return A list of search result objects that correspond to the search query
	 * @throws IOException
	 */
	public List<SearchResult> exactSearch(Set<String> parsedQuery) throws IOException {
		HashMap<String, SearchResult> resultMap = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> resultList = new ArrayList<>();

		for (String query : parsedQuery) {
			if (index.get(query) != null) {
				searchHelper(resultList, resultMap, query);
			}
		}
		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * Utilizes partial searching to find search results for input queries
	 * 
	 * @param parsedQuery a cleaned and stemmed query to use for searching
	 * @return A list of search result objects that correspond to the query string
	 * @throws IOException
	 */
	public List<SearchResult> partialSearch(Set<String> parsedQuery) throws IOException {
		HashMap<String, SearchResult> resultMap = new HashMap<String, SearchResult>();
		ArrayList<SearchResult> resultList = new ArrayList<>();

		for (String queryWord : parsedQuery) {
			for (String word : index.tailMap(queryWord).keySet()) {
				if (word.startsWith(queryWord)) {
					searchHelper(resultList, resultMap, word);
				} else {
					break;
				}
			}
		}
		Collections.sort(resultList);
		return resultList;
	}

	/**
	 * Generic search method to be called from Driver
	 * 
	 * @param queries the set of cleaned and stemmed queries to use
	 * @param exact   boolean to determine whether to do an exact or partial search
	 * @return a list of search result objects
	 * @throws IOException
	 */
	public List<SearchResult> search(Set<String> queries, boolean exact) throws IOException {
		if (exact) {
			return this.exactSearch(queries);
		} else {
			return this.partialSearch(queries);
		}
	}

	/**
	 * Instance of a search result at a given location
	 * 
	 * @author Cyrus
	 *
	 */
	public class SearchResult implements Comparable<SearchResult> {

		/**
		 * The location the search took place at
		 */
		private final String location;

		/**
		 * the number of instances of the query at the location
		 */
		private int queriesAtLocation;

		/**
		 * the ratio of queriesAtLocation to the total number of words at the location
		 */
		private double score;

		/**
		 * The constructor to be used if the query had a match at a location
		 * 
		 * @param location the location the query was found at
		 * @throws IOException
		 */
		public SearchResult(String location) throws IOException {
			this.location = location;
			this.queriesAtLocation = 0;
			this.score = 0;
		}

		/**
		 * Method to update the score and the queries at location parameter of this
		 * object
		 * 
		 * @param word the query word that was found while searching
		 */
		private void update(String word) {
			this.queriesAtLocation += index.get(word).get(this.location).size();
			this.score = (double) queriesAtLocation / counts.get(this.location);
		}

		/**
		 * @return the score
		 */
		public double getScore() {
			return score;
		}

		/**
		 * @return the number of queries at the location
		 */
		public int getQueriesAtLocation() {
			return queriesAtLocation;
		}

		/**
		 * @return the location of the search result
		 */
		public String getLocation() {
			return location;
		}

		@Override
		public int compareTo(SearchResult comparableResult) {
			if (this.score != comparableResult.getScore()) {
				return Double.compare(comparableResult.getScore(), this.score);
			} else if (this.queriesAtLocation != comparableResult.getQueriesAtLocation()) {
				return Integer.compare(comparableResult.getQueriesAtLocation(), this.queriesAtLocation);
			} else {
				return this.location.compareTo(comparableResult.getLocation());
			}
		}
	}
}
