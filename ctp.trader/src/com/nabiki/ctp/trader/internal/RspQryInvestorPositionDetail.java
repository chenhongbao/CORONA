package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcInvestorPositionDetailField;
import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;

public class RspQryInvestorPositionDetail {
	public CThostFtdcInvestorPositionDetailField InvestorPositionDetail;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryInvestorPositionDetail() {}
}
