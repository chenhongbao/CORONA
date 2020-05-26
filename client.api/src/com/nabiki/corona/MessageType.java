package com.nabiki.corona;

public class MessageType {
	// Received data, ID range: [1, 100]
	public final static short RX_ACCOUNT = 1;
	public final static short RX_ACTION_ERROR = 2;
	public final static short RX_CANDLE = 3;
	public final static short RX_HISTORY_CANDLE = 4;
	public final static short RX_COMMISSION = 5;
	public final static short RX_ERROR = 6;
	public final static short RX_INSTRUMENT = 7;
	public final static short RX_MARGIN = 8;
	public final static short RX_ORDER_ERROR = 9;
	public final static short RX_ORDER_STATUS = 10;
	public final static short RX_POSITION_DETAIL = 11;
	public final static short RX_TICK = 12;
	public final static short RX_TRADE_REPORT = 13;
	public final static short RX_SUBSCRIBE_SYMBOL = 14;
	public final static short RX_LIST_ACCOUNT_ID = 31;
	public final static short RX_LIST_SESSION_ID = 32;
	public final static short RX_SET_NEW_ACCOUNT = 33;
	public final static short RX_CASH_MOVE = 34;
	
	// Sent query, ID range: [101, 200]
	public final static short TX_QUERY_ACCOUNT = 101;
	public final static short TX_QUERY_COMMISSION = 102;
	public final static short TX_QUERY_INSTRUMENT = 103;
	public final static short TX_QUERY_MARGIN = 104;
	public final static short TX_QUERY_POSITION_DETAIL = 105;
	public final static short TX_QUERY_CLIENT_ACCOUNT = 111;
	public final static short TX_QUERY_CLIENT_ORDER_STATUS = 112;
	public final static short TX_QUERY_CLIENT_POSITION_DETAIL = 113;
	public final static short TX_QUERY_CLIENT_LIST_SESSION_ID = 114;
	public final static short TX_QUERY_ADMIN_ACCOUNT = 131;
	public final static short TX_QUERY_ADMIN_LIST_ACCOUNT_ID = 132;
	public final static short TX_QUERY_ADMIN_ORDER_STATUS = 133;
	public final static short TX_QUERY_ADMIN_POSITION_DETAIL = 134;
	public final static short TX_QUERY_ADMIN_LIST_SESSION_ID = 135;
	
	// Sent request, ID range: [201, 300]
	public final static short TX_REQUEST_ACTION = 201;
	public final static short TX_REQUEST_ORDER = 202;
	public final static short TX_REQUEST_CLIENT_ACTION = 211;
	public final static short TX_REQUEST_CLIENT_ORDER = 212;
	public final static short TX_REQUEST_ADMIN_ACTION = 231;
	public final static short TX_REQUEST_ADMIN_ORDER = 232;
	
	// Sent settings, ID range: [301, 400]
	public final static short TX_SET_SUBSCRIBE_SYMBOLS = 301;
	public final static short TX_SET_CLIENT_SUBSCRIBE_SYMBOLS = 311;
	public final static short TX_SET_ADMIN_NEW_ACCOUNT = 331;
	public final static short TX_SET_ADMIN_CASH_MOVE = 332;
	
	// Connection management, ID range: [1001, 1200]
	public final static short TX_MGR_OPEN_CONN = 1001;
	public final static short TX_MGR_CLOSE_CONN = 1002;
	public final static short TX_MGR_HEARTBEAT = 1003;
	public final static short TX_MGR_EMPTY = 1004;
}
