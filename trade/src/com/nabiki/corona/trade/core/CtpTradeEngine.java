package com.nabiki.corona.trade.core;

import java.nio.file.Paths;

import com.nabiki.corona.ActionFlag;
import com.nabiki.corona.CloseReason;
import com.nabiki.corona.HedgeFlag;
import com.nabiki.corona.OrderPriceType;
import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.info.api.RemoteConfig;
import com.nabiki.corona.trade.api.TradeEngine;
import com.nabiki.corona.trade.api.TradeEngineListener;
import com.nabiki.ctp.trader.*;
import com.nabiki.ctp.trader.struct.*;

public class CtpTradeEngine extends CThostFtdcTraderSpi implements TradeEngine {
	public enum State {
		STARTING, STARTED, STOPPING, STOPPED
	}

	private final TradeServiceContext context;
	private final TradeEngineListener listener;

	private State state = State.STOPPED;

	private RemoteConfig config;
	private CThostFtdcTraderApi traderApi;
	private CThostFtdcRspUserLoginField user;
	
	// Factory.
	private DataFactory factory = DefaultDataFactory.create();

	public CtpTradeEngine(TradeEngineListener listener, TradeServiceContext context) {
		this.context = context;
		this.listener = listener;
	}

	@Override
	public void stop() {
		this.state = State.STOPPING;
		// Request logout.
		CThostFtdcUserLogoutField req = new CThostFtdcUserLogoutField();
		req.BrokerID = this.config.brokerId();
		req.UserID = this.config.userId();
		checkRtnCode("Request logout", this.traderApi.ReqUserLogout(req, Utils.increaseGet()));
	}

	@Override
	public State state() {
		return this.state;
	}

	@Override
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
	
	@Override
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
	
	private CThostFtdcRspUserLoginField user() throws KerError {
		if (this.user == null)
			throw new KerError("User login null pointer.");
		return this.user;
	}
	
	private String exchangeId(String symbol) throws KerError {
		var in = this.context.info().instrument(symbol);
		if (in == null)
			throw new KerError("Instrument not found.");
		return in.exchangeId();
	}
	
	private int volumeMultiple(String symbol) throws KerError{
		var in = this.context.info().instrument(symbol);
		if (in == null)
			throw new KerError("Instrument not found.");
		return in.volumeMultiple();
	}

	private CThostFtdcInputOrderActionField translate(KerAction request) throws KerError {
		var r = new CThostFtdcInputOrderActionField();
		r.BrokerID = user().BrokerID;
		r.InvestorID = user().UserID;
		r.UserID = user.UserID;
		r.ActionFlag = ActionFlag.DELETE;
		r.FrontID = user().FrontID;
		r.SessionID = user().SessionID;
		r.InstrumentID = request.symbol();
		r.OrderRef = request.orderId();
		r.ExchangeID = exchangeId(request.symbol());
		return r;
	}

	private CThostFtdcInputOrderField translate(KerOrder request) throws KerError {
		var r = new CThostFtdcInputOrderField();
		r.BrokerID = user().BrokerID;
		r.InvestorID = user().UserID;
		r.ExchangeID = exchangeId(request.symbol());
		r.InstrumentID = request.symbol();
		r.UserID = user().UserID;
		r.OrderPriceType = OrderPriceType.LIMIT_PRICE;
		r.Direction = (byte)request.direction();
		r.CombOffsetFlag = (byte)request.offsetFlag();
		r.CombHedgeFlag = (byte)request.hedgeFlag();
		r.LimitPrice = request.price();
		r.VolumeTotalOriginal = request.volume();
		r.TimeCondition = (byte)request.timeCondition();
		r.MinVolume = 1;
		r.ContingentCondition = (byte)request.contigentConditon();
		r.StopPrice = 0D;
		r.ForceCloseReason = (byte)CloseReason.NOT_FORCE_CLOSE;
		r.IsAutoSuspend = 0;
		return r;
	}

	private CThostFtdcQryInvestorPositionDetailField translate(KerQueryPositionDetail request) throws KerError {
		var r = new CThostFtdcQryInvestorPositionDetailField();
		r.BrokerID = user().BrokerID;
		r.InvestorID = user().UserID;
		r.ExchangeID = exchangeId(request.symbol());
		r.InstrumentID = request.symbol();
		return r;
	}

	private CThostFtdcQryTradingAccountField translate(KerQueryAccount request) throws KerError {
		var r = new CThostFtdcQryTradingAccountField();
		r.AccountID = user().UserID;
		r.BrokerID = user().BrokerID;
		r.CurrencyID = this.config.currencyId();
		r.InvestorID = user().UserID;
		return r;
	}

	private CThostFtdcQryInstrumentCommissionRateField translate(KerQueryCommission request) throws KerError {
		var r = new CThostFtdcQryInstrumentCommissionRateField();
		r.BrokerID = user().BrokerID;
		r.ExchangeID = exchangeId(request.symbol());
		r.InstrumentID = request.symbol();
		r.InvestorID = user().UserID;
		return r;
	}

	private CThostFtdcQryInstrumentMarginRateField translate(KerQueryMargin request) throws KerError {
		var r = new CThostFtdcQryInstrumentMarginRateField();
		r.BrokerID = user().BrokerID;
		r.InvestorID = user().UserID;
		r.InstrumentID = request.symbol();
		r.ExchangeID = exchangeId(request.symbol());
		r.HedgeFlag = (byte)HedgeFlag.SPECULATION;
		return r;
	}

	private CThostFtdcQryInstrumentField translate(KerQueryInstrument request) throws KerError {
		var  r = new CThostFtdcQryInstrumentField();
		r.ExchangeID = exchangeId(request.symbol());
		r.InstrumentID = request.symbol();
		return r;
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

	private KerOrder translate(CThostFtdcInputOrderField order) throws KerError {
		var r = this.factory.create(KerOrder.class);
		r.contigentConditon((char)order.ContingentCondition);
		r.direction((char)order.Direction);
		r.gtdDate(Utils.date(order.GTDDate));
		r.hedgeFlag((char)order.CombHedgeFlag);
		r.ipAddress(order.IPAddress);
		r.macAddress(order.MacAddress);
		r.minVolume(order.MinVolume);
		r.offsetFlag((char)order.CombOffsetFlag);
		r.orderId(order.OrderRef);
		r.price(order.LimitPrice);
		r.priceType((char)order.OrderPriceType);
		r.stopPrice(order.StopPrice);
		r.symbol(order.InstrumentID);
		r.timeCondition((char)order.TimeCondition);
		r.volume(order.VolumeTotalOriginal);
		r.volumeCondition((char)order.VolumeCondition);
		return r;
	}

	private KerAction translate(CThostFtdcOrderActionField action) throws KerError {
		var r = this.factory.create(KerAction.class);
		r.ipAddress(action.IPAddress);
		r.macAddress(action.MacAddress);
		r.orderId(action.OrderRef);
		r.symbol(action.InstrumentID);
		return r;
	}

	private KerAction translate(CThostFtdcInputOrderActionField action) throws KerError {
		var r = this.factory.create(KerAction.class);
		r.ipAddress(action.IPAddress);
		r.macAddress(action.MacAddress);
		r.orderId(action.OrderRef);
		r.symbol(action.InstrumentID);
		return r;
	}

	private KerInstrument translate(CThostFtdcInstrumentField instrument) throws KerError {
		var r = this.factory.create(KerInstrument.class);
		r.createDate(Utils.date(instrument.CreateDate));
		r.deliveryMonth(instrument.DeliveryMonth);
		r.deliveryYear(instrument.DeliveryYear);
		r.endDelivDate(Utils.date(instrument.EndDelivDate));
		r.exchangeId(instrument.ExchangeID);
		r.exchangeInstId(instrument.ExchangeInstID);
		r.expireDate(Utils.date(instrument.ExpireDate));
		r.instLifePhase((char)instrument.InstLifePhase);
		r.isTrading(Utils.bool(instrument.IsTrading));
		r.longMarginRatio(instrument.LongMarginRatio);
		r.maxLimitOrderVolume(instrument.MaxLimitOrderVolume);
		r.maxMarketOrderVolume(instrument.MaxMarketOrderVolume);
		r.minLimitOrderVolume(instrument.MinLimitOrderVolume);
		r.minMarketOrderVolume(instrument.MinMarketOrderVolume);
		r.openDate(Utils.date(instrument.OpenDate));
		r.positionDateType((char)instrument.PositionDateType);
		r.positionType((char)instrument.PositionType);
		r.priceTick(instrument.PriceTick);
		r.productClass((char)instrument.ProductClass);
		r.productId(instrument.ProductID);
		r.shortMarginRatio(instrument.ShortMarginRatio);
		r.startDelivDate(Utils.date(instrument.StartDelivDate));
		r.symbol(instrument.InstrumentID);
		r.underlyingInstrId(instrument.UnderlyingInstrID);
		r.underlyingMultiple(instrument.UnderlyingMultiple);
		r.volumeMultiple(instrument.VolumeMultiple);
		return r;
	}

	private KerCommission translate(CThostFtdcInstrumentCommissionRateField commission) throws KerError {
		var r = this.factory.create(KerCommission.class);
		r.brokerId(commission.BrokerID);
		r.closeRatioByMoney(commission.CloseRatioByMoney);
		r.closeRatioByVolume(commission.CloseRatioByVolume);
		r.closeTodayRatioByMoney(commission.CloseTodayRatioByMoney);
		r.closeTodayRatioByVolume(commission.CloseTodayRatioByVolume);
		r.exchangeId(exchangeId(commission.InstrumentID));
		r.investorId(commission.InvestorID);
		r.openRatioByMoney(commission.OpenRatioByMoney);
		r.openRatioByVolume(commission.OpenRatioByVolume);
		r.symbol(commission.InstrumentID);
		return r;
	}

	private KerMargin translate(CThostFtdcInstrumentMarginRateField margin) throws KerError {
		var r = this.factory.create(KerMargin.class);
		r.brokerId(margin.BrokerID);
		r.exchangeId(exchangeId(margin.InstrumentID));
		r.hedgeFlag((char)margin.HedgeFlag);
		r.investorId(margin.InvestorID);
		r.isRelative(Utils.bool(margin.IsRelative));
		r.longMarginRatioByMoney(margin.LongMarginRatioByMoney);
		r.longMarginRatioByVolume(margin.LongMarginRatioByVolume);
		r.shortMarginRatioByMoney(margin.ShortMarginRatioByMoney);
		r.shortMarginRatioByVolume(margin.ShortMarginRatioByVolume);
		r.symbol(margin.InstrumentID);
		return r;
	}

	private KerPositionDetail translate(CThostFtdcInvestorPositionDetailField pd) throws KerError {
		var r = this.factory.create(KerPositionDetail.class);
		r.closeAmount(pd.CloseAmount);
		r.closeProfitByDate(pd.CloseProfitByDate);
		r.closeProfitByTrade(pd.CloseProfitByTrade);
		r.closeVolume(pd.CloseVolume);
		r.combSymbol(pd.CombInstrumentID);
		r.direction((char)pd.Direction);
		r.exchangeMargin(pd.ExchMargin);
		r.hedgeFlag((char)pd.HedgeFlag);
		r.lastSettlementPrice(pd.LastSettlementPrice);
		r.margin(pd.Margin);
		r.marginRateByMoney(pd.MarginRateByMoney);
		r.marginRateByVolume(pd.MarginRateByVolume);
		r.openDate(Utils.date(pd.OpenDate));
		r.openPrice(pd.OpenPrice);
		r.positionProfitByDate(pd.PositionProfitByDate);
		r.positionProfitByTrade(pd.PositionProfitByTrade);
		r.settlementPrice(pd.SettlementPrice);
		r.symbol(pd.InstrumentID);
		r.timeFirstVolume(pd.TimeFirstVolume);
		r.tradeType((char)pd.TradeType);
		r.tradingDay(Utils.date(pd.TradingDay));
		r.volume(pd.Volume);
		r.volumeMultiple(volumeMultiple(pd.InstrumentID));
		return r;
	}

	private KerAccount translate(CThostFtdcTradingAccountField account) throws KerError {
		var r = this.factory.create(KerAccount.class);
		r.available(account.Available);
		r.balance(account.Balance);
		r.brokerId(account.BrokerID);
		r.closeProfit(account.CloseProfit);
		r.commission(account.Commission);
		r.currencyId(account.CurrencyID);
		r.currentMargin(account.CurrMargin);
		r.deposit(account.Deposit);
		r.exchangeMargin(account.ExchangeMargin);
		r.frozenCash(account.FrozenCash);
		r.frozenCommission(account.FrozenCommission);
		r.frozenMargin(account.FrozenMargin);
		r.interest(account.Interest);
		r.interestBase(account.InterestBase);
		r.positionProfit(account.PositionProfit);
		r.preBalance(account.PreBalance);
		r.preDeposit(account.PreDeposit);
		r.preMargin(account.PreMargin);
		r.reserve(account.Reserve);
		r.reserveBalance(account.ReserveBalance);
		r.settlementId(account.SettlementID);
		r.tradingDay(Utils.date(account.TradingDay));
		r.withdraw(account.Withdraw);
		r.withdrawQuota(account.WithdrawQuota);
		return r;
	}
	
	private KerOrderStatus transalte(CThostFtdcOrderField order) throws KerError {
		var r = this.factory.create(KerOrderStatus.class);
		r.activeTime(Utils.time(order.ActiveTime));
		r.cancelTime(Utils.time(order.CancelTime));
		r.contigentCondition((char)order.ContingentCondition);
		r.currencyId(order.CurrencyID);
		r.direction((char)order.Direction);
		r.forceCloseReason((char)order.ForceCloseReason);
		r.gtdDate(Utils.date(order.GTDDate));
		r.hedgeFlag((char)order.CombHedgeFlag);
		r.insertTime(Utils.time(order.InsertTime));
		r.ipAddress(order.IPAddress);
		r.isAutoSuspend(Utils.bool(order.IsAutoSuspend));
		r.macAddress(order.MacAddress);
		r.minVolume(order.MinVolume);
		r.offsetFlag((char)order.CombOffsetFlag);
		r.orderId(order.OrderRef);
		r.orderSource((char)order.OrderSource);
		r.orderStatus((char)order.OrderStatus);
		r.orderSubmitStatus((char)order.OrderSubmitStatus);
		r.originalVolume(order.VolumeTotalOriginal);
		r.price(order.LimitPrice);
		r.priceType((char)order.OrderPriceType);
		r.remoteFrontId(order.FrontID);
		r.remoteSessionId(order.SessionID);
		r.statusMessage(order.StatusMsg);
		r.stopPrice(order.StopPrice);
		r.suspendTime(Utils.time(order.SuspendTime));
		r.symbol(order.InstrumentID);
		r.timeCondition((char)order.TimeCondition);
		r.tradedVolume(order.VolumeTraded);
		r.tradingDay(Utils.date(order.TradingDay));
		r.updateTime(Utils.time(order.UpdateTime));
		r.volumeCondition((char)order.VolumeCondition);
		r.zceTradedVolume(order.ZCETotalTradedVolume);
		return r;
	}

	private KerTradeReport translate(CThostFtdcTradeField trade) throws KerError {
		var r = this.factory.create(KerTradeReport.class);
		r.direction((char)trade.Direction);
		r.hedgeFlag((char)trade.HedgeFlag);
		r.offsetFlag((char)trade.OffsetFlag);
		r.orderId(trade.OrderRef);
		r.price(trade.Price);
		r.symbol(trade.InstrumentID);
		r.tradeDate(Utils.date(trade.TradeDate));
		r.tradeTime(Utils.time(trade.TradeTime));
		r.tradeType((char)trade.TradeType);
		r.tradingDay(Utils.date(trade.TradingDay));
		r.volume(trade.Volume);
		return r;
	}

	private KerError translate(CThostFtdcRspInfoField rsp) {
		return new KerError(rsp.code, rsp.message);
	}

	@Override
	public void OnErrRtnOrderAction(CThostFtdcOrderActionField orderAction, CThostFtdcRspInfoField rspInfo) {
		try {
			this.listener.error(translate(orderAction), translate(rspInfo));
		} catch (KerError e) {
			this.listener.error(e);
		}
	}

	@Override
	public void OnErrRtnOrderInsert(CThostFtdcInputOrderField inputOrder, CThostFtdcRspInfoField rspInfo) {
		try {
			this.listener.error(translate(inputOrder), translate(rspInfo));
		} catch (KerError e) {
			this.listener.error(e);
		}
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
		try {
			this.listener.error(translate(inputOrderAction), translate(rspInfo));
		} catch (KerError e) {
			this.listener.error(e);
		}
	}

	@Override
	public void OnRspOrderInsert(CThostFtdcInputOrderField inputOrder, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		try {
			this.listener.error(translate(inputOrder), translate(rspInfo));
		} catch (KerError e) {
			this.listener.error(e);
		}
	}

	@Override
	public void OnRspQryInstrument(CThostFtdcInstrumentField instrument, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		if (rspInfo.code == 0)
			try {
				this.listener.instrument(translate(instrument), isLast);
			} catch (KerError e) {
				this.listener.error(e);
			}
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField instrumentCommissionRate,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			try {
				this.listener.commission(translate(instrumentCommissionRate));
			} catch (KerError e) {
				this.listener.error(e);
			}
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField instrumentMarginRate,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			try {
				this.listener.margin(translate(instrumentMarginRate));
			} catch (KerError e) {
				this.listener.error(e);
			}
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryInvestorPositionDetail(CThostFtdcInvestorPositionDetailField investorPositionDetail,
			CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			try {
				this.listener.position(translate(investorPositionDetail), isLast);
			} catch (KerError e) {
				this.listener.error(e);
			}
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspQryTradingAccount(CThostFtdcTradingAccountField tradingAccount, CThostFtdcRspInfoField rspInfo,
			int requestId, boolean isLast) {
		if (rspInfo.code == 0)
			try {
				this.listener.account(translate(tradingAccount));
			} catch (KerError e) {
				this.listener.error(e);
			}
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
			// Keep user login.
			this.user = rspUserLogin;
			// Settlement.
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
