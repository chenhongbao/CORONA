package com.nabiki.corona.portal.inet;

import java.net.Socket;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.system.api.KerError;

public class ClientService extends PacketService implements Runnable {

	public ClientService(Socket client, ClientInputExecutor exec) throws KerError {
		super(client, exec);
	}

	@Override
	public void run() {
		while (!super.isClosed()) {
			try {
				var packet = super.receive();
				switch (packet.type()) {
				case MessageType.TX_QUERY_CLIENT_ACCOUNT:
				case MessageType.TX_QUERY_CLIENT_ORDER_STATUS:
				case MessageType.TX_QUERY_CLIENT_POSITION_DETAIL:
				case MessageType.TX_QUERY_CLIENT_LIST_SESSION_ID:
				case MessageType.TX_REQUEST_CLIENT_ACTION:
				case MessageType.TX_REQUEST_CLIENT_ORDER:
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
