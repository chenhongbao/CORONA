package com.nabiki.corona.trade.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.system.Utils;
import com.nabiki.corona.system.api.*;

public class InvestorManager {
	private final TradeServiceContext context;
	private final DataFactory factory;
	private final DataCodec codec;
	private final IdKeeper sm;
	private final Map<String, InvestorAccount> investors = new ConcurrentHashMap<>();

	private final static Path root = Path.of(".", "investor");
	private final static Path adminRoot = Path.of(".", "admin");

	public InvestorManager(TradeServiceContext context, IdKeeper sm, DataCodec codec, DataFactory factory) throws KerError {
		this.context = context;
		this.sm = sm;
		this.codec = codec;
		this.factory = factory;
		loadInvestors(InvestorManager.root);
	}

	private void loadInvestors(Path p) throws KerError {
		var names = Utils.getFileNames(p, false);
		if (names == null)
			throw new KerError("Investor data path not exists: " + p.toAbsolutePath().toString());

		for (var n : names) {
			var np = Path.of(p.toAbsolutePath().toString(), n);
			this.investors.put(n, new InvestorAccount(n, np, this.context, this.sm, this.codec, this.factory));
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

	public boolean checkPosition(List<KerPositionDetail> remote) throws KerError {
		writeAdminPosition(remote, InvestorManager.adminRoot);
		// TODO Check consistency of positions. Consider whether need to check here.
		//      If there are more than on environment sharing the same account, mustn't be matched.
		return true;
	}

	private void writeAdminPosition(List<KerPositionDetail> remote, Path root) throws KerError {
		// Ensure directory.
		Utils.ensureDir(root);

		int count = 0;
		for (var p : remote) {
			var fn = p.symbol() + "." + p.openDate().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "." + (count++)
					+ ".json";
			var path = Path.of(root.toAbsolutePath().toString(), fn);

			try (OutputStream os = new FileOutputStream(path.toFile())) {
				os.write(codec.encode(p));
				os.flush();
			} catch (IOException e) {
				throw new KerError("Fail creating file when saving position: " + path.toAbsolutePath().toString(), e);
			}
		}
	}

	public boolean checkAccount(KerAccount acc) throws KerError {
		writeAdminAccount(acc, InvestorManager.adminRoot);
		// TODO Check consistency of account.
		return true;
	}

	private void writeAdminAccount(KerAccount acc, Path root) throws KerError {
		// Ensure directory.
		Utils.ensureDir(root);

		var path = Path.of(root.toAbsolutePath().toString(), "account.json");
		try (OutputStream os = new FileOutputStream(path.toFile())) {
			os.write(codec.encode(acc));
			os.flush();
		} catch (IOException e) {
			throw new KerError("Fail creating file when saving account: " + path.toAbsolutePath().toString(), e);
		}
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
		this.investors.put(accountId, new InvestorAccount(accountId, np, this.context, this.sm, this.codec, this.factory));
	}
}
