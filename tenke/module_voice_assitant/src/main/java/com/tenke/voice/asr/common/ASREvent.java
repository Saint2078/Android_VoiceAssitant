package com.tenke.voice.asr.common;

public class ASREvent {
    private final String name;
    private final String params;
    private final byte[] data;
    private final int offset;
    private final int length;

    public ASREvent(String type, String params, byte[] data, int offset, int length){
        this.name = type;
        this.params = params;
        this.data = data;
        this.offset = offset;
        this.length = length;
    }

    public int getOffset() {
        return offset;
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getParams() {
        return params;
    }

    public byte[] getData() {
        return data;
    }
}
