
package huffman;


public class ReadFileToDecompress {
    private Byte[] fileBytes;
    private String[] huffRepresentation;

    private int originalFileLength;
    private Node root;

    private byte[] huffmanLengths;
    private byte[] huffmanRepresentationBytes;

    private static int remainingIndex = 0;
    private static int remainingSize = 0;

    public int getStartIndexOfHuffmanCode(final byte[] buffer, StringBuilder fileExtension) {
        int beginning = decompress(buffer, fileExtension);
        this.huffRepresentation = new String[this.fileBytes.length];
        buildTwoMainArray();
        this.root = buildTree(this.fileBytes, this.huffRepresentation);
        return beginning;
    }

    public int getOriginalFileLength() {
        return this.originalFileLength;
    }

    public Node getRoot() {
        return this.root;
    }

    private void buildTwoMainArray() {
        String fullHuffCode = this.getHuffmanRepresentationBytesAsSting();
        for (int i = 0; i < this.huffmanLengths.length; ++i) {
            this.huffRepresentation[i] = fullHuffCode.substring(0, this.huffmanLengths[i]);
            fullHuffCode = fullHuffCode.substring(this.huffmanLengths[i]);
        }
    }

    private Node buildTree(Byte[] bytes, String[] huff) {
        Node root;
        if (bytes.length > 0) {
            root = new Node((byte) '\0');
        } else return null;

        for (int i = 0; i < huff.length; ++i) {
            if (bytes[i] == null) break;
            Node current = root;
            if (huff[i] != null) {
                for (int j = 0; j < huff[i].length(); ++j) {
                    if (huff[i].charAt(j) == '0') {
                        if (current.getLeftChild() == null) {
                            current.setLeftChild(new Node((byte) '\0'));
                        }
                        current = current.getLeftChild();
                    } else {
                        if (current.getRightChild() == null) {
                            current.setRightChild(new Node((byte) '\0'));
                        }
                        current = current.getRightChild();
                    }
                }
                current.setTheByte(bytes[i]);
            }

        }

        return root;
    }

    private int decompress(final byte[] buffer, StringBuilder fileExtension) {

        StringBuilder length = new StringBuilder();

        int hlSize;
        int fbSize; // file bytes
        int hrSize;


        boolean getFileExtension = false;
        short i = 0;
        while (true) {
            // get file length from first 4 bytes
            if (i < 4) {
                assert length != null;
                length.append(Utility.byteToString(buffer[i++]));
                continue;
            } else if (i == 4) {
                assert length != null;
                this.originalFileLength = Integer.parseInt(length.toString(), 2);
                length = null;
            }

            // get file extension from the 4th byte even to find the first '\n'
            // because no file extension contains  '\n'
            if (buffer[i] != (byte) 10 && !getFileExtension) {
                fileExtension.append((char) buffer[i++]);
                continue;
            } else if (buffer[i] == (byte) 10) { // end of file extension and skip current byte that represent \n
                getFileExtension = true;
                i++;
                continue;
            }

            // starting to get the bytes and are Huffman representation for them;

            // get # of lengths bytes

            hlSize = buffer[i];
            if (hlSize <= 0) hlSize += 256; // to handle negative value in bytes
            this.huffmanLengths = new byte[hlSize];
            break;
        }

        for (int hlIndex = 0; hlIndex < hlSize; hlIndex++) {
            this.huffmanLengths[hlIndex] = buffer[++i];
        }

        // get file bytes
        fbSize = buffer[++i];
        if (fbSize <= 0) fbSize += 256;
        this.fileBytes = new Byte[fbSize];

        for (int fbIndex = 0; fbIndex < fbSize; fbIndex++) {
            this.fileBytes[fbIndex] = buffer[++i];
        }

        byte b1 = buffer[++i];
        byte b2 = buffer[++i];
        String sHr = Utility.byteToString(b1) + Utility.byteToString(b2);
        hrSize = Integer.parseInt(sHr, 2);
        int remaining = 1024 - i;
        this.huffmanRepresentationBytes = new byte[hrSize];
        for (int hrIndex = 0; hrIndex < hrSize; hrIndex++) {
            this.huffmanRepresentationBytes[hrIndex] = buffer[++i];
        }
        // rem= 516
        // hr = 500,,, 520
        // i = 1012
        if (hrSize <= remaining) {
            return i + 1;
        } else {
            remainingSize = hrSize - remaining;
            remainingIndex = i;
            return -1;
        }
    }

    public short addToOldArray(final byte[] buffer) {
        for (int i = remainingIndex; i < remainingSize; ++i) {
            this.huffmanRepresentationBytes[remainingIndex++] = buffer[i];
        }
        return (short) (remainingIndex +1);
    }

    private String getHuffmanRepresentationBytesAsSting() {
        StringBuilder s = new StringBuilder();
        for (byte b : huffmanRepresentationBytes) {
            s.append(Utility.byteToString(b));
        }
        return s.toString();
    }


}
