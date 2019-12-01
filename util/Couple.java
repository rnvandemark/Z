package util;

/**
 * A simple generic class that essentially acts as a tuple of size two.
 * @param <T1> Any object.
 * @param <T2> Any object.
 */
public class Couple<T1, T2> {
	
	/**
	 * Some object that acts as the first element in the tuple.
	 */
	public T1 first;
	
	/**
	 * Some object that acts as the second element in the tuple.
	 */
	public T2 second;
	
	/**
	 * The sole constructor.
	 * Takes the initial values for the first and second values.
	 * @param first An instance of some object, or null.
	 * @param second An instance of some object, or null.
	 */
	public Couple(T1 first, T2 second) {
		this.first  = first;
		this.second = second;
	}
}