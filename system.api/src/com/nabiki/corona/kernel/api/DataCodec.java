package com.nabiki.corona.kernel.api;

public interface DataCodec {
	/**
	 * Encode the given instance to bytes.
	 * 
	 * @param <T> type of the instance
	 * @param a instance
	 * @return encoded bytes
	 */
	<T> byte[] encode(T a);
	
	/**
	 * Decode the given type of class with given bytes.
	 * 
	 * @param <T> type to decode to
	 * @param b bytes
	 * @param clz class of type
	 * @return instance of decoded class of type
	 */
	<T>T decode(byte[] b, Class<T> clz);
}
