package com.nabiki.corona.trade.core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.DataCodec;
import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.api.KerAccount;
import com.nabiki.corona.kernel.api.KerError;
import com.nabiki.corona.kernel.data.DefaultDataCodec;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class AccountManager {
	private final Path root;
	private AccountEngine account;
	private final RuntimeInfo runtime;
	private final PositionManager positions;
	private final DataFactory factory;
	private final DataCodec codec = DefaultDataCodec.create();

	public AccountManager(Path path, RuntimeInfo runtime, PositionManager pos, DataFactory factory) {
		this.root = path;
		this.runtime = runtime;
		this.positions = pos;
		this.factory = factory;
		this.account = new AccountEngine(null, this.runtime, this.positions, this.factory);
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
	@SuppressWarnings("unused")
	private KerAccount readAccount(Path p) throws KerError {
		String[] fs = Utils.getFileNames(p, true);
		if (fs == null)
			throw new KerError("No account data file found.");
		if (fs.length > 1)
			throw new KerError("Ambiguious account data files.");

		var path = Path.of(p.toAbsolutePath().toString(), fs[0]);
		if (!path.toFile().canWrite())
			throw new KerError("Account data file not readable: " + path.toAbsolutePath().toString());

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
