package com.nabiki.corona.trade.core;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class InvestorManager {
	private final RuntimeInfo runtime;
	private final DataFactory factory;
	private final SessionManager sm;
	private final Map<String, InvestorAccount> investors = new ConcurrentHashMap<>();
	
	private final static Path root = Path.of(".", "investor");

	public InvestorManager(RuntimeInfo info, DataFactory factory, SessionManager sm) throws KerError {
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
	
	public InvestorAccount getInvestor(String accountId) {
		return this.investors.get(accountId);
	}
	
	public void setInvestor(String accountId) throws KerError {
		var np = Path.of(this.root.toAbsolutePath().toString(), accountId);
		this.investors.put(accountId, new InvestorAccount(accountId, np, this.runtime, this.factory, this.sm));
	}
}
