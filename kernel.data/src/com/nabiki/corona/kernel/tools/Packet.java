package com.nabiki.corona.kernel.tools;

public class Packet {
    public static class Type {
        public static short CLOSE = 0;
        public static short EMPTY = 1;
        public static short PLAIN_TXT = 2;
    }

    private short type;
    private byte[] bytes;

    public Packet(short type, byte[] bytes) {
        this.type = type;
        this.bytes = bytes;
    }

    public int type() {
        return this.type;
    }

    public byte[] bytes() {
        return this.bytes;
    }
}
