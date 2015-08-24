package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;

public class Triple <T, U, V> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4763661085561150688L;
	public T left;
	public U central;
	public V right;
	
	public Triple(T t, U u, V v)
	{
		left = t;
		central = u;
		right = v;
	}
	public Triple()
	{
		
	}
	
	public Boolean equals(Triple<T, U, V> t)
	{
		return t.left.equals(left)&&t.central.equals(central)&&t.right.equals(right);
	}
}
