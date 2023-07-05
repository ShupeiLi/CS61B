public class OffByN implements CharacterComparator {
    private int offValue;

    public OffByN(int offValue) {
        this.offValue = offValue;
    }

    @Override
    public boolean equalChars(char x, char y) {
        int diff = x - y;
        return (diff == this.offValue || diff == -this.offValue);
    }
}
