package tv.porst.splib.maps;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Contains helper functions for working with maps.
 */
public final class MapHelpers {

	/**
	 * Sorts a map by its values.
	 * 
	 * @param <S> Type of map keys.
	 * @param <T> Type of map values.
	 * @param map The map to sort.
	 * 
	 * @return The sorted map.
	 */
	public static <S, T extends Comparable<T>> Map<S, T> sortByValue(final Map<S, T> map) {

		final List<Map.Entry<S, T>> list = new LinkedList<Map.Entry<S, T>>(map.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<S, T>>() {
			@Override
			public int compare(final Map.Entry<S, T> o1, final Map.Entry<S, T> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});

		final Map<S, T> result = new LinkedHashMap<S, T>();
		for (final Map.Entry<S, T> element : list) {
			result.put(element.getKey(), element.getValue());
		}
		return result;
	}

}
