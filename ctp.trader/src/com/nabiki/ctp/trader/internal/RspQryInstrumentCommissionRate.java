package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcInstrumentCommissionRateField;
import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;

public class RspQryInstrumentCommissionRate {
	public CThostFtdcInstrumentCommissionRateField InstrumentCommissionRate;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryInstrumentCommissionRate() {}
}
