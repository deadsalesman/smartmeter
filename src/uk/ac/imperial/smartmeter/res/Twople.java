package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;

public class Twople<T, P> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 575279936354499757L;
	public T left;
	public P right;

	public Twople(T t, P p) {
		left = t;
		right = p;
	}

	public Twople() {
	}

	public Boolean equals(Twople<T, P> t) {

		return t.left.equals(left) && t.right.equals(right); //obviously broken for many types. beware.

	}

}
