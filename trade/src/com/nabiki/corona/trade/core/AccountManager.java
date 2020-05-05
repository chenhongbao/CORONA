package com.nabiki.corona.trade.core;

import java.nio.file.Path;

import com.nabiki.corona.kernel.api.DataFactory;
import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class AccountManager {
	private final Path path;

	public AccountManager(Path path, RuntimeInfo runtime, PositionManager pos, DataFactory factory) {
		this.path = path;
		// TODO initialize account
	}

	public AccountEngine account() {
		// TODO return account managed by this manager
		return null;
	}
}
