package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcInputOrderField;
import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;

public class RspOrderInsert {
	public CThostFtdcInputOrderField InputOrder;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspOrderInsert() {}
}
