public class Palindrome {
    public Deque<Character> wordToDeque(String word) {
        Deque<Character> output = new LinkedListDeque<>();
        for (char s : word.toCharArray()) {
            output.addLast(s);
        }
        return output;
    }

    private boolean isPalindromeHelper(Deque<Character> wordDeque) {
        if (wordDeque.size() <= 1) {
            return true;
        } else {
            Character first = wordDeque.removeFirst();
            Character last = wordDeque.removeLast();
            return (first.equals(last) && isPalindromeHelper(wordDeque));
        }
    }

    public boolean isPalindrome(String word) {
        if (word.length() <= 1) {
            return true;
        } else {
            Deque<Character> wordDeque = wordToDeque(word);
            return isPalindromeHelper(wordDeque);
        }
    }

    private boolean isPalindromeHelper(Deque<Character> wordDeque, CharacterComparator cc) {
        if (wordDeque.size() <= 1) {
            return true;
        } else {
            Character first = wordDeque.removeFirst();
            Character last = wordDeque.removeLast();
            return (cc.equalChars(first, last) && isPalindromeHelper(wordDeque, cc));
        }
    }

    public boolean isPalindrome(String word, CharacterComparator cc) {
        if (word.length() <= 1) {
            return true;
        } else {
            Deque<Character> wordDeque = wordToDeque(word);
            return isPalindromeHelper(wordDeque, cc);
        }
    }
}
