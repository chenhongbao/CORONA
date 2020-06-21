package com.nabiki.ctp.trader;
import com.nabiki.ctp.trader.struct.*;

public abstract class CThostFtdcTraderApi {

	protected CThostFtdcTraderApi() {
	}
	
	public static CThostFtdcTraderApi CreateFtdcTraderApi(String szFlowPath) {
		return new CThostFtdcTraderApiImpl(szFlowPath);
	}
	
	/**
	 * Get API version.
	 * 
	 * @return API version string
	 */
	public abstract String GetApiVersion();
	
	/**
	 * Get trading day in string. If the session is not logined, the method returns null.
	 * 
	 * @return trading day string
	 */
	public abstract String GetTradingDay();
	
	/**
	 * Initialize session to remote counter. It is not logined yet after the method returns.
	 */
	public abstract void Init();
	
	/**
	 * Wait untilt the session is released. The method returns after {@code Release()} is called.
	 */
	public abstract void Join();
	
	/**
	 * Set subscription type for private topic.
	 * 
	 * @param type constants defined in {@link ThostTeResumeType}
	 */
	public abstract void SubscribePrivateTopic(int type);
	
	/**
	 * Set subscription type for public topic.
	 * 
	 * @param type constants defined in {@link ThostTeResumeType}
	 */
	public abstract void SubscribePublicTopic(int type);
	
	/**
	 * Register fron address for the session. Client can have more than one front addresses and the native client
	 * chooses randomly to connect the remote counter.
	 * 
	 * @param frontAddress front address in the format {@code tcp://127.0.0.1:40010}
	 */
	public abstract void RegisterFront(String frontAddress);
	
	/**
	 * Register SPI for the trader session.
	 * 
	 * @param spi callback SPI for responses
	 */
	public abstract void RegisterSpi(CThostFtdcTraderSpi spi);
	
	/**
	 * Release the native resources for the trader session.
	 */
	public abstract void Release();
	
    /**
     * Request client authentication for the specified session. {@code OnRspAuthenticate} is called on the authentication
     * response.
     * 
     * <p>The method doesn't throw exception.
     * 
     * @param reqAuthenticateField authentication request
     * @param requestId identifier for this request
     * @return returned value from native function
     * <ul>
     * <li>0, successful sending
     * <li>-1, network failure
     * <li>-2, too many requests that are not processed
     * <li>-3, too many requests in last second
     * </ul>
     */
	public abstract int ReqAuthenticate(CThostFtdcReqAuthenticateField reqAuthenticateField, int requestId);
	
    /**
     * Request client login for the specified session. {@code OnRspUserLogin} is called on login response.
     * 
     * <p>The method doesn't throw exception.
     * 
     * @param reqUserLoginField login request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId);
	
    /**
     * Request client logout for the specified session. {@code OnRspUserLogout} is called on logout response.
     * 
     * <p>The method doesn't throw exception.
     * 
     * @param userLogout logout request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId);
	
	/**
	 * Request settlment confirm. {@code OnRspSettlementInfoConfirm} is called on response.
	 * 
	 * @param settlementInfoConfirm confirm request
	 * @param requestId identifier for this request
	 * @return returned value from native function
	 */
	public abstract int ReqSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField settlementInfoConfirm, int requestId);
	
    /**
     * Request inserting order for the specified session. Different methods are called on different errors or response.
     * <ul>
     * <li>{@code OnErrRtnOrderInsert} or {@code OnRspOrderInsert} is callled on incorrect fields in request.
     * <li>{@code OnRtnOrder} is called on order status update.
     * <li>{@code OnRtnTrade} is called on trade update.
     * </ul>
     * 
     * @param inputOrder order request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqOrderInsert(CThostFtdcInputOrderField inputOrder, int requestId);
	
    /**
     * Request cancelling an existing order from the specified session. There are two ways to cancel an order:
     * <ul>
     * <li>{@code OrderSysID}, the field is in order status update after execution of an order
     * <li>{@code FrontID + SessionID + OrderRef}, order reference is maintained by client and the previous two fields
     * 		are in successful login response, or in order status update.
	 * </ul>
	 * <p>Different methods are called on different errors or response:
	 * <ul>
	 * <li>{@code OnErrRtnOrderAction} or {@code OnRspOrderAction} is called on incorrect fields in action request.
	 * <li>{@code OnRtnOrder} is called on order status update.
     * </ul>
     * 
     * @param inputOrderAction action request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqOrderAction(CThostFtdcInputOrderActionField inputOrderAction, int requestId);
	
    /**
     * Request query instrument information of the specified session. {@code OnRspQryInstrument} is called on response.
     * 
     * @param qryInstrument query request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqQryInstrument(CThostFtdcQryInstrumentField qryInstrument, int requestId);
	
    /**
     * Request query commission rate from the specified session. {@code OnRspQryInstrumentCommissionRate} is called on
     * response.
     * 
     * @param qryInstrumentCommissionRate query request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqQryInstrumentCommissionRate(CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate, int requestId);
	
    /**
     * Request query margin rate from the specified session. {@code OnRspQryInstrumentMarginRate} is called on response.
     * 
     * @param qryInstrumentMarginRate query request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqQryInstrumentMarginRate(CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate, int requestId);
	
    /**
     * Request query trading account for the login user. {@code OnRspQryTradingAccount} is called on response.
     * 
     * @param qryTradingAccount query request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqQryTradingAccount(CThostFtdcQryTradingAccountField qryTradingAccount, int requestId);
	
    /**
     * Request query position detail for the login user. {@code OnRspQryInvestorPositionDetail} is called on response.
     * 
     * @param qryInvestorPositionDetail query request
     * @param requestId identifier for this request
     * @return returned value from native function
     */
	public abstract int ReqQryInvestorPositionDetail(CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail, int requestId);
}
