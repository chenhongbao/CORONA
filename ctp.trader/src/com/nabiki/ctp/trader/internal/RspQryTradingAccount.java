package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.trader.struct.CThostFtdcTradingAccountField;

public class RspQryTradingAccount {
	public CThostFtdcTradingAccountField TradingAccount;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspQryTradingAccount() {}
}
