package com.nabiki.corona.kernel.settings.api;

import java.util.List;

/**
 * Executable info interface. Please note that the data behind the interface could change without notice. So
 * don't extract the data until you really need them and update(extract again) at next use.
 * 
 * @author Hongbao Chen
 *
 */
public interface NativeExecutableInfo {
	String title();

	String executablePath();

	String workingDirectory();

	List<ArgPair> args();
}
