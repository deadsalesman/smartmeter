package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;

/**
 * Class that binds a pair of two objects into a single object. 
 * Alternative to using a map of T->P.
 * @author bwindo
 *
 * @param <T>
 * @param <P>
 */
public class Pair<T, P> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 575279936354499757L;
	public T left;
	public P right;

	public Pair(T t, P p) {
		left = t;
		right = p;
	}

	public Pair() {
	}

	public Boolean equals(Pair<T, P> t) {

		return t.left.equals(left) && t.right.equals(right); //obviously broken for many types. beware.

	}

}
