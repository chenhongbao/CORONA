package com.nabiki.corona.kernel.settings.api;

import java.util.Collection;

public interface BrokerAccount {
	Collection<String> connetionStrings();

	String user();

	String password();

	String broker();

	String clientId();

	String authCode();

	String note();
}
