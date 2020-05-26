package com.nabiki.corona.portal.inet;

import java.net.Socket;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.system.api.KerError;

public class MdService extends PacketService implements Runnable {

	public MdService(Socket client, ClientInputExecutor exec) throws KerError {
		super(client, exec);
	}

	@Override
	public void run() {
		while (!super.isClosed()) {
			try {
				var packet = super.receive();
				switch (packet.type()) {
				case MessageType.TX_SET_CLIENT_SUBSCRIBE_SYMBOLS:
					this.execute(packet);
					break;
				default:
					throw new KerError("Unknown message type: " + packet.type());
				}
			} catch (KerError e) {
				// Send error message to peer and exit loop.
				super.sendError(e);
				break;
			}
		}
		
		if (!super.isClosed())
			super.close();
	}

}
