package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcInstrumentField;
import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;

public class RspQryInstrument {
	public CThostFtdcInstrumentField Instrument;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryInstrument() {}
}
