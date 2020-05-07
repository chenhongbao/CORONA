package com.nabiki.corona.trade.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.DefaultDataCodec;
import com.nabiki.corona.kernel.api.DataCodec;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class AccountManager {
	private final Path root;
	private AccountEngine account;
	private final RuntimeInfo runtime;
	private final PositionManager positions;
	private final DataFactory factory;
	private final DataCodec codec = DefaultDataCodec.create();

	public AccountManager(Path path, RuntimeInfo runtime, PositionManager pos, DataFactory factory) throws KerError {
		this.root = path;
		this.runtime = runtime;
		this.positions = pos;
		this.factory = factory;
		
		// Try loading account from disk.
		var a = readAccount(this.root);
		this.account = new AccountEngine(a, this.runtime, this.positions, this.factory);
	}

	public AccountEngine account() {
		return this.account;
	}

	public void settle() throws KerError {
		if (!this.positions.isSettled())
			throw new KerError("Can't settle account before positions are all settled.");

		writeAccount(this.root, this.account.current());
	}
	
	public void init() throws KerError {
		this.account.init();
	}

	// Currently no need to read account from file.
	private KerAccount readAccount(Path p) throws KerError {
		var path = Path.of(root.toAbsolutePath().toString(), "0");
		if (!path.toFile().exists() || !path.toFile().canRead())
			return null;

		var bytes = Utils.readFile(path);
		return this.codec.decode(bytes, KerAccount.class);
	}
	
	private void writeAccount(Path p, KerAccount a) throws KerError {
		var bytes = this.codec.encode(a);
		var path = Path.of(p.toAbsolutePath().toString(), "0");

		try {
			if (path.toFile().exists())
				Files.delete(path);
		} catch (IOException e) {
			throw new KerError("Fail deleting old account data file: " + path.toAbsolutePath());
		}
		
		try {
			var os = new FileOutputStream(path.toFile());
			os.write(bytes);
			os.flush();
			os.close();
		} catch (IOException e) {
			throw new KerError("Fail writing account data to file: " + path.toAbsolutePath());
		}
	}
}
