package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.trader.struct.CThostFtdcSettlementInfoConfirmField;

public class RspSettlementInfoConfirm {
	public CThostFtdcSettlementInfoConfirmField SettlementInfoConfirm;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspSettlementInfoConfirm() {}
}
