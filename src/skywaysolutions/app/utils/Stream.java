package skywaysolutions.app.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stream utilities.
 *
 * @author Alfred Manville
 */
public final class Stream {
    /**
     * Reads an integer from a stream.
     *
     * @param is The input stream to read from.
     * @return The integer.
     * @throws IOException An I/O Error occurs.
     */
    public static int readInteger(InputStream is) throws IOException {
        byte[] intBuffer = new byte[4];
        if(is.read(intBuffer) != 4) throw new IOException("Could not read integer");
        int firstByte = intBuffer[0] & 0xff;
        return (((firstByte > 127) ? firstByte - 128 : firstByte) * 16777216 + (intBuffer[1] & 0xff) * 65536 + (intBuffer[2] & 0xff) * 256 + (intBuffer[3] & 0xff)) * ((firstByte > 127) ? -1 : 1);
    }

    /**
     * Writes an integer to a stream.
     *
     * @param os The output stream to write to.
     * @param toWrite The integer to write.
     * @throws IOException An I/O Error occurs.
     */
    public static void writeInteger(OutputStream os, int toWrite) throws IOException {
        byte[] intBuffer = new byte[4];
        boolean neg = toWrite < 0;
        if (neg) toWrite *= -1;
        intBuffer[0] = (byte) ((toWrite / 16777216)+((neg) ? 128 : 0));
        toWrite %= 16777216;
        intBuffer[1] = (byte) (toWrite / 65536);
        toWrite %= 65536;
        intBuffer[2] = (byte) (toWrite / 256);
        toWrite %= 256;
        intBuffer[3] = (byte) (toWrite);
        os.write(intBuffer);
    }

    /**
     * Reads a long from a stream.
     *
     * @param is The input stream to read from.
     * @return The long.
     * @throws IOException An I/O Error occurs.
     */
    public static long readLong(InputStream is) throws IOException {
        byte[] longBuffer = new byte[8];
        if(is.read(longBuffer) != 8) throw new IOException("Could not read long");
        int firstByte = longBuffer[0] & 0xff;
        return ((long) ((firstByte > 127) ? firstByte - 128 : firstByte) * 72057594037927936L + (long) (longBuffer[1] & 0xff) * 281474976710656L + (long) (longBuffer[2] & 0xff) * 1099511627776L + (long) (longBuffer[3] & 0xff) * 4294967296L +
                (long) (longBuffer[4] & 0xff) * 16777216L + (long) (longBuffer[5] & 0xff) * 65536L + (long) (longBuffer[6] & 0xff) * 256L + (long)(longBuffer[7] & 0xff)) * (long) ((firstByte > 127) ? -1 : 1);
    }

    /**
     * Writes a long to a stream.
     *
     * @param os The output stream to write to.
     * @param toWrite The long to write.
     * @throws IOException An I/O Error occurs.
     */
    public static void writeLong(OutputStream os, long toWrite) throws IOException {
        byte[] longBuffer = new byte[8];
        boolean neg = toWrite < 0;
        if (neg) toWrite *= -1;
        longBuffer[0] = (byte) ((toWrite / 72057594037927936L)+(long) ((neg) ? 128 : 0));
        toWrite %= 72057594037927936L;
        longBuffer[1] = (byte) (toWrite / 281474976710656L);
        toWrite %= 281474976710656L;
        longBuffer[2] = (byte) (toWrite / 1099511627776L);
        toWrite %= 1099511627776L;
        longBuffer[3] = (byte) (toWrite / 4294967296L);
        toWrite %= 4294967296L;
        longBuffer[4] = (byte) (toWrite / 16777216L);
        toWrite %= 16777216L;
        longBuffer[5] = (byte) (toWrite / 65536L);
        toWrite %= 65536L;
        longBuffer[6] = (byte) (toWrite / 256L);
        toWrite %= 256L;
        longBuffer[7] = (byte) (toWrite);
        os.write(longBuffer);
    }

    /**
     * Reads a byte set from a stream.
     *
     * @param is The input stream to read from.
     * @return The read byte set.
     * @throws IOException An I/O Error occurs.
     */
    public static byte[] readBytes(InputStream is) throws IOException {
        int length = readInteger(is);
        byte[] byteBuffer = new byte[length];
        if (is.read(byteBuffer) != length) throw new IOException("Could not read byte set");
        return byteBuffer;
    }

    /**
     * Writes a byte set to a stream.
     *
     * @param os The output stream to write to.
     * @param bytes The byte set to write.
     * @throws IOException An I/O Error occurs.
     */
    public static void writeBytes(OutputStream os, byte[] bytes) throws IOException {
        writeInteger(os, bytes.length);
        os.write(bytes);
    }
}
