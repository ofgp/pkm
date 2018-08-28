package com.rst.pkm.common;

/**
 * @author hujia
 */
public class LittleEndianBuffer {
    private byte[] buffer;
    private int index;

    public LittleEndianBuffer(int capacity) {
        buffer = new byte[capacity];
        index = 0;
    }

    public LittleEndianBuffer(byte[] buf) {
        buffer = buf;
        index = buf.length;
    }

    private void ensureCapacity(int capacity) {
        if (buffer.length - index < capacity) {
            byte[] temp = new byte[buffer.length * 2 + capacity];
            System.arraycopy(buffer, 0, temp, 0, index);
            buffer = temp;
        }
    }

    public void put(byte b) {
        ensureCapacity(1);
        buffer[index++] = b;
    }

    public void putShort(short value) {
        ensureCapacity(2);
        buffer[index++] = (byte) (0xFF & (value));
        buffer[index++] = (byte) (0xFF & (value >> 8));
    }

    public void putInt(int value) {
        ensureCapacity(4);
        buffer[index++] = (byte) (0xFF & (value));
        buffer[index++] = (byte) (0xFF & (value >> 8));
        buffer[index++] = (byte) (0xFF & (value >> 16));
        buffer[index++] = (byte) (0xFF & (value >> 24));
    }


    public void putLong(long value) {
        ensureCapacity(8);
        buffer[index++] = (byte) (0xFFL & (value));
        buffer[index++] = (byte) (0xFFL & (value >> 8));
        buffer[index++] = (byte) (0xFFL & (value >> 16));
        buffer[index++] = (byte) (0xFFL & (value >> 24));
        buffer[index++] = (byte) (0xFFL & (value >> 32));
        buffer[index++] = (byte) (0xFFL & (value >> 40));
        buffer[index++] = (byte) (0xFFL & (value >> 48));
        buffer[index++] = (byte) (0xFFL & (value >> 56));
    }


    public void putBytes(byte[] value) {
        ensureCapacity(value.length);
        System.arraycopy(value, 0, buffer, index, value.length);
        index += value.length;
    }

    public void putBytes(byte[] value, int offset, int length) {
        ensureCapacity(length);
        System.arraycopy(value, offset, buffer, index, length);
        index += length;
    }


    public byte[] toBytes() {
        byte[] bytes = new byte[index];
        System.arraycopy(buffer, 0, bytes, 0, index);
        return bytes;
    }

    public int length() {
        return index;
    }
}
