import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import opennlp.tools.stemmer.snowball.SnowballStemmer;

/**
 * A class that navigates the web and parses text into an inverted index
 * 
 * @author Cyrus
 *
 */
public class WebCrawler {

	/**
	 * The maximum number of redirects permissible
	 */
	private final int MAX_REDIRECTS = 3;

	/**
	 * The index to parse into
	 */
	private InvertedIndex index;

	/**
	 * The workqueue to use for multithreading
	 */
	private WorkQueue queue;
	/**
	 * The number of URLs that have been accessed
	 */
	private int count;
	/**
	 * A set of all URLs navigated to avoid going back to an already accessed URL
	 */
	private Set<URL> usedURLs;

	/**
	 * Constructor
	 * 
	 * @param index the index to use
	 * @param queue the workqueue to use
	 */
	public WebCrawler(InvertedIndex index, WorkQueue queue) {
		this.index = index;
		this.queue = queue;
		count = 0;
		usedURLs = new HashSet<URL>();
	}

	/**
	 * A method to begin processing URLs and then finish the queue
	 * 
	 * @param url
	 * @param limit
	 */
	public void buildIndex(URL url, int limit) {
		processURL(url, limit);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			System.out.println("The thread was interrupted while building!");
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * A method to access a URL, clean and parse the html, and then add that html to
	 * the inverted index
	 * 
	 * @param url   the base URL to start from
	 * @param limit the maximum number of URLs that should be traversed when
	 *              following links
	 */
	public void processURL(URL url, int limit) {
		if (count < limit) {
			usedURLs.add(url);
			Runnable r = () -> {
				String html = HtmlFetcher.fetch(url, MAX_REDIRECTS);
				html = HtmlCleaner.stripBlockElements(html);
				ArrayList<URL> linkList = new ArrayList<>();
				try {
					synchronized (usedURLs) {
						linkList = LinkParser.listLinks(url, html, limit - count + 1, usedURLs);
					}
				} catch (MalformedURLException e) {
					System.out.println("URL: " + url.toString() + " had an error while forming");
				}
				html = HtmlCleaner.stripHtml(html);
				ThreadSafeIndexBuilder.parseHelper(new SnowballStemmer(IndexBuilder.DEFAULT), url.toString(), html,
						index, 1);
				for (URL currUrl : linkList) {
					count++;
					processURL(currUrl, limit);
				}
			};
			queue.execute(r);
		}
	}
}