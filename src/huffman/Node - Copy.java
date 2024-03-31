

package huffman;

/**
 * This class contain 4 attributes:
 *      - The byte that inside the tree node
 *      - The frequency of this byte
 *      - The left child of this node
 *      - The right child of this node
 * This class used to build Huffman tree
 */
public class Node implements Comparable<Node> {

    private byte theByte;
    private int frequency;
    private Node leftChild;
    private Node rightChild;

    public Node(final byte theByte) {
        this.theByte = theByte;
    }

    public Node(final byte theByte, final int frequency, final Node left, final Node right) {
        this.theByte = theByte;
        this.frequency = frequency;
        this.leftChild = left;
        this.rightChild = right;
    }

    public boolean isLeaf() {
        return this.leftChild == null && this.rightChild == null;
    }

    public byte getTheByte() {
        return this.theByte;
    }

    public void setTheByte(byte theByte) {
        this.theByte = theByte;
    }

    public int getFrequency() {
        return this.frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public Node getLeftChild() {
        return this.leftChild;
    }

    public void setLeftChild(Node leftChild) {
        this.leftChild = leftChild;
    }

    public Node getRightChild() {
        return this.rightChild;
    }

    public void setRightChild(Node rightChild) {
        this.rightChild = rightChild;
    }

    @Override
    public int compareTo(Node that) {
        int frequencyComparison = Integer.compare(this.frequency, that.frequency);
        if (frequencyComparison != 0)
            return frequencyComparison;
        return Byte.compare(this.theByte, that.theByte);
    }

    @Override
    public String toString() {
        return "Node{" +
                "bytes=" + theByte +
                ", frequency=" + frequency +
                ", leftChild=" + leftChild +
                ", rightChild=" + rightChild +
                '}';
    }
}
