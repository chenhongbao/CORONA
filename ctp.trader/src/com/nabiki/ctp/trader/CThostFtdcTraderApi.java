package com.nabiki.ctp.trader;
import com.nabiki.ctp.trader.struct.*;

public abstract class CThostFtdcTraderApi {

	protected CThostFtdcTraderApi() {
	}
	
	public static CThostFtdcTraderApi CreateFtdcTraderApi(String szFlowPath) {
		return new CThostFtdcTraderApiImpl(szFlowPath);
	}
	
	public abstract String GetApiVersion();
	
	public abstract String GetTradingDay();
	
	public abstract void Init();
	
	public abstract void Join();
	
	public abstract void RegisterFront(String frontAddress);
	
	public abstract void RegisterSpi(CThostFtdcTraderSpi spi);
	
	public abstract void Release();
	
	public abstract int ReqAuthenticate(CThostFtdcReqAuthenticateField reqAuthenticateField, int requestId);
	
	public abstract int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId);
	
	public abstract int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId);
	
	public abstract int ReqOrderInsert(CThostFtdcInputOrderField inputOrder, int requestId);
	
	public abstract int ReqOrderAction(CThostFtdcInputOrderActionField inputOrderAction, int requestId);
	
	public abstract int ReqQryInstrument(CThostFtdcQryInstrumentField qryInstrument, int requestId);
	
	public abstract int ReqQryInstrumentCommissionRate(CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate, int requestId);
	
	public abstract int ReqQryInstrumentMarginRate(CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate, int requestId);
	
	public abstract int ReqQryTradingAccount(CThostFtdcQryTradingAccountField qryTradingAccount, int requestId);
	
	public abstract int ReqQryInvestorPositionDetail(CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail, int requestId);
}
