package com.nabiki.corona;

public class PacketType {
	public final static short RX_TICK = 1;
	public final static short RX_CANDLE = 2;
	public final static short TX_REQUEST_ORDER = 3;
	public final static short RX_ORDER_STATUS = 4;
	public final static short RX_TRADE_REPORT = 5;
	public final static short TX_REQUEST_ACTION = 6;
	public final static short RX_ACCOUNT = 6;
	public final static short RX_POSITION_DETAIL = 7;
	public final static short TX_QUERY_INSTRUMENT = 7;
	public final static short TX_QUERY_MARGIN = 7;
	public final static short TX_QUERY_COMMISSION = 7;
	public final static short RX_INSTRUMENT = 7;
	public final static short RX_MARGIN = 7;
	public final static short RX_COMMISSION = 7;
	public final static short TX_QEURY_ORDER_STATUS = 8;
	public final static short TX_QUERY_TRADE_REPORT = 9;
	public final static short TX_QUERY_ACCOUNT = 10;
	public final static short TX_QUERY_POSITION_DETAIL = 11;
	public final static short TX_SET_SUBSCRIBE_SYMBOLS = 12;
	public final static short TX_QEURY_CLIENT_ORDER_STATUS = 8;
	public final static short TX_QUERY_CLIENT_TRADE_REPORT = 9;
	public final static short TX_QUERY_CLIENT_ACCOUNT = 10;
	public final static short TX_QUERY_CLIENT_POSITION_DETAIL = 11;
	public final static short TX_SET_CLIENT_SUBSCRIBE_SYMBOLS = 12;
	public final static short RX_ERROR = 12;
	public final static short RX_ORDER_ERROR = 12;
	public final static short RX_ACTION_ERROR = 12;
}
