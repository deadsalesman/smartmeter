package uk.ac.imperial.smartmeter.res;

import java.io.Serializable;
/**
 * Class that binds a pair of four objects into a single object. 
 * 'Nicer' alternative to using a map of T-> map of U-> map of V -> W.
 * @author bwindo
 *
 * @param <T>
 * @param <U>
 * @param <V>
 * @param <W>
 */
public class Quadruple <T, U, V, W> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4763661085561150688L;
	public T left;
	public U leftmid;
	public V rightmid;
	public W right;
	
	public Quadruple(T t, U u, V v, W w)
	{
		left = t;
		leftmid = u;
		rightmid = v;
		right = w;
	}
	public Quadruple()
	{
		
	}
	
	public Boolean equals(Quadruple<T, U, V, W> t)
	{
		return t.left.equals(left)&&t.leftmid.equals(leftmid)&&t.rightmid.equals(rightmid)&&t.rightmid.equals(right);
	}
}
