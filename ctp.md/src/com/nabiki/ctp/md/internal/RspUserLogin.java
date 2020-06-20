package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.md.struct.CThostFtdcRspUserLoginField;

public class RspUserLogin {
	public CThostFtdcRspUserLoginField RspUserLogin;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUserLogin() {}
}
