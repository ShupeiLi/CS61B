import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestArrayDequeGold {
    private static final int NUMTEST = 500;
    private static final int TEST_LENGTH = 20;
    private static final int UPPER = 100;
    private static final double CHECK = 0.75;

    private String testAddFirst(StudentArrayDeque<Integer> testDeque,
                              ArrayDequeSolution<Integer> solDeque,
                              String msg) {
        Integer nextElement = StdRandom.uniform(0, UPPER);
        testDeque.addFirst(nextElement);
        solDeque.addFirst(nextElement);
        Integer test = testDeque.get(0);
        Integer sol = solDeque.get(0);
        msg = msg + "\naddFirst(" + nextElement + ")";
        assertEquals(msg, sol, test);
        return msg;
    }

    private String testAddLast(StudentArrayDeque<Integer> testDeque,
                             ArrayDequeSolution<Integer> solDeque,
                             String msg) {
        Integer nextElement = StdRandom.uniform(0, UPPER);
        testDeque.addLast(nextElement);
        solDeque.addLast(nextElement);
        Integer test = testDeque.get(testDeque.size() - 1);
        Integer sol = solDeque.get(solDeque.size() - 1);
        msg = msg + "\naddLast(" + nextElement + ")";
        assertEquals(msg, sol, test);
        return msg;
    }

    private String testRemoveFirst(StudentArrayDeque<Integer> testDeque,
                                 ArrayDequeSolution<Integer> solDeque,
                                 String msg) {
        Integer test = testDeque.removeFirst();
        Integer sol = solDeque.removeFirst();
        msg = msg + "\nremoveFirst()";
        assertEquals(msg, sol, test);
        return msg;
    }

    private String testRemoveLast(StudentArrayDeque<Integer> testDeque,
                                ArrayDequeSolution<Integer> solDeque,
                                String msg) {
        Integer test = testDeque.removeLast();
        Integer sol = solDeque.removeLast();
        msg = msg + "\nremoveLast()";
        assertEquals(msg, sol, test);
        return msg;
    }

    @Test
    public void testProject1() {
        StudentArrayDeque<Integer> testDeque;
        ArrayDequeSolution<Integer> solDeque;
        String msg;
        for (int i = 0; i < NUMTEST; i++) {
            testDeque = new StudentArrayDeque<>();
            solDeque = new ArrayDequeSolution<>();
            msg = "";
            for (int j = 0; j < TEST_LENGTH; j++) {
                double probability = StdRandom.uniform();
                if (testDeque.isEmpty() && solDeque.isEmpty()) {
                    if (probability < 0.5) {
                        msg = testAddFirst(testDeque, solDeque, msg);
                    } else {
                        msg = testAddLast(testDeque, solDeque, msg);
                    }
                } else if (!testDeque.isEmpty() && !solDeque.isEmpty()) {
                    if (probability < 0.25) {
                        msg = testAddFirst(testDeque, solDeque, msg);
                    } else if (probability >= 0.25 && probability < 0.5) {
                        msg = testAddLast(testDeque, solDeque, msg);
                    } else if (probability >= 0.5 && probability < CHECK) {
                        msg = testRemoveFirst(testDeque, solDeque, msg);
                    } else {
                        msg = testRemoveLast(testDeque, solDeque, msg);
                    }
                } else {
                    throw new RuntimeException("The length of the deque is incorrect.");
                }
            }
        }
    }
}
