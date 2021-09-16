import java.io.IOException;
import java.nio.file.Path;

/**
 * A thread-safe implementation of IndexBuilder
 * 
 * @author Cyrus
 *
 */
public class ThreadSafeIndexBuilder extends IndexBuilder {

	/**
	 * The queue to use for multithreading
	 */
	private final WorkQueue queue;

	/**
	 * A thread safe inverted index to use in place of the regular inverted index
	 * available in the superclass
	 */
	private final ThreadSafeInvertedIndex index;

	/**
	 * Constructor
	 * 
	 * @param index a pointer to the inverted index to use for storage
	 * @param queue The queue to use
	 */
	public ThreadSafeIndexBuilder(ThreadSafeInvertedIndex index, WorkQueue queue) {
		super(index);
		this.queue = queue;
		this.index = index;
	}

	@Override
	public void stemFile(Path path) throws IOException {
		Runnable r = () -> {
			InvertedIndex local = new InvertedIndex();
			try {
				stemFile(path, local);
			} catch (IOException e) {
				System.out.println("There was an error stemming the file at: " + path.toString());
			}
			index.addAll(local);
		};
		queue.execute(r);
	}

	@Override
	public void processPath(Path path) throws IOException {
		super.processPath(path);
		try {
			queue.finish();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

}
