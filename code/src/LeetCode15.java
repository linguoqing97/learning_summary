import java.util.*;

public class LeetCode15 {

    /*public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> resultList = new ArrayList<>();
        Set<List<Integer>> resultSet = new HashSet<>();
        Map<int[], Integer> sumMap = new HashMap<>();
        for (int i = 0; i < nums.length-1; i++) {
            for (int j = i+1; j < nums.length; j++) {
                int value = nums[i] + nums[j];
                int[] key = {i,j};
                sumMap.put(key, value);
            }
        }
        for (int i = 0; i < nums.length; i++) {
            for (int[] ints : sumMap.keySet()) {
                int value = sumMap.get(ints);
                if ((nums[i] + value) == 0 && i!=ints[0] && i!=ints[1]){
                    int[] q = {nums[i], nums[ints[0]], nums[ints[1]]};
                    List<Integer> integers = simpleSort(q);
                    resultSet.add(integers);
                }
            }
        }
        resultList.addAll(resultSet);
        return resultList;
    }

    public List<Integer> simpleSort(int[] a){
        List<Integer> numList = new ArrayList<>();
        Arrays.sort(a);
        numList.add(a[0]);
        numList.add(a[1]);
        numList.add(a[2]);
        return numList;
    }*/

    public List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> resultList = new ArrayList<>();
        Arrays.sort(nums);
        int n = nums.length;
        for (int i = 0; i < n; i++) {
            if (i>0 && nums[i]==nums[i-1] || nums[i]>0){
                continue;
            }
            int right = n-1;
            int target = -nums[i];
            for (int j = i+1; j < n; j++) {
                if (j>(i+1) && nums[j]==nums[j-1]){
                    continue;
                }
                while (j < right && nums[j] + nums[right] > target) {
                    right--;
                }
                if (j == right){
                    break;
                }
                if (nums[j] + nums[right] == target) {
                    List<Integer> tmp = new ArrayList<>();
                    tmp.add(nums[i]);
                    tmp.add(nums[j]);
                    tmp.add(nums[right]);
                    resultList.add(tmp);
                }
            }
        }
        return resultList;
    }
}