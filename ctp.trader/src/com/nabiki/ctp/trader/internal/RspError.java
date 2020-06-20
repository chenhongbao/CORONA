package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;

public class RspError {
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspError() {}
}
