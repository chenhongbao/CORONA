package com.nabiki.corona.kernel.api;

import com.nabiki.corona.api.Error;

public class KerError extends Exception implements Error {
	private static final long serialVersionUID = 1L;
	private int code;

	public KerError(int code) {
		super();
		this.code = code;
	}

	public KerError(int code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.code = code;
	}

	public KerError(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	public KerError(int code, String message) {
		super(message);
		this.code = code;
	}

	public KerError(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public KerError(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public KerError(String message, Throwable cause) {
		super(message, cause);
	}

	public KerError(String message) {
		super(message);
	}

	public KerError(Throwable cause) {
		super(cause);
	}

	@Override
	public int code() {
		return this.code;
	}

	@Override
	public String message() {
		return this.getMessage();
	}

	@Override
	public Throwable cause() {
		return this.getCause();
	}
}
