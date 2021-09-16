import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A thread-safe implementation of an inverted index
 * 
 * @author Cyrus
 *
 */
public class ThreadSafeInvertedIndex extends InvertedIndex {

	/**
	 * The lock object we will use to make this thread-safe
	 */
	private final ReadWriteLock lock;

	/**
	 * Constructor
	 * 
	 */
	public ThreadSafeInvertedIndex() {
		super();
		lock = new ReadWriteLock();
	}

	@Override
	public boolean add(String element, String location, int position) {
		lock.writeLock().lock();
		try {
			return super.add(element, location, position);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public void addAll(InvertedIndex subIndex) {
		lock.writeLock().lock();
		try {
			super.addAll(subIndex);
		} finally {
			lock.writeLock().unlock();
		}
	}

	@Override
	public int numElements() {
		lock.readLock().lock();
		try {
			return super.numElements();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String element) {
		lock.readLock().lock();
		try {
			return super.contains(element);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String element, String location) {
		lock.readLock().lock();
		try {
			return super.contains(element, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public boolean contains(String element, String location, int position) {
		lock.readLock().lock();
		try {
			return super.contains(element, location, position);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Collection<String> getWords() {
		lock.readLock().lock();
		try {
			return super.getWords();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Collection<String> getLocations(String element) {
		lock.readLock().lock();
		try {
			return super.getLocations(element);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Map<String, Integer> getCounts() {
		lock.readLock().lock();
		try {
			return super.getCounts();
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public Collection<Integer> getPositions(String element, String location) {
		lock.readLock().lock();
		try {
			return super.getPositions(element, location);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void locationsWriter(Path locationsPath) throws IOException {
		lock.readLock().lock();
		try {
			super.locationsWriter(locationsPath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public void indexWriter(Path indexPath) throws IOException {
		lock.readLock().lock();
		try {
			super.indexWriter(indexPath);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<InvertedIndex.SearchResult> exactSearch(Set<String> parsedQuery) throws IOException {
		lock.readLock().lock();
		try {
			return super.exactSearch(parsedQuery);
		} finally {
			lock.readLock().unlock();
		}
	}

	@Override
	public List<InvertedIndex.SearchResult> partialSearch(Set<String> parsedQuery) throws IOException {
		lock.readLock().lock();
		try {
			return super.partialSearch(parsedQuery);
		} finally {
			lock.readLock().unlock();
		}
	}
}