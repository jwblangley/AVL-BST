package jwblangley.BST;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AvlBST<T extends Comparable<T>> implements BinarySearchTree<T> {

  Node<T> root = null;
  private AtomicInteger size = new AtomicInteger(0);

  @Override
  public synchronized boolean add(T obj) {
    Node<T> newNode = new Node<>(obj);
    if (root == null) {
      root = newNode;
      size.incrementAndGet();
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
    size.incrementAndGet();

    rebalance();
    return true;
  }

  @Override
  public synchronized boolean remove(T obj) {
    Node<T> curr = root;
    Node<T> parent = root;
    while (curr != null) {
      if (obj.compareTo(curr.getItem()) == 0) {
        if (curr == root) {
          if (root.getLeft() == null && root.getRight() == null) {
            //Leaf node
            root = null;
            size.decrementAndGet();
            rebalance();
            return true;
          } else if (root.getRight() == null) {
            //only left child
            root = root.getLeft();
            size.decrementAndGet();
            rebalance();
            return true;
          } else if (root.getLeft() == null) {
            //only right child
            root = root.getRight();
            size.decrementAndGet();
            rebalance();
            return true;
          } else {
            //2 children
            Node<T> newRoot = findMaxNodeAndDisconnect(root.getLeft());
            newRoot.setRight(root.getRight());
            if (newRoot != root.getLeft()) {
              newRoot.setLeft(root.getLeft());
            }
            root = newRoot;

            size.decrementAndGet();
            rebalance();
            return true;
          }
        }
        if (curr.getLeft() == null && curr.getRight() == null) {
          //Leaf node
          if (curr.getItem().compareTo(parent.getItem()) < 0) {
            parent.setLeft(null);
          } else {
            parent.setRight(null);
          }
          size.decrementAndGet();
          rebalance();
          return true;
        } else if (curr.getRight() == null) {
          //only left child
          if (curr.getItem().compareTo(parent.getItem()) < 0) {
            parent.setLeft(curr.getLeft());
          } else {
            parent.setRight(curr.getLeft());
          }
          size.decrementAndGet();
          rebalance();
          return true;
        } else if (curr.getLeft() == null) {
          //only right child
          if (curr.getItem().compareTo(parent.getItem()) < 0) {
            parent.setLeft(curr.getRight());
          } else {
            parent.setRight(curr.getRight());
          }
          size.decrementAndGet();
          rebalance();
          return true;
        } else {
          //2 children
          boolean leftOfParent = curr.getItem().compareTo(parent.getItem()) < 0;
          Node<T> replacementNode = findMaxNodeAndDisconnect(curr.getLeft());
          replacementNode.setRight(curr.getRight());
          if (replacementNode != curr.getLeft()) {
            replacementNode.setLeft(curr.getLeft());
          }
          if (leftOfParent) {
            parent.setLeft(replacementNode);
          } else {
            parent.setRight(replacementNode);
          }

          size.decrementAndGet();
          rebalance();
          return true;
        }
      } else {
        parent = curr;
        curr = (obj.compareTo(curr.getItem()) < 0) ? curr.getLeft() : curr.getRight();
      }
    }
    //Not in tree
    return false;
  }


  private Node<T> findMaxNodeAndDisconnect(final Node<T> node) {
    Node<T> parent = node;
    Node<T> current = node;

    while (current.getRight() != null) {
      parent = current;
      current = current.getRight();
    }

    //disconnect
    if (current != node) {
      parent.setRight(current.getLeft());
    }
    return current;
  }

  private void rebalance() {
    //Find smallest unbalanced subtree
    if (avlInvariant(root)) {
      //No rebalancing needed
      assert isValid();
      return;
    }
    Node currentNode = root;
    Node parent = root;
    Node grandparent = root;
    while (!avlInvariant(currentNode)) {
      grandparent = parent;
      parent = currentNode;
      currentNode = avlInvariant(currentNode.getLeft())
          ? currentNode.getRight() : currentNode.getLeft();
    }
    Node subtree = parent;
    Node subtreeParent = grandparent;

    boolean newRoot = (subtree == root);
    Node newTop = subtree;
    boolean isLeftChild = subtreeParent.getLeft() == subtree;

    if (height(subtree.getLeft()) > height(subtree.getRight())) {
      if (height(subtree.getLeft().getLeft()) > height(subtree.getLeft().getRight())) {
        newTop = rightRotate(subtree);
      } else {
        newTop = leftRightRotate(subtree);
      }
    } else {
      if (height(subtree.getRight().getLeft()) > height(subtree.getRight().getRight())) {
        newTop = rightLeftRotate(subtree);
      } else {
        newTop = leftRotate(subtree);
      }
    }

    if (newRoot) {
      root = newTop;
    } else {
      if (isLeftChild) {
        subtreeParent.setLeft(newTop);
      } else {
        subtreeParent.setRight(newTop);
      }
    }

    if(!avlInvariant(root)){
      rebalance();
    }
  }

  private Node<T> rightRotate(Node<T> subtree) {
    Node<T> tempTree = subtree.getLeft();
    subtree.setLeft(tempTree.getRight());
    tempTree.setRight(subtree);
    return tempTree;
  }

  private Node<T> leftRotate(Node<T> subtree) {
    Node<T> tempTree = subtree.getRight();
    subtree.setRight(tempTree.getLeft());
    tempTree.setLeft(subtree);
    return tempTree;
  }

  private Node<T> leftRightRotate(Node<T> node) {
    node.setLeft(leftRotate(node.getLeft()));
    return rightRotate(node);
  }

  private Node<T> rightLeftRotate(Node<T> node) {
    node.setRight(rightRotate(node.getRight()));
    return leftRotate(node);
  }

  @Override
  public synchronized int size() {
    assert isValid();
    return size.get();
  }

  @Override
  public synchronized List<T> inOrderTraversal() {
    List<T> iot = new ArrayList<>();
    dfs(iot, root);
    return iot;
  }

  private void dfs(List<T> list, Node<T> currentNode) {
    if (currentNode == null) {
      return;
    }

    dfs(list, currentNode.getLeft());
    list.add(currentNode.getItem());
    dfs(list, currentNode.getRight());
  }

  private boolean isValid() {
    if (!bstInvariant(root)) {
      System.out.println("BST invariant failed");
      return false;
    }
    if (!avlInvariant(root)) {
      System.out.println("AVL invariant failed");
      return false;
    }
    return true;
  }

  private boolean bstInvariant(Node<T> currentNode) {
    if (currentNode == null) {
      return true;
    }
    if (currentNode.getLeft() != null) {
      if (currentNode.getItem().compareTo(currentNode.getLeft().getItem()) < 0) {
        return false;
      }
    }
    if (currentNode.getRight() != null) {
      if (currentNode.getItem().compareTo(currentNode.getRight().getItem()) > 0) {
        return false;
      }
    }
    return bstInvariant(currentNode.getLeft()) && bstInvariant(currentNode.getRight());

  }

  private boolean avlInvariant(Node node) {
    if (node == null) {
      return true;
    }
    if (Math.abs(height(node.getLeft()) - height(node.getRight())) > 1) {
      return false;
    }
    return avlInvariant(node.getLeft()) && avlInvariant(node.getRight());
  }


  private int height(Node root) {
    if (root == null) {
      return 0;
    }
    return 1 + Integer.max(height(root.getLeft()), height(root.getRight()));
  }

  @Override
  public String toString() {
    return (root == null) ? "null" : root.toString();
  }
}
