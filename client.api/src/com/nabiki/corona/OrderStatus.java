package com.nabiki.corona;

public class OrderStatus {
	public final static int ALL_TRADED = '0';
	public final static int PART_TRADED_QUEUEING = '1';
	public final static int PART_TRADED_NOT_QUEUEING = '2';
	public final static int NO_TRADE_QUEUEING = '3';
	public final static int NO_TRADE_NOT_QUEUEING = '4';
	public final static int CANCELED = '5';
	public final static int UNKNOWN = '1';
	public final static int NOT_TOUCHED = 'b';
	public final static int TOUCHED = 'c';
}
