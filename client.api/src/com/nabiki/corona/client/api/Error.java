package com.nabiki.corona.client.api;

public interface Error {
	int code();

	String message();

	Throwable cause();
}
