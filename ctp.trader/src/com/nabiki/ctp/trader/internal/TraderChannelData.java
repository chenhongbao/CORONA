package com.nabiki.ctp.trader.internal;

import java.util.List;

import com.nabiki.ctp.trader.jni.CThostFtdcConnect;
import com.nabiki.ctp.trader.jni.CThostFtdcDisconnect;
import com.nabiki.ctp.trader.jni.CThostFtdcOrderField;
import com.nabiki.ctp.trader.jni.CThostFtdcTradeField;

public class TraderChannelData {
	public List<ErrRtnOrderAction> ListErrRtnOrderAction;
	
	public List<ErrRtnOrderInsert> ListErrRtnOrderInsert;
	
	public List<CThostFtdcConnect> ListConnect;
	
	public List<CThostFtdcDisconnect> ListDisconnect;
	
	public List<RspAuthenticate> ListRspAuthenticate;
	
	public List<RspError> ListRspError;
	
	public List<RspOrderAction> ListRspOrderAction;
	
	public List<RspOrderInsert> ListRspOrderInsert;
	
	public List<RspQryInstrument> ListRspQryInstrument;
	
	public List<RspQryInstrumentCommissionRate> ListRspQryInstrumentCommissionRate;
	
	public List<RspQryInstrumentMarginRate> ListRspQryInstrumentMarginRate;
	
	public List<RspQryInvestorPositionDetail> ListRspQryInvestorPositionDetail;
	
	public List<RspQryTradingAccount> ListRspQryTradingAccount;
	
	public List<RspSettlementInfoConfirm> ListRspSettlementInfoConfirm;
	
	public List<RspUserLogin> ListRspUserLogin;
	
	public List<RspUserLogout> ListRspUserLogout;
	
	public List<CThostFtdcOrderField> ListRtnOrder;
	
	public List<CThostFtdcTradeField> ListRtnTrade;
}
