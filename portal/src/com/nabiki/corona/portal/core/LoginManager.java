package com.nabiki.corona.portal.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import com.nabiki.corona.system.api.KerError;
import com.nabiki.corona.system.api.KerNewAccount;

public class LoginManager {
	private final File loginDb = Path.of(".", "pwd").toFile();
	private static final LoginManager mgr = new LoginManager();
	
	private LoginManager() {}
	
	public static LoginManager get() {
		return LoginManager.mgr;
	}
	
	private boolean duplicated(String accountId) throws KerError {
		try (BufferedReader in = new BufferedReader(new FileReader(this.loginDb))) {
			String line;
			while ((line = in.readLine()) != null) {
				var segs = line.trim().split(",");
				if (segs[0].compareTo(accountId) == 0)
					return true;
			}

			return false;
		} catch (FileNotFoundException e) {
			throw new KerError("Fail validating user login.", e);
		} catch (IOException e) {
			throw new KerError("Fail reading login db.", e);
		}
	}
	
	public void writeNewAccount(KerNewAccount acc) throws KerError {
		if (duplicated(acc.accountId()))
			throw new KerError("Duplicated account ID: " + acc.accountId());
		
		// Check file existence.
		if (!this.loginDb.exists()) {
			try {
				this.loginDb.createNewFile();
				this.loginDb.setWritable(true);
			} catch (IOException e) {
				throw new KerError("Fail creating new file: " + this.loginDb.getAbsolutePath(), e);
			}
		}
		
		// Write account pin info.
		try (FileWriter os = new FileWriter(this.loginDb, true)) {
			os.write(accountString(acc.accountId(), acc.pin(), acc.role()));
			os.flush();
		} catch (FileNotFoundException e) {
			throw new KerError("Login db not found.", e);
		} catch (IOException e) {
			throw new KerError("Fail writing account information.", e);
		}
	}
	
	private String accountString(String aid, String pin, int role) {
		return aid + "," + pin + "," + role;
	}

	public boolean loginOk(String aid, String pin, int role) throws KerError {
		try (BufferedReader in = new BufferedReader(new FileReader(this.loginDb))) {
			String line;
			while ((line = in.readLine()) != null) {
				if (accountString(aid, pin, role).compareTo(line.trim()) == 0)
					return true;
			}

			return false;
		} catch (FileNotFoundException e) {
			throw new KerError("Fail validating user login.", e);
		} catch (IOException e) {
			throw new KerError("Fail reading login db.", e);
		}
	}
}
