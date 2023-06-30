public class LinkedListDeque<T> {
    private class Node<T> {
        protected T item;
        protected Node<T> prev;
        protected Node<T> next;

        public Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }

        public Node(T item) {
            this.item = item;
            this.prev = this;
            this.next = this;
        }
    }

    private int nItems = 0; // The size of the deque
    private Node<T> sentinel = new Node<>(null); // Sentinel node

    public LinkedListDeque() { }

    public void addFirst(T item) {
        Node<T> newNode = new Node<>(item, sentinel, sentinel.next);
        sentinel.next.prev = newNode;
        sentinel.next = newNode;
        nItems++;
    }

    public void addLast(T item) {
        Node<T> newNode = new Node<>(item, sentinel.prev, sentinel);
        sentinel.prev.next = newNode;
        sentinel.prev = newNode;
        nItems++;
    }

    public boolean isEmpty() {
        return nItems == 0;
    }

    public int size() {
        return nItems;
    }

    public void printDeque() {
        Node<T> node = sentinel.next;
        while (node.item != null) {
            System.out.print(node.item + " ");
            node = node.next;
        }
        System.out.print("\n");
    }

    public T removeFirst() {
        Node<T> temp = sentinel.next;
        sentinel.next = temp.next;
        temp.next.prev = sentinel;
        nItems--;
        return temp.item;
    }

    public T removeLast() {
        Node<T> temp = sentinel.prev;
        sentinel.prev = temp.prev;
        temp.prev.next = sentinel;
        nItems--;
        return temp.item;
    }

    public T get(int index) {
        if (index >= nItems) {
            return null;
        } else {
            Node<T> node = sentinel.next;
            while (index > 0) {
                node = node.next;
                index--;
            }
            return node.item;
        }
    }

    private Node<T> getHelper(Node<T> node, int index) {
        if (index == 0) {
            return node;
        } else {
            index--;
            return getHelper(node.next, index);
        }
    }

    public T getRecursive(int index) {
        if (index >= nItems) {
            return null;
        } else {
            return getHelper(sentinel.next, index).item;
        }
    }
}
