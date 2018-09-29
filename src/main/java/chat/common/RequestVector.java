package chat.common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class defines a request vector with a Map. A request vector is serializable
 * to be inserted in messages
 */
public class RequestVector implements Serializable {
	/**
	 * serial version unique identifier for serialization.
	 */
	private static final long serialVersionUID = 2L;
	/**
	 * the map of the request vector. The key is the identity (integer) of the process
	 * and the object is the value (integer) of the clock of the process.
	 */
	private TreeMap<Integer, Integer> sortedVector;

	/**
	 * the constructor with an empty {@link java.util.Map}.
	 */
	public RequestVector() {
		sortedVector = new TreeMap<>();
		assert invariant();
	}
	/**
	 * checks the invariant of the class: scalar clock are greater than or equal to
	 * 0.
	 * 
	 * NB: the method is final so that the method is not overridden in potential
	 * subclasses because it is called in the constructor.
	 * 
	 * @return a boolean stating whether the invariant is maintained.
	 */
	public final boolean invariant() {
		boolean result = true;
		for (Integer value : sortedVector.values()) {
			if (value.intValue() < 0) {
				return false;
			}
		}
		return result;
	}
	/**
	 * gets the value (integer) of the clock of a process (integer key). The
	 * returned value is either the value found or the default value <tt>0</tt>.
	 * 
	 * @param key
	 *            the identifier (integer) of the process.
	 * @return the clock value.
	 */
	public int getEntry(final int key) {
		return sortedVector.getOrDefault(key, 0);
	}

	/**
	 * sets the value (integer) of the clock of the process (integer key). If the
	 * corresponding key does not already exists in the map, it is inserted.
	 * 
	 * @param key
	 *            the identifier (integer) of the process. An
	 *            IllegalArgumentException is thrown in case of a negative value.
	 * @param value
	 *            the value of the clock.
	 */
	public void setEntry(final int key, final int value) {
		if (key < 0) {
			throw new IllegalArgumentException("identite de processus non valide (" + key + ")");
		}
		sortedVector.put(key, value);
		assert invariant();
	}
	/**
	 * increments the clock of a given process (integer). If the corresponding key
	 * does not already exists in the map, it is inserted with the value 1, that is
	 * to say as if it were 0 before the call.
	 * 
	 * @param key
	 *            the identifier (integer) of the process.
	 */
	public void incrementEntry(final int key) {
		if (key < 0) {
			throw new IllegalArgumentException("identite de processus non valide (" + key + ")");
		}
		sortedVector.put(key, getEntry(key) + 1);
		assert invariant();
	}
	/**
	 * states whether this request vector is greater or equals the request vector
	 * provided in the argument <tt>other</tt>.
	 * 
	 * @param other
	 *            the request vector to compare to.
	 * @return the boolean of the condition greater.
	 */
	public boolean isGreater(final RequestVector other) {
		if (other != null) {
			List<Integer> keys = new ArrayList<>(sortedVector.keySet());
			keys.addAll(other.sortedVector.keySet());
			for (Integer key : keys) {
				if (this.getEntry(key) < other.getEntry(key) || this.getEntry(key) == other.getEntry(key)) {
					return false;
				}
			}
			return true;
		} else {
			return false;
		}
	}
	/**
	 * Returns a view of the portion of the map whose keys are greater than or equal to given key.
	 * @param fromKey
	 *         the key to compare to.
	 * @return supMap
	 *          map whose keys are greater than or equal to given key.
	 */
	public Set<Integer> supMap(final Integer fromKey) {
		return sortedVector.tailMap(fromKey).keySet();
	}
	/**
	 * Returns a view of the portion of the map whose keys are lower given key.
	 * @param toKey
	 *         the key to compare to.
	 * @return infMap
	 *          map whose keys are lower than given key.
	 */
	public Set<Integer> infMap(final Integer toKey) {
		return sortedVector.subMap(sortedVector.firstKey(), toKey).keySet();
	}
	/**
	 * concatenates two sets.
	 * @param map1
	 *         first set.
	 * @param map2
	 *         second set.
	 * @return concatMap
	 *          concatenation of the two sets.
	 */
	/*public Set<Integer> computeUnionOfKeys(final Set<Integer> map1, final Set<Integer> map2) {
		return Stream.concat(map1.stream(), map2.stream()).collect(Collectors.toSet());
	}*/
	public Set<Integer> computeUnionOfKeys(final Integer fromKey, final Integer toKey) {
		Set<Integer> map1 = supMap(fromKey);
		Set<Integer> map2 = infMap(toKey);
		return Stream.concat(map1.stream(), map2.stream()).collect(Collectors.toSet());
	}
	@Override
	public String toString() {
		return sortedVector.toString();
	}

}
