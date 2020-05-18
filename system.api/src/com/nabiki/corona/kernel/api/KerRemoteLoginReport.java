package com.nabiki.corona.kernel.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public interface KerRemoteLoginReport {
	boolean isLogin();
	
	void isLogin(boolean b);
	
	LocalDate tradingDay();
	
	LocalDateTime loginTime();
	
	String brokerId();
	
	String userId();
	
	String systemName();
	
	int frontId();
	
	int sessionId();
	
	int maxOrderReference();
	
	LocalTime shfeTime();
	
	LocalTime dceTime();
	
	LocalTime czcecTime();
	
	LocalTime ffexTime();
	
	LocalTime ineTime();
}
