import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Parses URL links from the anchor tags within HTML text.
 */
public class LinkParser {

	/**
	 * Removes the fragment component of a URL (if present), and properly encodes
	 * the query string (if necessary).
	 *
	 * @param url the url to clean
	 * @return cleaned url (or original url if any issues occurred)
	 */
	public static URL clean(URL url) {
		try {
			return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(),
					url.getQuery(), null).toURL();
		} catch (MalformedURLException | URISyntaxException e) {
			return url;
		}
	}

	/**
	 * Returns a list of all the HTTP(S) links found in the href attribute of the
	 * anchor tags in the provided HTML. The links will be converted to absolute
	 * using the base URL and cleaned (removing fragments and encoding special
	 * characters as necessary).
	 *
	 * @param base     the base url used to convert relative links to absolute3
	 * @param html     the raw html associated with the base url
	 * @param limit    The maximum number of links that should be returned
	 * @param usedUrls The set of URLs already used
	 * @return cleaned list of all http(s) links in the order they were found
	 * @throws MalformedURLException
	 */
	public static ArrayList<URL> listLinks(URL base, String html, int limit, Set<URL> usedUrls)
			throws MalformedURLException {
		ArrayList<URL> links = new ArrayList<URL>();
		Pattern pattern = Pattern.compile("(?i)<\\s*a\\s*[^>]*href\\s*=\\s*\"([a-z0-9:/.?=\\_-]*)[^\"]*\"\\s*[^>]*>");
		Matcher matcher = pattern.matcher(html);
		Stream<MatchResult> results = matcher.results();
		Iterator<MatchResult> it = results.iterator();
		int counter = 0;

		while (it.hasNext() && counter < limit) {
			String str = it.next().group(1);
			URL absolute = new URL(base, str);
			URL cleanUrl = clean(absolute);
			if (!usedUrls.contains(cleanUrl)) {
				usedUrls.add(cleanUrl);
				links.add(cleanUrl);
				counter++;
			}
		}
		return links;
	}
}