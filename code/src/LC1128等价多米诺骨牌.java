import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LC1128等价多米诺骨牌 {
    public int numEquivDominoPairs(int[][] dominoes) {
        int ans = 0;
        /** 暴力
        for (int i = 0; i < dominoes.length; i++) {
            for (int j = i+1; j < dominoes.length; j++) {
                if (dominoes[i][0] == dominoes[j][0] && dominoes[i][1] == dominoes[j][1]){
                    ans++;
                    continue;
                }
                if (dominoes[i][0] == dominoes[j][1] && dominoes[i][1] == dominoes[j][0]){
                    ans++;
                    continue;
                }
            }
        }
         **/
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        for (int i = 0; i < dominoes.length; i++) {
            if (dominoes[i][0] > dominoes[i][1]){
                int temp = dominoes[i][0];
                dominoes[i][0] = dominoes[i][1];
                dominoes[i][1] = temp;
            }
            int sum = dominoes[i][0]*10+dominoes[i][1];
            if (Objects.nonNull(map.get(sum))) {
                map.put(sum, map.get(sum)+1);
            }else{
                map.put(sum, 1);
            }
        }
        for (Integer value : map.values()) {
            while(value>0){
                ans+=(--value);
            }
        }
        return ans;
    }
}
