package com.nabiki.corona.candle.core;

import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import com.nabiki.corona.candle.api.EngineState;
import com.nabiki.corona.candle.api.TickEngine;
import com.nabiki.corona.candle.api.TickEngineListener;
import com.nabiki.corona.object.DefaultDataFactory;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.system.info.api.*;
import com.nabiki.ctp.md.CThostFtdcMdApi;
import com.nabiki.ctp.md.CThostFtdcMdSpi;
import com.nabiki.ctp.md.struct.CThostFtdcDepthMarketDataField;
import com.nabiki.ctp.md.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp.md.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.md.struct.CThostFtdcRspUserLoginField;
import com.nabiki.ctp.md.struct.CThostFtdcSpecificInstrumentField;
import com.nabiki.ctp.md.struct.CThostFtdcUserLogoutField;

public class CtpTickEngine extends CThostFtdcMdSpi implements TickEngine {
	private EngineState state = EngineState.STOPPED;
	
	private final CandleServiceContext context;
	private final TickEngineListener listener;

	private RemoteConfig config;
	private CThostFtdcMdApi mdApi;
	
	// Factory.
	private DataFactory factory = DefaultDataFactory.create();
	
	private Set<String> subscribed = new HashSet<>();

	public CtpTickEngine(TickEngineListener l, CandleServiceContext context) {
		this.listener = l;
		this.context = context;
	}
	
	@Override
	public void sendSymbols() throws KerError {
		var symbols = this.context.info().symbols();
		if (symbols == null || symbols.size() == 0)
			throw new KerError("No symbol to subscribe.");
		// Get symbols.
		String[] instruments = new String[symbols.size()];
		symbols.toArray(instruments);
		// Send request.
		checkRtnCode("subscribe symbols", this.mdApi.SubscribeMarketData(instruments, instruments.length));
	}

	@Override
	public void stop() {
		// Change state.
		changeState(EngineState.STOPPING);
		// Logout request.
		CThostFtdcUserLogoutField req = new CThostFtdcUserLogoutField();
		req.BrokerID = this.config.brokerId();
		req.UserID =  this.config.userId();
		checkRtnCode("request logout", this.mdApi.ReqUserLogout(req, Utils.increaseGet()));
	}

	@Override
	public EngineState state() {
		return this.state;
	}
	
	@Override
	public void start() throws KerError {
		this.config = this.context.info().remoteConfig().traderConfig();
		if (this.config == null)
			throw new KerError("Md config not found.");
		// Ensure flow directory.
		Utils.ensureDir(Paths.get(this.config.flowPath()));
		
		// Set state.
		changeState(EngineState.STARTING);
		
		// Create trader api if it doesn't exist.
		if (this.mdApi == null) {
			this.mdApi = CThostFtdcMdApi.CreateFtdcMdApi(this.config.flowPath(), false, false);
			// Set counter info.
			// Front addresses.
			for (var addr : this.config.addresses())
				this.mdApi.RegisterFront(addr);
			// Spi.
			this.mdApi.RegisterSpi(this);
		}

		// Connect to remote counter.
		this.mdApi.Init();
	}
	
	private void changeState(EngineState state) {
		this.state = state;
		try {
			this.listener.state(this.state);
		} catch (Throwable th) {
		}
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
	
	private KerTick translate(CThostFtdcDepthMarketDataField depth) throws KerError {
		var r = this.factory.create(KerTick.class);
		r.actionDay(Utils.date(depth.ActionDay));
		r.askPrice(depth.AskPrice1);
		r.askVolume(depth.AskVolume1);
		r.averagePrice(depth.AveragePrice);
		r.bidPrice(depth.BidPrice1);
		r.bidVolume(depth.BidVolume1);
		r.closePrice(depth.ClosePrice);
		r.highestPrice(depth.HighestPrice);
		r.lastPrice(depth.LastPrice);
		r.lowerLimitPrice(depth.LowerLimitPrice);
		r.lowestPrice(depth.LowestPrice);
		r.openInterest((int)depth.OpenInterest);
		r.openPrice(depth.OpenPrice);
		r.preClosePrice(depth.PreClosePrice);
		r.preOpenInterest((int)depth.PreOpenInterest);
		r.preSettlementPrice(depth.PreSettlementPrice);
		r.settlementPrice(depth.SettlementPrice);
		r.symbol(depth.InstrumentID);
		r.tradingDay(Utils.date(depth.TradingDay));
		r.updateTime(Utils.time(depth.UpdateTime));
		r.updateMillis(depth.UpdateMillisec);
		r.upperLimitPrice(depth.UpperLimitPrice);
		r.volume(depth.Volume);
		// Check the tick type.
		// Need close price to decide post market and realtime.
		r.isPreMarket(isPreMarket());
		r.isPostMarket(isPostMarket(r));
		r.isRealTime(isReal(r));
		return r;
	}
	
	private boolean isPreMarket() {
		// TODO Use 21:00 and 09:00 to check the pre market.
		var hour = LocalTime.now().getHour();
		return (8 < hour && hour < 9) || (20 < hour && hour < 21);
	}
	
	private boolean isPostMarket(KerTick tick) {
		return Utils.validPrice(tick.closePrice());
	}
	
	private boolean isReal(KerTick tick) {
		return !isPreMarket() && !isPostMarket(tick);
	}
	
	private KerError translate(CThostFtdcRspInfoField rsp) {
		return new KerError(rsp.code, rsp.message);
	}

	@Override
	public void OnFrontConnected() {
		if (this.state == EngineState.STOPPING || this.state == EngineState.STOPPED)
			return;
		
		CThostFtdcReqUserLoginField req = new CThostFtdcReqUserLoginField();
		req.BrokerID = this.config.brokerId();
		req.UserID = this.config.userId();
		req.Password = this.config.password();
		req.UserProductInfo = this.config.userProductInfo();
		// Send login request.
		checkRtnCode("request login", this.mdApi.ReqUserLogin(req, Utils.increaseGet()));
	}

	@Override
	public void OnFrontDisconnected(int reason) {
		this.listener.error(new KerError(reason, "Trader disconnected: " + reason));
	}

	@Override
	public void OnRspError(CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {
		this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspUserLogin(CThostFtdcRspUserLoginField rspUserLogin, CThostFtdcRspInfoField rspInfo, int requestId,
			boolean isLast) {
		if (rspInfo.code == 0)
			changeState(EngineState.STARTED);
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspUserLogout(CThostFtdcUserLogoutField userLogout, CThostFtdcRspInfoField rspInfo, int nRequestID,
			boolean isLast) {
		if (rspInfo.code == 0)
			changeState(EngineState.STOPPED);
		else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspSubMarketData(CThostFtdcSpecificInstrumentField specificInstrument, CThostFtdcRspInfoField rspInfo,
			int requestId, boolean isLast) {
		if (rspInfo.code == 0) {
			this.subscribed.add(specificInstrument.InstrumentID);
			if (isLast) {
				try {
					for (var symbol : this.context.info().symbols())
						if (!this.subscribed.contains(symbol))
							this.listener.error(new KerError("Symbol not subscribed: " + symbol));
				} catch (KerError e) {
					this.listener.error(e);
				}
				// Clear for next round.
				this.subscribed.clear();
			}
		} else
			this.listener.error(translate(rspInfo));
	}

	@Override
	public void OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField specificInstrument,
			CThostFtdcRspInfoField rspInfo, int nRequestID, boolean isLast) {
	}

	@Override
	public void OnRtnDepthMarketData(CThostFtdcDepthMarketDataField depthMarketData) {
		try {
			this.listener.tick(translate(depthMarketData));
		} catch (KerError e) {
			this.listener.error(e);
		}
	}
}
