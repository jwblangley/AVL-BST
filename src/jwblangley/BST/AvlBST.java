package jwblangley.BST;

import java.util.List;

public class AvlBST<T extends Comparable<T>> implements BinarySearchTree<T> {

  Node<T> root = null;

  @Override
  public synchronized boolean add(T obj) {
    Node<T> newNode = new Node<>(obj);
    if (root == null) {
      root = newNode;
      return true;
    }
    Node<T> curr = root;
    Node<T> parent = root;
    while (curr != null) {
      if (obj.compareTo(curr.getItem()) == 0) {
        //item already in tree
        return false;
      } else {
        parent = curr;
        curr = (obj.compareTo(curr.getItem()) < 0) ? curr.getLeft() : curr.getRight();
      }
    }

    if ((obj.compareTo(parent.getItem()) < 0)) {
      parent.setLeft(newNode);
    } else {
      parent.setRight(newNode);
    }

    rebalance();
    return false;
  }

  @Override
  public synchronized boolean remove(T obj) {
    //TODO @James: implement remove
    return false;
  }

  @Override
  public synchronized int size() {
    assert isValid();
    //TODO @James: implement size
    return 0;
  }

  @Override
  public synchronized List<T> inOrderTraversal() {
    assert isValid();
    //TODO @James: implement inOrderTraversal
    return null;
  }

  private void rebalance() {
    //TODO @James: implement rebalance
    assert isValid();
  }


  private boolean isValid() {
    return isValidInner(root);
  }

  private boolean isValidInner(Node<T> currentNode) {
    if (Math.abs(height(currentNode.getLeft()) - height(currentNode.getRight())) > 1) {
      System.out.println("AVL invariant failed");
      return false;
    }
    if (currentNode.getItem().compareTo(currentNode.getLeft().getItem()) > 0
        && currentNode.getItem().compareTo(currentNode.getRight().getItem()) < 0) {
      return isValidInner(currentNode.getLeft()) && isValidInner(currentNode.getRight());
    } else {
      System.out.println("BST invariant failed");
      return false;
    }
  }

  private int height(Node root) {
    if (root == null) {
      return 0;
    }
    return 1 + Integer.max(height(root.getLeft()), height(root.getRight()));
  }
}
