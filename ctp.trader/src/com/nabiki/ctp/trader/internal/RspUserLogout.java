package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;
import com.nabiki.ctp.trader.jni.CThostFtdcUserLogoutField;

public class RspUserLogout {
	public CThostFtdcUserLogoutField UserLogout;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUserLogout() {}
}
