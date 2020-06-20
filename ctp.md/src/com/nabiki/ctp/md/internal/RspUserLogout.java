package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.md.struct.CThostFtdcUserLogoutField;

public class RspUserLogout {
	public CThostFtdcUserLogoutField UserLogout;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUserLogout() {}
}
