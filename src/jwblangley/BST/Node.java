package jwblangley.BST;

public class Node<T extends Comparable<T>> {

  private final T item;
  private Node<T> leftNode, rightNode;

  @Override
  public String toString() {
    return "(Node:" + item + ", L:" + leftNode + ", R:" + rightNode + ")";
  }

  public Node(T item) {
    this.item = item;
  }

  public T getItem() {
    return item;
  }

  public Node<T> getRight() {
    return rightNode;
  }

  public void setRight(Node<T> rightNode) {
    this.rightNode = rightNode;
  }

  public Node<T> getLeft() {
    return leftNode;
  }

  public void setLeft(Node<T> leftNode) {
    this.leftNode = leftNode;
  }

}