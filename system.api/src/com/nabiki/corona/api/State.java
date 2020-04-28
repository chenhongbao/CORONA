package com.nabiki.corona.api;

/**
 * Command or status encoded in byte.
 * 
 * @author Hongbao Chen
 *
 */
public final class State {
	// TODO set states
	final public static char OFFSET_OPEN = '0';
	final public static char OFFSET_CLOSE = '1';
	final public static char OFFSET_FORCE_CLOSE = '2';
	final public static char OFFSET_CLOSE_TODAY = '3';
	final public static char OFFSET_CLOSE_YESTERDAY = '4';
	final public static char OFFSET_FORCE_OFF = '5';
	final public static char OFFSET_LOCAL_FORCE_CLOSE = '6';
	
	final public static char DIRECTION_BUY = '0';
	final public static char DIRECTION_SELL = '1';
	
	final public static char ORDER_CANCELED = '5';
}
