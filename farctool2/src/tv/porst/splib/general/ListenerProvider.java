package tv.porst.splib.general;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Class that provides a generic interface for working with listeners.
 *
 * @param <ListenerType> Type of the listeners managed by the class.
 */
public final class ListenerProvider<ListenerType> implements Iterable<ListenerType> {

	/**
	 * Currently added listeners.
	 */
	private final List<ListenerType> listeners = new ArrayList<ListenerType>();

	/**
	 * Adds a new listener to the provider.
	 * 
	 * @param listener The new listener object to add.
	 */
	public void add(final ListenerType listener) {
		listeners.add(listener);
	}

	@Override
	public Iterator<ListenerType> iterator() {
		return listeners.iterator();
	}

	/**
	 * Removes a listener from the provider.
	 * 
	 * @param listener The listener to remove.
	 */
	public void remove(final ListenerType listener) {
		listeners.remove(listener);
	}
}