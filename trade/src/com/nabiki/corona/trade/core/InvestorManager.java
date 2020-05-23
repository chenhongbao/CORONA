package com.nabiki.corona.trade.core;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.DataFactory;
import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.info.api.RuntimeInfo;

public class InvestorManager {
	private final RuntimeInfo runtime;
	private final DataFactory factory;
	private final IdKeeper sm;
	private final Map<String, InvestorAccount> investors = new ConcurrentHashMap<>();
	
	private final static Path root = Path.of(".", "investor");

	public InvestorManager(RuntimeInfo info, DataFactory factory, IdKeeper sm) throws KerError {
		this.runtime= info;
		this.factory = factory;
		this.sm = sm;
		loadInvestors(InvestorManager.root);
	}

	private void loadInvestors(Path p) throws KerError {
		var names = Utils.getFileNames(p, false);
		if (names == null)
			throw new KerError("Investor data path not exists: " + p.toAbsolutePath().toString());
		
		for (var n : names) {
			var np = Path.of(p.toAbsolutePath().toString(), n);
			this.investors.put(n,  new InvestorAccount(n, np, this.runtime, this.factory, this.sm));
		}
	}
	
	public void settle() throws KerError {
		for (var i : this.investors.values())
			i.settle();
	}
	
	public void init() throws KerError {
		for (var i : this.investors.values())
			i.init();
	}
	
	/**
	 * Get investor with the given account ID.
	 * 
	 * @param accountId account ID
	 * @return investor
	 */
	public InvestorAccount getInvestor(String accountId) {
		return this.investors.get(accountId);
	}
	
	/**
	 * Get all investors.
	 * 
	 * @return collection of all investors.
	 */
	public Collection<InvestorAccount> getInvestors() {
		return this.investors.values();
	}
	
	public void setInvestor(String accountId) throws KerError {
		var np = Path.of(InvestorManager.root.toAbsolutePath().toString(), accountId);
		this.investors.put(accountId, new InvestorAccount(accountId, np, this.runtime, this.factory, this.sm));
	}
}
