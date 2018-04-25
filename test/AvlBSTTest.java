import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import jwblangley.BST.AvlBST;
import jwblangley.BST.BinarySearchTree;
import jwblangley.BST.Node;
import org.junit.Test;

public class AvlBSTTest {

  //N.B invariant assertions are within the add and remove methods


  private final int NUM_THREADS = 8;

  @Test
  public void toStringTest() {
    Node node1 = new Node("Hello");
    assertEquals("(Node:Hello, L:null, R:null)", node1.toString());
    Node node2 = new Node("World");
    node1.setRight(node2);
    assertEquals("(Node:Hello, L:null, R:(Node:World, L:null, R:null))", node1.toString());
    Node node3 = new Node("Bye");
    node2.setLeft(node3);
    assertEquals("(Node:Hello, L:null, R:(Node:World, L:(Node:Bye, L:null, R:null), R:null))",
        node1.toString());
  }

  @Test
  public void serialAddTest() {
    BinarySearchTree<Integer> bst = new AvlBST<>();
    assertEquals(bst.size(), 0);
    assert bst.add(5);
    assertEquals("(Node:5, L:null, R:null)", bst.toString());
    assertEquals(bst.size(), 1);
    assert bst.add(2);
    assertEquals("(Node:5, L:(Node:2, L:null, R:null), R:null)", bst.toString());
    assertEquals(bst.size(), 2);
    assert bst.add(7);
    assertEquals("(Node:5, L:(Node:2, L:null, R:null), R:(Node:7, L:null, R:null))",
        bst.toString());
    assertEquals(bst.size(), 3);
    assert bst.add(4);
    assertEquals(
        "(Node:5, L:(Node:2, L:null, R:(Node:4, L:null, R:null)), R:(Node:7, L:null, R:null))",
        bst.toString());
    assertEquals(bst.size(), 4);
    assertFalse(bst.add(4));
    assertEquals(
        "(Node:5, L:(Node:2, L:null, R:(Node:4, L:null, R:null)), R:(Node:7, L:null, R:null))",
        bst.toString());
    assertEquals(bst.size(), 4);

    assert bst.add(3);
    assertEquals(
        "(Node:5, L:(Node:3, L:(Node:2, L:null, R:null), R:(Node:4, L:null, R:null)), R:(Node:7, L:null, R:null))",
        bst.toString());
    assertEquals(bst.size(), 5);

    assert isSorted(bst);
    assertConsistentSize(bst);
  }

  @Test
  public void serialRemoveTest() {
    BinarySearchTree<Integer> bst = new AvlBST<>();
    assertFalse(bst.remove(2));

    bst.add(2);
    assert bst.remove(2);
    assertEquals(bst.size(), 0);

    bst.add(3);
    bst.add(1);
    bst.add(2);
    assert bst.remove(2);
    assertEquals(bst.size(), 2);
    assert isSorted(bst);
    assertConsistentSize(bst);

    bst = new AvlBST<>();
    bst.add(3);
    bst.add(1);
    bst.add(2);
    assert bst.remove(3);
    assertEquals(bst.size(), 2);
    assert isSorted(bst);
    assertConsistentSize(bst);

    bst = new AvlBST<>();
    bst.add(3);
    bst.add(1);
    bst.add(2);
    assert bst.remove(1);
    assertEquals(bst.size(), 2);
    assert isSorted(bst);
    assertConsistentSize(bst);

    bst = new AvlBST<>();
    bst.add(4);
    bst.add(1);
    bst.add(2);
    bst.add(6);
    bst.add(3);
    bst.add(5);
    assert bst.remove(4);
    assertEquals(bst.size(), 5);
    assertEquals(
        "(Node:3, L:(Node:2, L:(Node:1, L:null, R:null), R:null), R:(Node:6, L:(Node:5, L:null, R:null), R:null))",
        bst.toString());
    assert isSorted(bst);
    assertConsistentSize(bst);
  }

  @Test
  public void durabilityTest() {
    BinarySearchTree<Integer> bst = new AvlBST<>();
    Random rand = new Random();

    for (int i = 0; i < 1000; i++) {
      if (rand.nextBoolean()) {
        bst.add(rand.nextInt(1000));
      } else {
        bst.remove(rand.nextInt(1000));
      }
    }
  }

  @Test(timeout = 10000)
  public void threadSafeTest() {
    BinarySearchTree<Integer> bst = new AvlBST<>();
    Random rand = new Random();

    Thread[] threads = new Thread[NUM_THREADS];
    Arrays.setAll(threads, index ->
        new Thread(() -> {
          for (int i = 0; i < 100; i++) {
            if (rand.nextBoolean()) {
              bst.add(rand.nextInt(100));
            } else {
              bst.remove(rand.nextInt(100));
            }
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }
        }));

    Arrays.stream(threads).forEach(Thread::start);
    Arrays.stream(threads).forEach(thread -> {
      try {
        thread.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    });

    assert isSorted(bst);
    assertConsistentSize(bst);
  }

  private <T extends Comparable<T>> boolean isSorted(BinarySearchTree<T> bst) {
    List<T> sortResult = bst.inOrderTraversal();

    if (sortResult.size() == 0) {
      return true;
    }

    Iterator<T> iter = sortResult.iterator();
    T t1 = iter.next();
    while (iter.hasNext()) {
      T t2 = iter.next();
      if (t1.compareTo(t2) > 0) {
        return false;
      }
      t1 = t2;
    }
    return true;
  }

  private void assertConsistentSize(BinarySearchTree bst) {
//    System.out.println(bst.inOrderTraversal());
    assertEquals(bst.size(), bst.inOrderTraversal().size());
  }


}