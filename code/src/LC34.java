public class LC34 {

    public int[] searchRange(int[] nums, int target) {
        int[] result = new int[2];

        int length = nums.length;
        int max = -1, min = -1;
        // 特殊情况直接返回-1，-1
        if (length == 0 || nums[0] > target || nums[length-1] < target){
            return new int[]{-1,-1};
        }

        int left = 0;
        int right = length-1;
        while(right != left){
            if (nums[right] == target){
                max = right;
                break;
            }
            int middle = (right+left)/2;
            if (nums[middle] > target){
                right = middle-1;
            }
            if (nums[middle] <= target){
                left = middle +1;
            }
            if (nums[middle] == target){
                max = middle;
            }
        }
        if (nums[right] == target){
            max = right;
        }

        left = 0;
        right = length-1;
        while(right != left){
            if (nums[left] == target){
                min = left;
                break;
            }
            int middle = (right+left)/2;
            if (nums[middle] < target){
                left = middle+1;
            }
            if (nums[middle] >= target){
                right = middle-1;
            }
            if (nums[middle] == target){
                min = middle;
            }
        }
        if (nums[left] == target){
            min = left;
        }
        result[0] = min;
        result[1] = max;
        return result;
    }

}
