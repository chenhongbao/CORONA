package com.nabiki.corona.system.api;

import com.nabiki.corona.client.api.Error;

public class KerError extends Throwable implements Error {
	private static final long serialVersionUID = 1L;
	private int code;
	
	public KerError(int code) {
		super();
		this.code = code;
	}
	
	public KerError(String message) {
		super(message);
	}
	
	public KerError(int code, String message) {
		super(message);
		this.code = code;
	}
	
	public KerError(Throwable cause) {
		super(cause);
	}
	
	public KerError(int code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public KerError(String message, Throwable cause) {
		super(message, cause);
	}

	public KerError(int code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	@Override
	public int code() {
		return this.code;
	}

	@Override
	public String message() {
		return super.getMessage();
	}

	@Override
	public Throwable cause() {
		return super.getCause();
	}
	
	
}
