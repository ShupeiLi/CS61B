package lab9;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * Implementation of interface Map61B with BST as core data structure.
 *
 * @author Your name here
 */
public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {

    private class Node {
        /* (K, V) pair stored in this Node. */
        private K key;
        private V value;

        /* Children of this Node. */
        private Node left;
        private Node right;

        // Add attributes.
        private Node parent;
        private boolean leftChild;

        private Node(K k, V v) {
            key = k;
            value = v;
            parent = null;
            leftChild = false;
        }

        private Node(K k, V v, Node parent, boolean leftChild) {
            key = k;
            value = v;
            this.parent = parent;
            this.leftChild = leftChild;
        }
    }

    private Node root;  /* Root node of the tree. */
    private int size; /* The number of key-value pairs in the tree */

    /* Creates an empty BSTMap. */
    public BSTMap() {
        this.clear();
    }

    /* Removes all the mappings from this map. */
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    /** Returns the value mapped to by KEY in the subtree rooted in P.
     *  or null if this map contains no mapping for the key.
     */
    private V getHelper(K key, Node p) {
        if (p == null) {
            return null;
        }
        if (p.key.equals(key)) {
            return p.value;
        } else if (p.key.compareTo(key) > 0) {
            return getHelper(key, p.left);
        } else {
            return getHelper(key, p.right);
        }
    }

    /** Returns the value to which the specified key is mapped, or null if this
     *  map contains no mapping for the key.
     */
    @Override
    public V get(K key) {
        return getHelper(key, root);
    }

    /** Returns a BSTMap rooted in p with (KEY, VALUE) added as a key-value mapping.
      * Or if p is null, it returns a one node BSTMap containing (KEY, VALUE).
     */
    private Node putHelper(K key, V value, Node p, Node parent, boolean leftChild) {
        if (p == null) {
            if (parent != null) {
                return new Node(key, value, parent, leftChild);
            } else {
                return new Node(key, value);
            }
        }
        if (p.key.compareTo(key) > 0) {
            p.left = putHelper(key, value, p.left, p, true);
        } else if (p.key.compareTo(key) < 0) {
            p.right = putHelper(key, value, p.right, p, false);
        }
        return p;
    }

    /** Inserts the key KEY
     *  If it is already present, updates value to be VALUE.
     */
    @Override
    public void put(K key, V value) {
        root = putHelper(key, value, root, null, false);
        size += 1;
    }

    /* Returns the number of key-value mappings in this map. */
    @Override
    public int size() {
        return size;
    }

    //////////////// EVERYTHING BELOW THIS LINE IS OPTIONAL ////////////////
    private Set<K> keySetHelper(Set<K> set, Node p) {
        if (p != null) {
            set.add(p.key);
            set = keySetHelper(set, p.left);
            set = keySetHelper(set, p.right);
        }
        return set;
    }

    /* Returns a Set view of the keys contained in this map. */
    @Override
    public Set<K> keySet() {
        Set<K> set = new TreeSet<>();
        return keySetHelper(set, root);
    }

    /** Removes KEY from the tree if present
     *  returns VALUE removed,
     *  null on failed removal.
     */
    @Override
    public V remove(K key) {
        return removeHelper(key, root, false, null);
    }

    private V removeHelper(K key, Node p, boolean valueSpecified, V valueCheck) {
        V value;
        boolean checker = true;
        if (valueSpecified) {
            checker = p.value.equals(valueCheck);
        }
        if (p == null) {
            return null;
        }
        if (p.key.equals(key) && checker) {
            value = p.value;
            size--;
            if (p.left == null) {
                transplant(p, p.right);
            } else if (p.right == null) {
                transplant(p, p.left);
            } else {
                Node alter = findMax(p.left);
                K tempKey = alter.key;
                V tempValue = alter.value;
                removeHelper(key, alter, valueSpecified, valueCheck);
                p.key = tempKey;
                p.value = tempValue;
            }
        } else if (p.key.compareTo(key) > 0) {
            value = removeHelper(key, p.left, valueSpecified, valueCheck);
        } else {
            value = removeHelper(key, p.right, valueSpecified, valueCheck);
        }
            return value;
    }

    private void transplant(Node p, Node target) {
        if (p.leftChild && p.parent != null) {
            p.parent.left = target;
        } else if (p.parent != null) {
            p.parent.right = target;
        } else {
            root = target;
        }
    }

    private Node findMax(Node p) {
        if (p == null) {
            return null;
        }
        if (p.right != null) {
            return findMax(p.right);
        } else {
            return p;
        }
    }

    /** Removes the key-value entry for the specified key only if it is
     *  currently mapped to the specified value.  Returns the VALUE removed,
     *  null on failed removal.
     **/
    @Override
    public V remove(K key, V value) {
        return removeHelper(key, root, true, value);
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
