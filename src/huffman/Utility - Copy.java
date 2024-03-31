package huffman;

/**
 * This class contains three methods that are
 * used in several other classes more than once
 */
public abstract class Utility {

    // Convert byte to binary sting of 8 bits
    public static String byteToString(final byte b) {
        byte[] masks = {-128, 64, 32, 16, 8, 4, 2, 1};
        StringBuilder builder = new StringBuilder();
        for (byte m : masks) {
            if ((b & m) == m) {
                builder.append('1');
            } else {
                builder.append('0');
            }
        }
        return builder.toString();
    }

    // Convert binary string of 8 bits to byte value
    public static byte stringToByte(final String s) {
        return (byte) (int) Integer.valueOf(s, 2);
    }

    // Convert file size to array of 4 bytes
    public static byte[] getFileLengthAsBytes(final String binaryString) {
        byte[] bytes = new byte[4]; // number of digits
        bytes[0] = stringToByte(binaryString.substring(0, 8));
        bytes[1] = stringToByte(binaryString.substring(8, 16));
        bytes[2] = stringToByte(binaryString.substring(16, 24));
        bytes[3] = stringToByte(binaryString.substring(24, 32));
        return bytes;
    }

    // Convert file size from 4 byte to string to print it in the for the header TextArea
    public static String getFileLengthAsString(final byte[] length) {
        StringBuilder len = new StringBuilder();
        for (byte b : length) {
            len.append((char) b);
        }
        return len.toString();
    }
}
