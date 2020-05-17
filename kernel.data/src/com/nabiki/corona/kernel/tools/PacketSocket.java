package com.nabiki.corona.kernel.tools;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

import com.nabiki.corona.MessageType;
import com.nabiki.corona.kernel.api.KerError;

public class PacketSocket {
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;
    public PacketSocket(Socket s) throws KerError {
    	if (s == null)
    		throw new KerError("Socket null pointer.");
    	
        if (s.isClosed() || s.isInputShutdown() || s.isOutputShutdown())
            throw new KerError("Given socket incapable of duplex communication.");
        this.socket = s;

        try {
            this.input = new DataInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            throw new KerError("Can't get input stream of socket.");
        }

        try {
            this.output = new DataOutputStream(this.socket.getOutputStream());
        } catch (IOException e) {
            throw new KerError("Can't get output stream of socket.");
        }
    }

    public SocketAddress address(boolean isRemote) {
        if (isRemote)
            return this.socket.getRemoteSocketAddress();
        else
            return this.socket.getLocalSocketAddress();
    }

    public Socket socket() {
        return this.socket;
    }

    public Packet receive() throws KerError {
        if (socket().isInputShutdown())
            throw new KerError("Can't receive packet from an input shutdown socket.");

        short type;
        int length;
        try {
            type = this.input.readShort();
        } catch (IOException e) {
            throw new KerError("Fail reading packet type.");
        }

        if (type == MessageType.TX_MGR_CLOSE_CONN) {
            passiveClose();
            return new Packet(MessageType.TX_MGR_CLOSE_CONN, null);
        }

        try {
            length = this.input.readInt();
        } catch (IOException e) {
            throw new KerError("Fail reading packet length.");
        }

        if (length == 0) {
            return new Packet(MessageType.TX_MGR_EMPTY, null);
        } else if (length < 0) {
            // Abnormal close.
            passiveClose();
            throw new KerError("Peer abnormal close.");
        }

        int actual = 0;
        byte[] payload;
        try {
            payload = new byte[length];
            while (actual < length) {
                var r = this.input.read(payload, actual, length - actual);
                actual += r;
            }
        } catch (IOException e) {
            throw new KerError("Fail reading packet body.");
        }

        return new Packet(type, payload);
    }

    public void send(short type, byte[] bytes, int offset, int length) throws KerError {
        if (bytes == null)
            throw new KerError("Bytes' array null pointer.");
        if (offset < 0 || length <= 0)
            throw new KerError("Invalid parameters.");
        if (bytes.length < offset + length)
            throw new KerError("Given bytes' array insufficient data.");

        try {
            this.output.writeShort(type);
            this.output.writeInt(length);
            this.output.write(bytes, offset, length);
            this.output.flush();
        } catch (IOException e) {
            throw new KerError("Fail sending packet.");
        }
    }

    public void close() {
        try {
            // Write type and zero length.
            this.output.writeShort(MessageType.TX_MGR_CLOSE_CONN);
            this.output.writeInt(0);
            // Wait for peer's response and then close.
            this.input.readInt();
            this.socket.close();
        } catch (IOException e) {
        }
    }

    private void passiveClose() {
        if (this.socket.isClosed())
            return;

        try {
            this.output.writeShort(MessageType.TX_MGR_CLOSE_CONN);
            this.output.writeInt(0);
            this.socket.close();
        } catch (IOException e) {
        }
    }
}