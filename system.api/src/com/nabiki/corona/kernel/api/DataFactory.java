package com.nabiki.corona.kernel.api;

public interface DataFactory {
	<T> T create(Class<T> clz);
	
	<T> T create(T o);
}
