package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.jni.CThostFtdcInstrumentMarginRateField;
import com.nabiki.ctp.trader.jni.CThostFtdcRspInfoField;

public class RspQryInstrumentMarginRate {
	public CThostFtdcInstrumentMarginRateField InstrumentMarginRate;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryInstrumentMarginRate() {}
}
