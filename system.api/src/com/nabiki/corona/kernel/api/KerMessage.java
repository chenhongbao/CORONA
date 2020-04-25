package com.nabiki.corona.kernel.api;

import java.util.Collection;

public interface KerMessage {
	String messageType();
	
	void messageType(String s);
	
	String ackType();
	
	void ackType(String s);
	
	byte[] element(int i);
	
	int bodySize();
	
	void addElement(byte[] b);
	
	Collection<byte[]> body();
	
	void errorCode(int i);
	
	int errorCode();
	
	void errorMessage(String s);
	
	String errorMessage();
}
