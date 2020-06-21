package com.nabiki.corona.trade.core;

import java.nio.file.Paths;

import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.info.api.RemoteConfig;
import com.nabiki.ctp.trader.*;
import com.nabiki.ctp.trader.struct.*;

public class TradeEngine extends CThostFtdcTraderSpi {
	public enum State {
		STARTING, STARTED, STOPPING, STOPPED
	}

	private final TradeServiceContext context;
	private final TradeEngineListener listener;

	private State state = State.STOPPED;

	private RemoteConfig config;
	private CThostFtdcTraderApi traderApi;

	public TradeEngine(TradeEngineListener listener, TradeServiceContext context) {
		this.context = context;
		this.listener = listener;
	}

	public void stop() {
		this.state = State.STOPPING;
		// Request logout.
		CThostFtdcUserLogoutField req = new CThostFtdcUserLogoutField();
		req.BrokerID = this.config.brokerId();
		req.UserID = this.config.userId();
		checkRtnCode("Request logout", this.traderApi.ReqUserLogout(req, Utils.increaseGet()));
	}

	public State state() {
		return this.state;
	}

	public void start() throws KerError {
		this.config = this.context.info().remoteConfig().traderConfig();
		if (this.config == null)
			throw new KerError("Trader config not found.");
		// Ensure flow directory.
		Utils.ensureDir(Paths.get(this.config.flowPath()));
		// Set state.
		this.state = State.STARTING;
		
		// Create trader api if it doesn't exist.
		if (this.traderApi == null) {
			this.traderApi = CThostFtdcTraderApi.CreateFtdcTraderApi(this.config.flowPath());
			// Set counter info.
			// Front addresses.
			for (var addr : this.config.addresses())
				this.traderApi.RegisterFront(addr);
			// Spi.
			this.traderApi.RegisterSpi(this);
			// Topic types.
			this.traderApi.SubscribePrivateTopic(this.config.privateTopicType());
			this.traderApi.SubscribePublicTopic(this.config.publicTopicType());
		}

		// Connect to remote counter.
		this.traderApi.Init();
	}
	
	public synchronized void send(Request<?> request) throws KerError {
		if (request == null)
			throw new KerError("Request null pointer");
		
		switch (request.type()) {
		case Unknown:
			throw new KerError("Unknown request type.");
		case QueryInstrument:
			checkRtnCode("request query instrument", 
					this.traderApi.ReqQryInstrument(translate((KerQueryInstrument)request.request()), Utils.increaseGet()));
			break;
		case QueryMargin:
			checkRtnCode("request query margin",
					this.traderApi.ReqQryInstrumentMarginRate(translate((KerQueryMargin)request.request()), Utils.increaseGet()));
			break;
		case QueryCommission:
			checkRtnCode("request query commission",
					this.traderApi.ReqQryInstrumentCommissionRate(translate((KerQueryCommission)request.request()), Utils.increaseGet()));
			break;
		case QueryAccount:
			// Set currency id.
			var qryAccount = (KerQueryAccount)request.request();
			qryAccount.currencyId(this.config.currencyId());
			checkRtnCode("request query account",
					this.traderApi.ReqQryTradingAccount(translate(qryAccount), Utils.increaseGet()));
			break;
		case QueryPositionDetail:
			checkRtnCode("request query position detail",
					this.traderApi.ReqQryInvestorPositionDetail(translate((KerQueryPositionDetail)request.request()), Utils.increaseGet()));
			break;
		case Order:
			checkRtnCode("request inserting order",
					this.traderApi.ReqOrderInsert(translate((KerOrder)request.request()), Utils.increaseGet()));
			break;
		case Action:
			checkRtnCode("request order action",
					this.traderApi.ReqOrderAction(translate((KerAction)request.request()), Utils.increaseGet()));
		default:
			break;
		}
	}

	private CThostFtdcInputOrderActionField translate(KerAction request) {
		// TODO Auto-generated method stub
		return null;
	}

	private CThostFtdcInputOrderField translate(KerOrder request) {
		// TODO Auto-generated method stub
		return null;
	}

	private CThostFtdcQryInvestorPositionDetailField translate(KerQueryPositionDetail request) {
		// TODO Auto-generated method stub
		return null;
	}

	private CThostFtdcQryTradingAccountField translate(KerQueryAccount request) {
		// TODO Auto-generated method stub
		return null;
	}

	private CThostFtdcQryInstrumentCommissionRateField translate(KerQueryCommission request) {
		// TODO Auto-generated method stub
		return null;
	}

	private CThostFtdcQryInstrumentMarginRateField translate(KerQueryMargin request) {
		// TODO Auto-generated method stub
		return null;
	}

	private CThostFtdcQryInstrumentField translate(KerQueryInstrument request) {
		// TODO Auto-generated method stub
		return null;
	}

	private void checkRtnCode(String msg, int n) {
		switch (n) {
		case -1:
			this.listener.error(new KerError("Network failure: " + msg));
			break;
		case -2:
			this.listener.error(new KerError("Request queue overflow: " + msg));
			break;
		case -3:
			this.listener.error(new KerError("Flow control: " + msg));
			break;
		default:
			break;
		}
	}

	private KerOrder translate(CThostFtdcInputOrderField order) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerAction translate(CThostFtdcOrderActionField action) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerAction translate(CThostFtdcInputOrderActionField action) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerInstrument translate(CThostFtdcInstrumentField instrument) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerCommission translate(CThostFtdcInstrumentCommissionRateField commission) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerMargin translate(CThostFtdcInstrumentMarginRateField margin) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerPositionDetail translate(CThostFtdcInvestorPositionDetailField pd) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerAccount translate(CThostFtdcTradingAccountField account) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private KerOrderStatus transalte(CThostFtdcOrderField order) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerTradeReport translate(CThostFtdcTradeField trade) {
		// TODO Auto-generated method stub
		return null;
	}

	private KerError translate(CThostFtdcRspInfoField rsp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void OnErrRtnOrderAction(CThostFtdcOrderActionField orderAction, CThostFtdcRspInfoField rspInfo) {
		this.listener.error(translate(orderAction), translate(rspInfo));
	}

	@Override
	public void OnErrRtnOrderInsert(CThostFtdcInputOrderField inputOrder, CThostFtdcRspInfoField rspInfo) {
		this.listener.error(translate(inputOrder), translate(rspInfo));
	}

	@Override
	public void OnFrontConnected() {
		if (this.state == State.STOPPING || this.state == State.STOPPED)
			return;
		
		CThostFtdcReqAuthenticateField req = new CThostFtdcReqAuthenticateField();
		req.AppID = this.config.appId();
		req.AuthCode = this.config.authCode();
		req.BrokerID = this.config.brokerId();
		req.UserID = this.config.userId();
		req.UserProductInfo = this.config.userProductInfo();
		// Send auth request.
		checkRtnCode("request authentication", this.traderApi.ReqAuthenticate(req, Utils.increaseGet()));
	}

	@Override
	public void OnFrontDisconnected(int reason) {
		this.listener.error(new KerError(reason, "Trader disconnected: " + reason));
	}

	@Override
	public void OnRspAuthenticate(CThostFtdcRspAuthenticateField rspAuthenticateField, CThostFtdcRspInfoField rspInfo,
			int requestId, boolean isLast) {
		if (rspInfo.code == 0) {
			CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
			req.BrokerID = this.config.brokerId();
			req.UserID = this.config.userId();
			req.Password = this.config.password();
			req.UserProductInfo = this.config.userProductInfo();
			// Send login request.
			checkRtnCode("request login", this.traderApi.ReqUserLogin(req, Utils.increaseGet()));
		} else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspError(CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspOrderAction(CThostFtdcInputOrderActionField inputOrderAction, CThostFtdcRspInfoField rspInfo,
			int requestId, boolean isLast) {
		this.listener.error(translate(inputOrderAction), translate(rspInfo));
	}

	@Override
	public void OnRspOrderInsert(CThostFtdcInputOrderField inputOrder, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		this.listener.error(translate(inputOrder), translate(rspInfo));
	}

	@Override
	public void OnRspQryInstrument(CThostFtdcInstrumentField instrument, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		if (rspInfo.code == 0)
			this.listener.instrument(translate(instrument), isLast);
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField instrumentCommissionRate,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			this.listener.commission(translate(instrumentCommissionRate));
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField instrumentMarginRate,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			this.listener.margin(translate(instrumentMarginRate));
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryInvestorPositionDetail(CThostFtdcInvestorPositionDetailField investorPositionDetail,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			this.listener.position(translate(investorPositionDetail), isLast);
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryTradingAccount(CThostFtdcTradingAccountField tradingAccount, CThostFtdcRspInfoField rspInfo,
			int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			this.listener.account(translate(tradingAccount));
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField settlementInfoConfirm,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		this.state = State.STARTED;
	}

	@Override
	public void OnRspUserLogin(CThostFtdcRspUserLoginField rspUserLogin, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		if (rspInfo.code == 0) {
			CThostFtdcSettlementInfoConfirmField req = new CThostFtdcSettlementInfoConfirmField();
			req.BrokerID = this.config.brokerId();
			req.InvestorID = this.config.userId();
			checkRtnCode("request settlement confirm", this.traderApi.ReqSettlementInfoConfirm(req, Utils.increaseGet()));
		} else
			this.listener.error(translate(rspInfo));
			
	}

	@Override
	public void OnRspUserLogout(CThostFtdcUserLogoutField userLogout, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		if (rspInfo.code == 0) {
			this.state = State.STOPPED;
		} else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRtnOrder(CThostFtdcOrderField order) {
		try {
			this.context.local().orderStatus(transalte(order));
		} catch (KerError e) {
			this.listener.error(e);
		}
	}
	
	@Override
	public void OnRtnTrade(CThostFtdcTradeField trade) {
		try {
			this.context.local().tradeReport(translate(trade));
		} catch (KerError e) {
			this.listener.error(e);
		}
	}
}
