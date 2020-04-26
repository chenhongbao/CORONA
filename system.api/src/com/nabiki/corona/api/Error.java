package com.nabiki.corona.api;

public interface Error {
	int code();

	String message();

	Throwable cause();
}
