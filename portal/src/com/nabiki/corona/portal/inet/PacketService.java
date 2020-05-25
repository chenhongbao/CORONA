package com.nabiki.corona.portal.inet;

import java.net.Socket;

import com.nabiki.corona.object.tool.Packet;
import com.nabiki.corona.object.tool.PacketSocket;
import com.nabiki.corona.system.api.KerError;

/**
 * Provide common operations on client input and output.
 */
public class PacketService {
	private final PacketSocket client;
	private final ClientInputExecutor executor;
	
	public PacketService(Socket client, ClientInputExecutor exec) throws KerError {
		this.client = new PacketSocket(client);
		this.executor = exec;
	}
	
	public void send(short type, byte[] bytes, int offset, int length) throws KerError {
		this.client.send(type, bytes, offset, length);
	}
	
	public Packet receive() throws KerError {
		return this.client.receive();
	}
	
	public void execute(Packet input) {
		this.executor.input(input, this);
	}
	
	public void close() {
		this.client.close();
	}
}
