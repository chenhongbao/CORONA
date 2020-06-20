package com.nabiki.ctp.trader;
import com.nabiki.ctp.trader.struct.*;


public class CThostFtdcTraderApiImpl extends CThostFtdcTraderApi {
	private static final String apiVersion = "0.0.1";
	private final String szFlowPath;
	
	CThostFtdcTraderApiImpl(String szFlowPath) {
		this.szFlowPath = szFlowPath;
	}

	@Override
	public String GetApiVersion() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String GetTradingDay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void Init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Join() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RegisterFront(String frontAddress) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void RegisterSpi(CThostFtdcTraderSpi spi) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Release() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int ReqAuthenticate(CThostFtdcReqAuthenticateField reqAuthenticateField, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqOrderInsert(CThostFtdcInputOrderField inputOrder, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqOrderAction(CThostFtdcInputOrderActionField inputOrderAction, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqQryInstrument(CThostFtdcQryInstrumentField qryInstrument, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqQryInstrumentCommissionRate(CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate,
											  int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqQryInstrumentMarginRate(CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate,
										  int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqQryTradingAccount(CThostFtdcQryTradingAccountField qryTradingAccount, int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int ReqQryInvestorPositionDetail(CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail,
			int requestId) {
		// TODO Auto-generated method stub
		return 0;
	}
}
