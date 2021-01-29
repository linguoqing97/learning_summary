import java.util.HashMap;
import java.util.Map;

public class LC1014最佳观光组合 {
    public int maxScoreSightseeingPair(int[] a) {
        int value = 0;
        int ii = a[0] + 0;
        for (int i = 1; i < a.length; i++) {
            value = Math.max(value, ii + a[i]-i);
            ii = Math.max(ii, a[i]+i);
        }
        return value;
    }
}
