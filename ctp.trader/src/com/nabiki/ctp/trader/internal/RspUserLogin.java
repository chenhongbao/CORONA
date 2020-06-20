package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;
import com.nabiki.ctp.trader.jni.CThostFtdcRspUserLoginField;

public class RspUserLogin {
	public CThostFtdcRspUserLoginField RspUserLogin;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUserLogin() {}
}
