package com.nabiki.corona.kernel.settings;

import com.nabiki.corona.kernel.settings.api.RemoteConfig;

public class RemoteConfigImpl implements RemoteConfig {
	public String host;
	public int port;
	
	public RemoteConfigImpl() {}

	@Override
	public String host() {
		return this.host;
	}

	@Override
	public int port() {
		return this.port;
	}

}
