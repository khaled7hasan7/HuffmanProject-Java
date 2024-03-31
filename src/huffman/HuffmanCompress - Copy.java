
package huffman;

import java.io.*;
import java.util.PriorityQueue;


 
public class HuffmanCompress {
    private final int ALPHABET_SIZE = 256;
    private StatisticsTable[] huffmanTable;
    private Node root;
    private String fullHeaderAsString = "";
    private short numberOfNodes = 0;
    private static final int NoOfBytes = 1024;


    public void compress(final File sourceFile) {
        root = buildHuffmanTree(buildFrequenciesOfTheBytes(sourceFile));
        assert root != null;
        huffmanTable = new StatisticsTable[ALPHABET_SIZE];
        getHuffmanCode(root, "");
        printToFile(sourceFile);
    }


    public StatisticsTable[] getHuffmanTable() {
        return this.huffmanTable;
    }

    public String getFullHeaderAsString() {
        return this.fullHeaderAsString;
    }

    // Read the file every 1024 bytes is read separately, and get the frequencies for them
    private int[] buildFrequenciesOfTheBytes(final File sourceFile) {
        int[] frequencies = new int[ALPHABET_SIZE];
        try {
            FileInputStream reader = new FileInputStream(sourceFile);

            byte[] buffer = new byte[NoOfBytes]; // number of bytes can be read

            // remaining is the number of bytes to read to fill the buffer
            short remaining = (short) buffer.length;
            int read;
            while (true) {
                read = reader.read(buffer, buffer.length - remaining, remaining);
                // read methode return the total number of bytes read into the buffer, or -1 if there is no more
                // data because the end of the file has been reached.
                if (read >= 0) { // some bytes were read
                    remaining -= read;
                    if (remaining == 0) { // the buffer is full
                        for (byte b : buffer) {
                            frequencies[b + 128]++;
                        }
                        remaining = (short) buffer.length;
                    }
                } else {
                    // the end of the sourceFile was reached. If some bytes are in the buffer
                    for (int i = 0; i < buffer.length - remaining; i++) {
                        frequencies[buffer[i] + 128]++;
                    }
                    break;
                }
            }
            reader.close();
            return frequencies;
        } catch (IOException e) {
            Message.displayMessage("Warning", e.getMessage());
        }
        return null;
    }


    private Node buildHuffmanTree(final int[] frequencies) {

        if (frequencies != null) {

            PriorityQueue<Node> priorityQueue = new PriorityQueue<>();
            for (int i = 0; i < ALPHABET_SIZE; i++) {
                if (frequencies[i] > 0) {
                    priorityQueue.add(new Node((byte) (i - 128), frequencies[i], null, null));
                    numberOfNodes++;
                }
            }
            if (priorityQueue.size() == 1) { // if the file has only one byte
                Node left = priorityQueue.poll();
                return new Node((byte) '\0', left.getFrequency(), left, null);
            }
            // O(nlogn)
            while (priorityQueue.size() > 1) { // > 1, to for the ability to poll twice from queue
                Node left = priorityQueue.poll();
                Node right = priorityQueue.poll();
                assert right != null;
                Node parent = new Node((byte) '\0', left.getFrequency() + right.getFrequency(), left, right);
                priorityQueue.add(parent);

            }

            return priorityQueue.peek();
        }
        return null;
    }

    // build huffman code for each byte recursive
    private void getHuffmanCode(Node node, String code) {
        if (node.isLeaf()) {
            huffmanTable[node.getTheByte() + 128] = new StatisticsTable(node.getTheByte(), node.getFrequency(), code, (byte) code.length());
        } else {
            getHuffmanCode(node.getLeftChild(), code + "0");
            getHuffmanCode(node.getRightChild(), code + "1");
        }
    }


    // Read the file again, and print the header and staring to encode process
    private void printToFile(final File sourceFile) {

        // get the path of the file and his extension
        byte indexOfDot = (byte) sourceFile.getAbsolutePath().lastIndexOf('.');
        String newFilePath = sourceFile.getAbsolutePath().substring(0, indexOfDot + 1) + "huf";
        String fileExtension = sourceFile.getAbsolutePath().substring(indexOfDot + 1);
        byte[] fileExtensionBytes = fileExtension.getBytes();
        int lengthOfFile = (int) sourceFile.length();

        try {

            FileOutputStream writer = new FileOutputStream(newFilePath);

            // ***************************** Start of the header **********************

            // print the file length in bytes ( 4 bytes)
            String l = Integer.toBinaryString(lengthOfFile);
            l = "0".repeat(32 - l.length()) + l;
            byte[] lengthInBytes = Utility.getFileLengthAsBytes(l);
            writer.write(lengthInBytes, 0, 4);

            // print the file extension
            writer.write(fileExtensionBytes, 0, fileExtensionBytes.length);
            writer.write('\n');

            // get the 2D array (huffmanBuffer code length, bytes, huffmanBuffer code)
            byte[][] header = this.getHeader(huffmanTable, numberOfNodes);

            // set the header in string to display it in the TextArea in the window
            this.fullHeaderAsString = Utility.getFileLengthAsString(lengthInBytes); // file length
            this.fullHeaderAsString += (fileExtension + "\n");// file extension
            this.fullHeaderAsString += getHeaderAsString(header);

            // print length of the first row(huffmanBuffer code length), then the row itself to the huf file
            writer.write(header[0].length);
            writer.write(header[0], 0, header[0].length);

            // print length of the second row(bytes), then the row itself to the huf file
            writer.write(header[1].length);
            writer.write(header[1], 0, header[1].length);

            // print length of the third row(huffmanBuffer code), then the row itself to the huf file
            int twoByteLength = header[2].length;
            String strTwoByte = Integer.toBinaryString(twoByteLength);
            strTwoByte = "0".repeat(16 - strTwoByte.length()) + strTwoByte;
            byte[] tempByte = new byte[2];
            tempByte[0] = Utility.stringToByte(strTwoByte.substring(0, 8));
            tempByte[1] = Utility.stringToByte(strTwoByte.substring(8));

            writer.write(tempByte, 0, 2);
            writer.write(header[2], 0, twoByteLength);

            // ********************* end of the header ****************************


            // ************** encode the file and print to the output file ************

            InputStream fis = new FileInputStream(sourceFile);

            byte[] buffer = new byte[NoOfBytes]; // number of bytes can be read

            // remaining is the number of bytes to read to fill the buffer
            short remaining = (short) buffer.length;

            byte[] huffmanBuffer = new byte[NoOfBytes];
            short index = 0, read;
            StringBuilder remainingBits = new StringBuilder();
            String huffmanBits;

            while (true) {
                read = (short) fis.read(buffer, buffer.length - remaining, remaining);
                if (read >= 0) { // some bytes were read
                    remaining -= read;
                    if (remaining == 0) { // the buffer is full
                        for (byte b : buffer) {
                            huffmanBits = remainingBits + huffmanTable[b + 128].getHuffmanCode();

                            if (huffmanBits.length() >= 8) {
                                // to store the bits above than index 7
                                remainingBits = new StringBuilder(huffmanBits.substring(8));
                                huffmanBuffer[index++] = Utility.stringToByte(huffmanBits.substring(0, 8));
                                if (index == NoOfBytes) {
                                    writer.write(huffmanBuffer, 0, NoOfBytes);
                                    index = 0;
                                }
                            } else {
                                remainingBits = new StringBuilder(huffmanBits);
                            }

                        }
                        remaining = (short) buffer.length;
                    }
                } else {

                    // the end of the file was reached. If some bytes are in the buffer

                    for (int i = 0; i < buffer.length - remaining; i++) { // for the remaining bytes
                        huffmanBits = remainingBits + huffmanTable[buffer[i] + 128].getHuffmanCode();

                        if (huffmanBits.length() >= 8) {
                            remainingBits = new StringBuilder(huffmanBits.substring(8)); // to store bit above than index 7
                            huffmanBuffer[index++] = Utility.stringToByte(huffmanBits.substring(0, 8));
                            if (index == NoOfBytes) { // maybe the huffmanBuffer code for these bytes when grouped reach 1024 byte
                                writer.write(huffmanBuffer, 0, NoOfBytes);
                                index = 0;
                            }
                        } else {
                            remainingBits = new StringBuilder(huffmanBits);
                        }
                    }

                    String temp;
                    int length;
                    while (remainingBits.length() != 0) {
                        length = remainingBits.length();
                        if (length < 8) {
                            temp = remainingBits.substring(length);
                            remainingBits.append("0".repeat(8 - length));
                        } else {
                            temp = remainingBits.substring(8);
                            remainingBits = new StringBuilder(remainingBits.substring(0, 8));
                        }
                        huffmanBuffer[index++] = Utility.stringToByte(remainingBits.toString());
                        if (index == NoOfBytes) {
                            writer.write(huffmanBuffer, 0, NoOfBytes);
                            index = 0;
                        }
                        remainingBits = new StringBuilder(temp);
                    }
                    break;
                }
            }
            if (index > 0) { // huffmanBuffer still contain bytes
                writer.write(huffmanBuffer, 0, index);
            }
            fis.close();
            writer.close();
            Message.displayMessage("Successfully", sourceFile.getName() + " was compress successfully");
        } catch (IOException e) {
            Message.displayMessage("Warning", e.getMessage());
        }

    }

    private byte[][] getHeader(StatisticsTable[] huffmanTable, int numberOfNodes) {

        byte[][] header = new byte[3][];

        // ***********************************************************************************

        // get  huffman code length for each leaf node
        byte[] lengths = new byte[numberOfNodes];
        int index = 0;
        int sumOfBitsLength = 0;
        for (StatisticsTable b : huffmanTable) {
            if (b != null) {
                lengths[index++] = b.getHuffmanCodeLength();
                sumOfBitsLength += b.getHuffmanCodeLength();
            }
        }

        header[0] = lengths; // to store the length of each Huffman code

        // ***********************************************************************************

        // get the header from leaf node
        byte[] bytes = new byte[numberOfNodes];
        for (int iLoop = 0, iIndex = 0; iLoop < huffmanTable.length; iLoop++) {
            if (huffmanTable[iLoop] != null) {
                bytes[iIndex++] = huffmanTable[iLoop].getTheByte();
            }
        }
        header[1] = bytes; // store all byte of leaf nodes

        // ***********************************************************************************

        // to calculate how many byte exactly I need to store Huffman code for each leaf node
        byte[] huffmanCodeForLeaf;
        if (sumOfBitsLength % 8 == 0)
            huffmanCodeForLeaf = new byte[sumOfBitsLength / 8];
        else huffmanCodeForLeaf = new byte[(sumOfBitsLength / 8) + 1];

        StringBuilder strBits = new StringBuilder();
        byte b;
        int huffIndex = 0;
        for (StatisticsTable statisticsTable : huffmanTable) {
            if (statisticsTable != null) {
                strBits.append(statisticsTable.getHuffmanCode());
                if (strBits.length() >= 8) {
                    b = Utility.stringToByte(strBits.substring(0, 8));
                    huffmanCodeForLeaf[huffIndex++] = b;
                    strBits = new StringBuilder(strBits.substring(8));
                }

            }
        }
        // the strBits contain bits
        while (strBits.length() > 0) {
            if (strBits.length() >= 8) {
                b = Utility.stringToByte(strBits.substring(0, 8));
                strBits = new StringBuilder(strBits.substring(8));
                huffmanCodeForLeaf[huffIndex++] = b;
            } else {
                b = Utility.stringToByte(strBits + "0".repeat(8 - strBits.length()));
                huffmanCodeForLeaf[huffIndex] = b;
                strBits = new StringBuilder();
            }
        }

        header[2] = huffmanCodeForLeaf; // To store Huffman representation for all leaf nodes

        // ***********************************************************************************

        return header;
    }

    public void returnDefault() {
        huffmanTable = new StatisticsTable[ALPHABET_SIZE];
        root = null;
        fullHeaderAsString = "";
        numberOfNodes = 0;
        System.gc();
    }

    // Get the 2D array as sting to display it in the TextArea
    private String getHeaderAsString(byte[][] header) {

        StringBuilder head = new StringBuilder();

        for (byte i = 0; i < 3; i++) {
            if (i == 2) {
                int twoByteLength = header[i].length;
                String strTwoByte = Integer.toBinaryString(twoByteLength);
                strTwoByte = "0".repeat(16 - strTwoByte.length()) + strTwoByte;
                byte[] tempByte = new byte[2];
                tempByte[0] = Utility.stringToByte(strTwoByte.substring(0, 8));
                tempByte[1] = Utility.stringToByte(strTwoByte.substring(8));
                head.append((char) tempByte[0]);
                head.append((char) tempByte[1]);
            } else {
                head.append((char) header[i].length); // size of each
            }
            // first row: represent lengths for huffman code
            // second row : represent all bytes
            // third row: represent all huffman code
            for (byte aByte : header[i]) {
                head.append((char) aByte); // huffman code for each byte
            }
        }
        return head.toString();
    }

}
