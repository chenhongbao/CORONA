package com.nabiki.corona;

public class OrderStatus {
	public final static char ALL_TRADED = '0';
	public final static char PART_TRADED_QUEUEING = '1';
	public final static char PART_TRADED_NOT_QUEUEING = '2';
	public final static char NO_TRADE_QUEUEING = '3';
	public final static char NO_TRADE_NOT_QUEUEING = '4';
	public final static char CANCELED = '5';
	public final static char UNKNOWN = '1';
	public final static char NOT_TOUCHED = 'b';
	public final static char TOUCHED = 'c';
}
