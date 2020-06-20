package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcInstrumentMarginRateField;
import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;

public class RspQryInstrumentMarginRate {
	public CThostFtdcInstrumentMarginRateField InstrumentMarginRate;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryInstrumentMarginRate() {}
}
