package com.nabiki.ctp.trader;

public class ErrorCodes {
	/**
	 * Throwable from unknown cause.
	 */
	public static final int UNNO_THROW = -5;
	
	/**
	 * No space to append data.
	 */
	public static final int NO_SPACE = -4;
	
	/**
	 * Thread interrupted by unknown source.
	 */
	public static final int UNNO_INTERRUPTED = -3;
	
	/**
	 * Waiting timeout in native codes.
	 */
	public static final int NATIVE_TIMEOUT = -2;
	
	/**
	 * JVM internal error from native codes.
	 */
	public static final int JVM_INTERNAL = -1;
	
	/**
	 * No error.
	 */
	public static final int NO_ERROR = 0;
}
