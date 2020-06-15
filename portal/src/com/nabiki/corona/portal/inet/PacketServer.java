package com.nabiki.corona.portal.inet;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.system.Utils;
import com.nabiki.corona.object.*;
import com.nabiki.corona.system.api.*;
import com.nabiki.corona.object.tool.*;
import com.nabiki.corona.portal.core.LoginManager;
import com.nabiki.corona.system.packet.api.*;

/**
 * Provide common operations on client input and output.
 */
public class PacketServer {
	private final PacketSocket client;
	private final ClientInputExecutor executor;

	// Login mark.
	private boolean logined;
	private KerLogin login;
	
	// Write packet to files.
	private final Path root = Path.of(".", "packet");
	private final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSSSSS");

	public PacketServer(Socket client, ClientInputExecutor exec) throws KerError {
		this.client = new PacketSocket(client);
		this.executor = exec;
		// Set mark.
		this.logined = false;
		// Create packet dir.
		Utils.ensureDir(this.root);
	}

	public void send(short type, byte[] bytes, int offset, int length) throws KerError {
		this.client.send(type, bytes, offset, length);
	}

	public Packet receive() throws KerError {
		var r = this.client.receive();
		// Write packet before input into executor.
		// It is very important to save every received packet for future verification.
		writePacket(r);
		return r;
	}

	public void execute(Packet input) {
		this.executor.input(input, this, this.login);
	}

	public void close() {
		this.client.close();
	}

	public boolean isClosed() {
		return this.client.socket().isClosed() || !this.client.socket().isConnected();
	}

	public void sendError(KerError e) {
		try {
			var r = DefaultDataFactory.create().create(RxErrorMessage.class);

			// Set values.
			r.value(e);
			r.last(true);
			r.time(LocalDateTime.now());
			r.error(new KerError("Please check payload for error details."));
			r.responseSeq(Utils.increaseGet());

			// Encode.
			var bytes = DefaultDataCodec.create().encode(r);
			send(MessageType.RX_ERROR, bytes, 0, bytes.length);
		} catch (KerError ex) {
		}
	}
	
	public KerLogin getLogin() {
		return this.login;
	}
	
	private void writePacket(Packet packet) {
		var name = LocalDateTime.now().format(this.format) + "_" + packet.type() + ".json";
		var file = Path.of(this.root.toAbsolutePath().toString(), name).toFile();
		
		try (FileOutputStream os = new FileOutputStream(file)) {
			os.write(packet.bytes());
		} catch (IOException e) {
		}
	}
	
	private boolean loginOk(KerLogin login) throws KerError {
		return LoginManager.get().loginOk(login.accountId(), login.pin(), login.role());
	}

	protected void waitLogin() throws KerError {
		while (!this.logined) {
			var packet = this.client.receive();
			if (packet.type() == MessageType.TX_MGR_REQUEST_LOGIN) {
				var reqLogin = DefaultDataCodec.create().decode(packet.bytes(), TxRequestLoginMessage.class);
				// Can't login multiple users.
				if (reqLogin.valueCount() != 1)
					throw new KerError("Unsupprted multiple users login.");
				
				// Get login result.
				this.logined = loginOk(reqLogin.value(0));
				if (!this.logined)
					throw new KerError("Login incorrect.");
				else
					this.login = reqLogin.value(0);
					
			}
		}
	}
}
