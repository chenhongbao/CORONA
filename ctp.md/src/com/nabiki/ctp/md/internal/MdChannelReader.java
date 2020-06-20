package com.nabiki.ctp.md.internal;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.nabiki.ctp.md.*;
import com.nabiki.ctp.md.struct.*;

public class MdChannelReader {
	private final static long WAIT_MILLIS = 1000;

	private final int channelId;
	private final CThostFtdcMdSpi spi;
	private final Thread readerThread, callbackThread;
	private final BlockingQueue<MdChannelData> bQueue = new LinkedBlockingQueue<>();

	// Stop marker.
	private AtomicBoolean stopped;
	
	private String tradingDay;

	// Same code with trader channel reader.
	public MdChannelReader(int channelId, CThostFtdcMdSpi spi) {
		if (spi == null)
			throw new NullPointerException("SPI null pointer.");

		this.channelId = channelId;
		this.spi = spi;
		this.stopped = new AtomicBoolean(false);

		// Threads.
		this.readerThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					MdNatives.WaitOnChannel(this.channelId, WAIT_MILLIS);
					
					var data = new MdChannelData();
					// Read channel data.
					MdNatives.ReadChannel(this.channelId, data);
					if (!this.bQueue.offer(data, WAIT_MILLIS, TimeUnit.MILLISECONDS)) {
						onErrorRsp(ErrorCodes.NO_SPACE, "Blocking queue's offer timeout.", 0, true);
					}
				} catch (InterruptedException e) {
					if (!this.stopped.get())
						onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
				} catch (Throwable th) {
					onErrorRsp(ErrorCodes.JVM_INTERNAL, th.getMessage(), 0, true);
				}
			}
		});

		this.callbackThread = new Thread(() -> {
			while (!Thread.currentThread().isInterrupted()) {
				try {
					onChannelData(this.bQueue.poll(WAIT_MILLIS, TimeUnit.MILLISECONDS));
				} catch (InterruptedException e) {
					if (!this.stopped.get())
						onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
				} catch (Throwable th) {
					onErrorRsp(ErrorCodes.UNNO_THROW, th.getMessage(), 0, true);
				}
			}

			this.readerThread.interrupt();
			try {
				this.readerThread.join(WAIT_MILLIS * 2);
			} catch (InterruptedException e) {
				onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
			}
		});
	}
	
	public String tradingDay() {
		return this.tradingDay;
	}

	private void onErrorRsp(int code, String message, int requestId, boolean isLast) {
		var rsp = new CThostFtdcRspInfoField();
		rsp.code = code;
		rsp.message = message;
		try {
			this.spi.OnRspError(rsp, requestId, isLast);
		} catch (Throwable th) {
		}
	}

	private void onChannelData(MdChannelData data) {
		if (data == null)
			return;

		try {
			for (var m : data.ListRtnDepthMarketData)
				this.spi.OnRtnDepthMarketData(m);
			
			for (@SuppressWarnings("unused") var m : data.ListConnect)
				this.spi.OnFrontConnected();

			for (var m : data.ListDisconnect)
				this.spi.OnFrontDisconnected(m.Reason);

			for (var m : data.ListRspUserLogin) {
				this.spi.OnRspUserLogin(m.RspUserLogin, m.RspInfo, m.RequestId, m.IsLast);
				this.tradingDay = m.RspUserLogin.TradingDay;
			}

			for (var m : data.ListRspUserLogout) {
				this.spi.OnRspUserLogout(m.UserLogout, m.RspInfo, m.RequestId, m.IsLast);
				this.tradingDay = null;
			}
						
			for (var m : data.ListRspSubMarketData)
				this.spi.OnRspSubMarketData(m.SpecificInstrument, m.RspInfo, m.RequestId, m.IsLast);
			
			for (var m : data.ListRspUnSubMarketData)
				this.spi.OnRspUnSubMarketData(m.SpecificInstrument, m.RspInfo, m.RequestId, m.IsLast);
		} catch (Throwable th) {
			onErrorRsp(ErrorCodes.UNNO_THROW, th.getMessage(), 0, true);
		}
	}

	public void stop() {
		if (this.stopped.get())
			return;
		// Stop threads.
		this.stopped.set(true);
		this.callbackThread.interrupt();

		try {
			this.callbackThread.join(WAIT_MILLIS * 4);
		} catch (InterruptedException e) {
			onErrorRsp(ErrorCodes.UNNO_INTERRUPTED, e.getMessage(), 0, true);
		}
	}
}
