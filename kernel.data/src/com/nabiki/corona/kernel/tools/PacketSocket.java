package com.nabiki.corona.kernel.tools;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

import com.nabiki.corona.kernel.api.KerError;

public class PacketSocket {
    private final Socket socket;
    private final DataInputStream input;
    private final DataOutputStream output;
    public PacketSocket(Socket s) throws KerError {
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

    public byte[] receive() throws KerError {
        if (socket().isInputShutdown())
            throw new KerError("Can't receive packet from an input shutdown socket.");

        int length;
        try {
            length = this.input.readInt();
        } catch (IOException e) {
            throw new KerError("Fail reading packet length.");
        }

        if (length == 0) {
            // Normal close.
            passiveClose(length);
            return new byte[0];
        } else if (length < 0) {
            // Abnormal close.
            passiveClose(length);
            throw new KerError("Peer abnormal close.");
        }

        int actual = 0;
        byte[] ret;
        try {
            ret = new byte[length];
            while (actual < length) {
                var r = this.input.read(ret, actual, length - actual);
                actual += r;
            }
        } catch (IOException e) {
            throw new KerError("Fail reading packet body.");
        }

        return ret;
    }

    public void send(byte[] bytes, int offset, int length) throws KerError {
        if (bytes == null)
            throw new KerError("Bytes' array null pointer.");
        if (offset < 0 || length <= 0)
            throw new KerError("Invalid parameters.");
        if (bytes.length < offset + length)
            throw new KerError("Given bytes' array insufficient data.");

        try {
            this.output.writeInt(length);
            this.output.write(bytes, offset, length);
            this.output.flush();
        } catch (IOException e) {
            throw new KerError("Fail sending packet.");
        }
    }

    public void close(int errorCode) {
        try {
            // Ensure negative or zero code.
            var sndError = Math.min(0, errorCode);
            this.output.writeInt(sndError);
            // Wait for peer's response and then close.
            this.input.readInt();
            this.socket.close();
        } catch (IOException e) {
        }
    }

    private void passiveClose(int errorCode) {
        if (this.socket.isClosed())
            return;

        try {
            this.output.writeInt(Math.min(0, errorCode));
            this.socket.close();
        } catch (IOException e) {
        }
    }
}
