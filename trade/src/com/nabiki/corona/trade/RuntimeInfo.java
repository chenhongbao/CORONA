package com.nabiki.corona.trade;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.log.Logger;
import org.osgi.service.log.LoggerFactory;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;
import com.nabiki.corona.kernel.biz.api.TickLocal;

@Component(service = {})
public class RuntimeInfo {
	@Reference(service = LoggerFactory.class)
	private Logger log;

	// Last ticks.
	@Reference(bind = "bindTickLocal", updated = "updatedTickLocal", unbind = "unbindTickLocal")
	private volatile Collection<TickLocal> ticks = new ConcurrentSkipListSet<>();

	public void bindTickLocal(TickLocal local) {
		if (local == null)
			return;

		this.ticks.add(local);
		this.log.info("Bind local tick: {}.", local.name());
	}

	public void updatedTickLocal(TickLocal local) {
		if (local == null)
			return;

		if (this.ticks.contains(local)) {
			this.log.info("Update local tick: {}.", local.name());
		}
	}

	public void unbindTickLocal(TickLocal local) {
		if (local == null)
			return;

		this.ticks.remove(local);
		this.log.info("Unbind local tick: {}.", local.name());
	}

	// Information keepers.
	private final Map<String, KerInstrument> instruments = new ConcurrentHashMap<>();
	private final Map<String, KerMargin> margins = new ConcurrentHashMap<>();
	private final Map<String, KerCommission> commissions = new ConcurrentHashMap<>();

	public RuntimeInfo() {
	}

	/**
	 * Check if the information of the given symbol are all ready.
	 * 
	 * @param symbol symbol
	 * @return true if all info are ready, false otherwise.
	 */
	public boolean ready(String symbol) {
		return instrument(symbol) != null && margin(symbol) != null && commission(symbol) != null
				&& lastTick(symbol) != null;
	}

	public void instrument(KerInstrument in) {
		if (in == null || in.symbol() == null)
			this.log.warn("kernel instrument null pointer.");

		this.instruments.put(in.symbol(), in);
	}

	public void margin(KerMargin margin) {
		if (margin == null || margin.symbol() == null)
			this.log.warn("Kenerl margin null pointer.");

		this.margins.put(margin.symbol(), margin);
	}

	public void commission(KerCommission comm) {
		if (comm == null || comm.symbol() == null)
			this.log.warn("Kenerl commission null pointer.");

		this.commissions.put(comm.symbol(), comm);
	}

	public KerInstrument instrument(String symbol) {
		return this.instruments.get(symbol);
	}

	public KerMargin margin(String symbol) {
		return this.margins.get(symbol);
	}

	public KerCommission commission(String symbol) {
		return this.commissions.get(symbol);
	}

	public Tick lastTick(String symbol) {
		Tick ret = null;
		for (var t : ticks) {
			ret = t.last(symbol);
			if (ret != null)
				break;
		}

		return ret;
	}
}
