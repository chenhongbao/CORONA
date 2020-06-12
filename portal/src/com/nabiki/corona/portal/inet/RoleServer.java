package com.nabiki.corona.portal.inet;

import java.net.Socket;

import com.nabiki.corona.AccountRole;
import com.nabiki.corona.MessageType;
import com.nabiki.corona.object.tool.Packet;
import com.nabiki.corona.system.api.KerError;

public class RoleServer extends PacketServer implements Runnable {

	public RoleServer(Socket client, ClientInputExecutor exec) throws KerError {
		super(client, exec);
	}

	@Override
	public void run() {
		while (true) {
			try {
				super.waitLogin();
			} catch (KerError e) {
				super.sendError(e);
				break;
			}

			// Check the availability of login info.
			if (super.getLogin() == null)
				break;

			while (!super.isClosed()) {
				try {
					var packet = super.receive();
					switch (super.getLogin().role()) {
					case AccountRole.ADMIN:
						processAdmin(packet);
						break;
					case AccountRole.MANAGER:
						processManager(packet);
						break;
					case AccountRole.TRADER:
						processTrader(packet);
						break;
					default:
						throw new KerError("Unknown account role: " + super.getLogin().role());
					}
				} catch (KerError e) {
					// Send error message to peer and exit loop.
					super.sendError(e);
					break;
				}
			}
			break;
		}

		if (!super.isClosed())
			super.close();
	}

	private void processAdmin(Packet packet) throws KerError {
		switch (packet.type()) {
		case MessageType.TX_QUERY_ADMIN_ACCOUNT:
		case MessageType.TX_QUERY_ADMIN_LIST_ACCOUNT_ID:
		case MessageType.TX_QUERY_ADMIN_ORDER_STATUS:
		case MessageType.TX_QUERY_ADMIN_POSITION_DETAIL:
		case MessageType.TX_QUERY_ADMIN_LIST_SESSION_ID:
		case MessageType.TX_QUERY_ADMIN_TRADE_REPORT:
		case MessageType.TX_REQUEST_ADMIN_ACTION:
		case MessageType.TX_REQUEST_ADMIN_ORDER:
		case MessageType.TX_SET_ADMIN_NEW_ACCOUNT:
		case MessageType.TX_SET_ADMIN_CASH_MOVE:
			this.execute(packet);
			break;
		default:
			throw new KerError("No authorized message type: " + packet.type());
		}
	}

	private void processTrader(Packet packet) throws KerError {
		switch (packet.type()) {
		case MessageType.TX_QUERY_CLIENT_ACCOUNT:
		case MessageType.TX_QUERY_CLIENT_ORDER_STATUS:
		case MessageType.TX_QUERY_CLIENT_POSITION_DETAIL:
		case MessageType.TX_QUERY_CLIENT_LIST_SESSION_ID:
		case MessageType.TX_QUERY_CLIENT_TRADE_REPORT:
		case MessageType.TX_REQUEST_CLIENT_ACTION:
		case MessageType.TX_REQUEST_CLIENT_ORDER:
			this.execute(packet);
			break;
		default:
			throw new KerError("No authorized message type: " + packet.type());
		}
	}

	private void processManager(Packet packet) throws KerError {
		switch (packet.type()) {
		case MessageType.TX_QUERY_ADMIN_ACCOUNT:
		case MessageType.TX_QUERY_ADMIN_LIST_ACCOUNT_ID:
		case MessageType.TX_QUERY_ADMIN_ORDER_STATUS:
		case MessageType.TX_QUERY_ADMIN_POSITION_DETAIL:
		case MessageType.TX_QUERY_ADMIN_LIST_SESSION_ID:
		case MessageType.TX_QUERY_ADMIN_TRADE_REPORT:
		case MessageType.TX_SET_ADMIN_NEW_ACCOUNT:
		case MessageType.TX_SET_ADMIN_CASH_MOVE:
			this.execute(packet);
			break;
		default:
			throw new KerError("No authorized message type: " + packet.type());
		}
	}
}
