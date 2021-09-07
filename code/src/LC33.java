public class LC33 {

    public int search(int[] nums, int target) {
        int length = nums.length;
        if (target < nums[0] && target > nums[length-1]){
            return -1;
        }

        return 0;
    }

}
