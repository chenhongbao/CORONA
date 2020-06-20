package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcRspAuthenticateField;
import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;

public class RspAuthenticate {
	public CThostFtdcRspAuthenticateField RspAuthenticateField;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspAuthenticate() {}
}
