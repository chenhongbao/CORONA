package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.trader.struct.CThostFtdcUserLogoutField;

public class RspUserLogout {
	public CThostFtdcUserLogoutField UserLogout;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUserLogout() {}
}
