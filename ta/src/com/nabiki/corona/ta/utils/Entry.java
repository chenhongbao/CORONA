package com.nabiki.corona.ta.utils;

public class Entry<E> {
	private final int index;
	private final E value;
	
	public Entry(int index, E value) {
		this.index = index;
		this.value = value;
	}
	
	public int index() {
		return this.index;
	}
	
	public E value() {
		return this.value;
	}
}