import java.util.Arrays;

//3 66 29 113 157 218 80 70 122 181 183 35 210
public class RadixSortTest {
    private static final String[] testString = {
            "56", "12", "112", "1", "94", "4", "9", "82", "394", "80"
    };

    public static void main(String[] args) {
        System.out.println(Arrays.toString(RadixSort.sort(testString)));
    }
}
