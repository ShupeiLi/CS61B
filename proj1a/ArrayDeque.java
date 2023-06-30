public class ArrayDeque<T> {
    private static final int ARR_LIMIT = 16;
    private T[] array = (T[]) new Object[8];
    private int start = array.length - 1;
    private int end = 0;
    private int nItems = 0;

    public ArrayDeque() { }

    private void resizeArray(boolean increase) {
        T[] newArray;
        if (increase) {
            newArray = (T[]) new Object[2 * array.length];
        } else {
            newArray = (T[]) new Object[array.length / 2];
        }
        int pointer = 0;
        if (start + 1 > end - 1) {
            int count = nItems;
            for (int i = start + 1; i < array.length && count > 0; i++) {
                newArray[pointer] = array[i];
                pointer++;
                count--;
            }
            for (int i = 0; i < end && count > 0; i++) {
                newArray[pointer] = array[i];
                pointer++;
                count--;
            }
        } else {
            for (int i = start + 1; i < end; i++) {
                newArray[pointer] = array[i];
                pointer++;
            }
        }
        array = newArray;
        start = array.length - 1;
        end = pointer;
    }

    public void addFirst(T item) {
        if ((start == end) || (start == 0)) {
            resizeArray(true);
        }
        array[start] = item;
        start--;
        nItems++;
    }

    public void addLast(T item) {
        if ((start == end) || (end == (array.length - 1))) {
            resizeArray(true);
        }
        array[end] = item;
        end++;
        nItems++;
    }

    public boolean isEmpty() {
        return nItems == 0;
    }

    public int size() {
        return nItems;
    }

    public void printDeque() {
        if (start >= end) {
            for (int i = start + 1; i < array.length; i++) {
                System.out.print(array[i] + " ");
            }
            for (int i = 0; i < end; i++) {
                System.out.print(array[i] + " ");
            }
        } else {
            for (int i = start + 1; i < end; i++) {
                System.out.print(array[i] + " ");
            }
        }
        System.out.print("\n");
    }

    public T removeFirst() {
        if (isEmpty()) {
            return null;
        } else {
            if ((start + 1) == array.length) {
                start = end + 1;
            } else {
                start++;
            }
            T temp = array[start];
            nItems--;
            if (array.length > ARR_LIMIT && (((double) nItems / array.length) < 0.25)) {
                resizeArray(false);
            }
            return temp;
        }
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        } else {
            if (end == 0) {
                end = start - 1;
            } else {
                end--;
            }
            T temp = array[end];
            nItems--;
            if (array.length > ARR_LIMIT && (((double) nItems / array.length) < 0.25)) {
                resizeArray(false);
            }
            return temp;
        }
    }

    public T get(int index) {
        if (isEmpty()) {
            return null;
        } else if (index < array.length - start - 1) {
            return array[start + 1 + index];
        } else {
            return array[index - (array.length - start - 1)];
        }
    }
}
