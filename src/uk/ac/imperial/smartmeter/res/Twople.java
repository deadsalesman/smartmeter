package uk.ac.imperial.smartmeter.res;

public class Twople<T, P> {
	public T key;
	public P value;

	public Twople(T t, P p) {
		key = t;
		value = p;
	}

	public Boolean equals(Twople<T, P> t) {

		return t.key.equals(key) && t.value.equals(value);

	}

}
