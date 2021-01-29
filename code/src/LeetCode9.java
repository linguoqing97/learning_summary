public class LeetCode9 {
    public boolean isPalindrome(int x) {
        if(x < 0){
            return false;
        }
        int reX = 0;
        int x1 = x;
        while(x>0){
            int q = x%10;
            x /= 10;
            reX *= 10;
            reX += q;
        }
        System.out.println(reX);
        if (reX == x1){
            return true;
        }
        return false;
    }
}
