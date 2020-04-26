package com.nabiki.corona.kernel.settings.api;

import java.util.Collection;

/**
 * Broker counter account interface. Please note that the data behind the interface could change without notice. So
 * don't extract the data until you really need them and update(extract again) at next use.
 * 
 * @author Hongbao Chen
 *
 */
public interface BrokerAccount {
	Collection<String> connetionStrings();

	String user();

	String password();

	String broker();

	String clientId();

	String authCode();

	String note();
}
