package huffman;

/**
 * This class contain 4 attributes:
 *      - the byte.
 *      - frequency of this byte.
 *      - Huffman code of this byte.
 *      - Huffman code length of this byte.
 * This class used when compress and decompress file process to help us
 *  to encode and decode.
 */
public class StatisticsTable {

    private byte theByte;
    private int frequency;
    private String huffmanCode;
    private byte huffmanCodeLength;


    public StatisticsTable(final byte theByte, final int frequency, final String variableLength, final byte huffmanCodeLength) {
        this.theByte = theByte;
        this.frequency = frequency;
        this.huffmanCode = variableLength;
        this.huffmanCodeLength = huffmanCodeLength;
    }

    public byte getTheByte() {
        return this.theByte;
    }

    public void setTheByte(final byte theByte) {
        this.theByte = theByte;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(final int frequency) {
        this.frequency = frequency;
    }

    public String getHuffmanCode() {
        return this.huffmanCode;
    }

    public byte getHuffmanCodeLength() {
        return this.huffmanCodeLength;
    }

    public void setHuffmanCodeLength(final byte huffmanCodeLength) {
        this.huffmanCodeLength = huffmanCodeLength;
    }

    public void setHuffmanCode(final String huffmanCode) {
        this.huffmanCode = huffmanCode;
    }


    @Override
    public String toString() {
        return "StatisticsTable{" +
                "ASCII=" + theByte +
                ", frequency=" + frequency +
                ", huffmanCode='" + huffmanCode + '\'' +
                ", huffmanLength=" + huffmanCodeLength +
                '}';
    }
}
