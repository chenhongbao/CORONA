package com.nabiki.corona.kernel.settings.api;

import java.util.List;

public interface NativeExecutableInfo {
	String title();
	
	String executablePath();
	
	String workingDirectory();
	
	List<ArgPair> args();
}
