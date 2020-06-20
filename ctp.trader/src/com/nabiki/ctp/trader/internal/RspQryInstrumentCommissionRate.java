package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcInstrumentCommissionRateField;
import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;

public class RspQryInstrumentCommissionRate {
	public CThostFtdcInstrumentCommissionRateField InstrumentCommissionRate;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryInstrumentCommissionRate() {}
}
