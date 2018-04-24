package jwblangley.BST;

import java.util.List;

public class AvlBST<T extends Comparable<T>> implements BinarySearchTree<T> {

  Node<T> root = null;

  @Override
  public boolean add(T obj) {
    return false;
  }

  @Override
  public boolean remove(T obj) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public List<T> inOrderTraversal() {
    return null;
  }


  private boolean isValid() {
    return isValidInner(root);
  }

  private boolean isValidInner(Node<T> currentNode) {
    //TODO @James: implement BST invariant check
    if (Math.abs(height(currentNode.getLeft()) - height(currentNode.getRight())) > 1) {
      return false;
    }
    return isValidInner(currentNode.getLeft()) && isValidInner(currentNode.getRight());
  }

  private int height(Node root) {
    if (root == null) {
      return 0;
    }
    return 1 + Integer.max(height(root.getLeft()), height(root.getRight()));
  }
}
