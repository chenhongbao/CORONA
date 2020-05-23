package com.nabiki.corona.object.tool;

public class Packet {
    private short type;
    private byte[] bytes;

    public Packet(short type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    public short type() {
        return this.type;
    }

    public byte[] bytes() {
        return this.bytes;
    }
}
