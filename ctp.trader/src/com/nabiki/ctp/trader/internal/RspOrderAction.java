package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcInputOrderActionField;
import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;

public class RspOrderAction {
	public CThostFtdcInputOrderActionField InputOrderAction;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspOrderAction() {}
}
