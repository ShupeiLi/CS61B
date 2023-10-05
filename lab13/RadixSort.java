/**
 * Class for doing Radix sort
 *
 * @author Akhil Batra, Alexander Hwang
 *
 */
public class RadixSort {
    /**
     * Does LSD radix sort on the passed in array with the following restrictions:
     * The array can only have ASCII Strings (sequence of 1 byte characters)
     * The sorting is stable and non-destructive
     * The Strings can be variable length (all Strings are not constrained to 1 length)
     *
     * @param asciis String[] that needs to be sorted
     *
     * @return String[] the sorted array
     */
    private static final int LENGTH = 256;

    public static String[] sort(String[] asciis) {
        // Max length of strings
        int maxLength = Integer.MIN_VALUE;
        for (String str : asciis) {
            if (str.length() > maxLength) {
                maxLength = str.length();
            }
        }

        // Sorting
        for (int d = maxLength - 1; d >= 0; d--) {
            asciis = sortHelperLSD(asciis, d);
            //sortHelperLSD(asciis, d);
        }

        return asciis;
    }

    /**
     * LSD helper method that performs a destructive counting sort the array of
     * Strings based off characters at a specific index.
     * @param asciis Input array of Strings
     * @param index The position to sort the Strings on.
     * Counting sort implementation
     */
    private static String[] sortHelperLSD(String[] asciis, int index) {
        // Optional LSD helper method for required LSD radix sort
        int[] counts = new int[LENGTH];
        for (String str : asciis) {
            counts[padding(str, index)]++;
        }

        int[] starts = new int[LENGTH];
        int pos = 0;
        for (int i = 0; i < starts.length; i += 1) {
            starts[i] = pos;
            pos += counts[i];
        }

        String[] sorted = new String[asciis.length];
        for (String str : asciis) {
            int idx = padding(str, index);
            sorted[starts[idx]] = str;
            starts[idx]++;
        }
        return sorted;
    }

    /** Quicksort implementation: Have bugs.
    private static void sortHelperLSD(String[] asciis, int index) {
        quicksort(asciis, 0, asciis.length - 1, index);
    }

    private static void quicksort(String[] asciis, int start, int end, int index) {
        if (start < end) {
            int pivot = padding(asciis[end], index);
            int i = start - 1;
            String temp;
            for (int j = start; j < end; j++) {
                if (padding(asciis[j], index) <= pivot) {
                    i++;
                    temp = asciis[i];
                    asciis[i] = asciis[j];
                    asciis[j] = temp;
                }
            }
            i++;
            temp = asciis[i];
            asciis[i] = asciis[end];
            asciis[end] = temp;
            quicksort(asciis, start, i - 1, index);
            quicksort(asciis, i + 1, end, index);
        }
    }
     */

    private static int padding(String str, int index) {
        if (str.length() >= index + 1) {
            return str.charAt(index);
        } else {
            return 0;
        }
    }

    /**
     * MSD radix sort helper function that recursively calls itself to achieve the sorted array.
     * Destructive method that changes the passed in array, asciis.
     *
     * @param asciis String[] to be sorted
     * @param start int for where to start sorting in this method (includes String at start)
     * @param end int for where to end sorting in this method (does not include String at end)
     * @param index the index of the character the method is currently sorting on
     *
     **/
    private static void sortHelperMSD(String[] asciis, int start, int end, int index) {
        // Optional MSD helper method for optional MSD radix sort
        return;
    }
}
