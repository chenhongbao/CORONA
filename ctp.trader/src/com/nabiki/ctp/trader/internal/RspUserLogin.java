package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.trader.struct.CThostFtdcRspUserLoginField;

public class RspUserLogin {
	public CThostFtdcRspUserLoginField RspUserLogin;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUserLogin() {}
}
