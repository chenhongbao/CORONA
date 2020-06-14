package com.nabiki.corona.trade.core;

import java.nio.file.Path;
import java.util.List;

import com.nabiki.corona.ErrorCode;
import com.nabiki.corona.ErrorMessage;
import com.nabiki.corona.OffsetFlag;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;

public class InvestorAccount {
	private final AccountManager accountManager;
	private final PositionManager positionManager;
	private final IdKeeper idKeeper;
	private final SessionWriter sessionWriter;
	
	// Message keepers.
	private final MessageKeeper<KerTradeReport> tradeKeeper;
	private final MessageKeeper<KerOrderStatus> statusKeeper;

	private final String accountId;
	private final Path directory;
	private final TradeServiceContext context;
	private final DataCodec codec;
	private final DataFactory factory;

	public InvestorAccount(String accountId, Path dir, TradeServiceContext context, IdKeeper keeper, DataCodec codec, DataFactory factory) throws KerError {
		this.accountId = accountId;
		this.context = context;
		this.directory= dir;
		this.codec = codec;
		this.factory = factory;
		
		// Build directory to keep data files.
		var positionDir = Path.of(this.directory.toAbsolutePath().toString(), "position");
		Utils.ensureDir(positionDir);
		var accountDir = Path.of(this.directory.toAbsolutePath().toString(), "account");
		Utils.ensureDir(accountDir);
		
		// Create instances of data.
		this.idKeeper = keeper;
		this.positionManager = new PositionManager(positionDir, this.context, this.codec, this.factory);
		this.accountManager = new AccountManager(accountDir, this.context, this.positionManager, this.codec, this.factory);
		this.sessionWriter = new SessionWriter(Path.of(this.directory.toAbsolutePath().toString(), "sessions"), this.codec);
		this.tradeKeeper = new MessageKeeper<>();
		this.statusKeeper = new MessageKeeper<>();
	}
	
	public void orderStatus(KerOrderStatus status) throws KerError {
		this.statusKeeper.message(status.sessionId(), status);
		this.sessionWriter.write(status);
	}
	
	public List<KerOrderStatus> orderStatus(String sid) throws KerError {
		return this.statusKeeper.messages(sid);
	}
	
	public String accountId() {
		return this.accountId;
	}
	
	public AccountManager accountManager() {
		return this.accountManager;
	}
	
	public void moveCash(CashMove cmd) throws KerError {
		this.accountManager.accountEngine().moveCash(cmd);
	}
	
	public PositionManager position() {
		return this.positionManager;
	}
	
	public void settle() throws KerError {
		this.positionManager.settle();
		this.accountManager.settle();
	}
	
	public void init() throws KerError {
		this.positionManager.init();
		this.accountManager.init();
	}
	
	public List<KerTradeReport> trades(String sid) throws KerError {
		return this.tradeKeeper.messages(sid);
	}

	public void trade(KerTradeReport rep) throws KerError {
		if (rep == null)
			throw new KerError("Can't process trade report of null pointer.");
		
		// Set session ID.
		var sid = this.idKeeper.getSessionIdWithOrderId(rep.orderId());
		rep.sessionId(sid);
		
		var positionEngine = this.positionManager.getPositon(rep.symbol());
		if (positionEngine == null)
			throw new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND);
		
		if (rep.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
			// Add new position, then unlocked the margin.
			// What happens in real is to move the money from account's available to used margin of position.
			positionEngine.trade(rep);
			this.accountManager.accountEngine().trade(rep);
		} else {
			// Remove locked position to closed position.
			// What happens in real is to reduce the used margin, then account's available is increased thereby.
			positionEngine.trade(rep);
		}
		
		// Save trades.
		this.tradeKeeper.message(rep.sessionId(), rep);
		this.sessionWriter.write(rep);
	}
	
	public void cancel(KerOrderStatus order) throws KerError {
		if (order == null)
			throw new KerError("Can't cancel order of null pointer.");
		
		var sid = this.idKeeper.getSessionIdWithOrderId(order.orderId());
		
		if (order.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
			this.accountManager.accountEngine().cancel(sid);
		} else {
			// Other flags are closing order.
			var positionEngine = this.positionManager.getPositon(order.symbol());
			if (positionEngine == null) {
				throw new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND);
			} else {
				positionEngine.cancel(sid);
			}
		}
	}
	
	/**
	 * Check the validity of the order.
	 * 
	 * @param order order
	 * @return evaluation result
	 * @throws KerError
	 */
	public KerOrderEvalue allocateOrder(KerOrder order) throws KerError {
		if (order == null) {
			throw new KerError("Can't allocate for order of null pointer.");
		}
		// Don't insert order if the information for the denoted instrument is not ready.
		if (!this.context.info().ready(order.symbol)) {
			var r = this.factory.create(KerOrderEvalue.class);
			r.error(new KerError(ErrorCode.NOT_INITED, ErrorMessage.NOT_INITED));
			return r;
		}

		KerOrderEvalue r = null;
		// Create session ID.
		var sid = this.idKeeper.getSessionIdWithOrderId(order.orderId());
		order.sessionId(sid);
		
		// Save order.
		this.sessionWriter.write(order);
		
		if (order.offsetFlag() == OffsetFlag.OFFSET_OPEN) {
			r = validateOpen(order);
		} else {
			r = validateClose(order);
		}
		
		// Set default NONE error, says it is OK.
		if (r.error() == null)
			r.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		
		// Return trade session ID for verified order.
		if (r.error().code() == ErrorCode.NONE)
			r.sessionId(sid);
		
		return r;
	}

	private KerOrderEvalue validateClose(KerOrder order) throws KerError {
		KerOrderEvalue eval = this.factory.create(KerOrderEvalue.class);
		
		var positionEngine = this.positionManager.getPositon(order.symbol());
		if (positionEngine == null) {
			eval.error( new KerError(ErrorCode.INSTRUMENT_NOT_FOUND, ErrorMessage.INSTRUMENT_NOT_FOUND));
		} else {
			try {
				eval.positionsToClose(positionEngine.lock(order));
			} catch (KerError e) {
				eval.error(e);
			}
		}
		
		return eval;
	}

	private KerOrderEvalue validateOpen(KerOrder order) throws KerError {
		var eval = this.accountManager.accountEngine().lock(order);
		if (eval.error() == null)
			eval.error(new KerError(ErrorCode.NONE, ErrorMessage.NONE));
		return eval;
	}
}
