package com.nabiki.corona.portal.inet;

import java.net.Socket;

import com.nabiki.corona.system.api.KerError;

public class PacketAdminService extends PacketService implements Runnable {

	public PacketAdminService(Socket client, ClientInputExecutor exec) throws KerError {
		super(client, exec);
	}

	@Override
	public void run() {
		// TODO receive admin input
	}

	
}
