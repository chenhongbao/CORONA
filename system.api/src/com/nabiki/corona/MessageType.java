package com.nabiki.corona;

public class MessageType {
	// Received data, ID range: [1, 100]
	public final static short RX_ACCOUNT = 1;
	public final static short RX_ACTION_ERROR = 2;
	public final static short RX_CANDLE = 3;
	public final static short RX_COMMISSION = 4;
	public final static short RX_ERROR = 5;
	public final static short RX_INSTRUMENT = 6;
	public final static short RX_MARGIN = 7;
	public final static short RX_ORDER_ERROR = 8;
	public final static short RX_ORDER_STATUS = 9;
	public final static short RX_POSITION_DETAIL = 10;
	public final static short RX_TICK = 11;
	public final static short RX_TRADE_REPORT = 12;
	
	// Sent query, ID range: [101, 200]
	public final static short TX_QUERY_ACCOUNT = 101;
	public final static short TX_QUERY_COMMISSION = 102;
	public final static short TX_QUERY_INSTRUMENT = 103;
	public final static short TX_QUERY_MARGIN = 104;
	public final static short TX_QUERY_POSITION_DETAIL = 105;
	public final static short TX_QUERY_CLIENT_ACCOUNT = 106;
	public final static short TX_QUERY_CLIENT_ORDER_STATUS = 107;
	public final static short TX_QUERY_CLIENT_POSITION_DETAIL = 108;
	
	// Sent request, ID range: [201, 300]
	public final static short TX_REQUEST_ACTION = 201;
	public final static short TX_REQUEST_ORDER = 202;
	
	// Sent settings, ID range: [301, 400]
	public final static short TX_SET_CLIENT_SUBSCRIBE_SYMBOLS = 301;
	public final static short TX_SET_SUBSCRIBE_SYMBOLS = 302;
	
	// Connection management, ID range: [1001, 1200]
	public final static short TX_MGR_OPEN_CONN = 1001;
	public final static short TX_MGR_CLOSE_CONN = 1002;
	public final static short TX_MGR_HEARTBEAT = 1003;
	public final static short TX_MGR_EMPTY = 1004;
}
