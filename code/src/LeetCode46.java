public class LeetCode46 {

    public int translateNum(int num) {
        String a = num+"";
        int i = a.compareTo("25");
        char[] chars = a.toCharArray();
        return digui(chars.length, chars);
    }

    public int digui(int length, char[] chars){
        if (length==1){
            return 1;
        }
        int a = chars[length-1]-48;
        int b = chars[length-2]-48;
        if (length ==2 && (b <2 || (b == 2 && a < 6))){
            return 2;
        }
        if (length ==2 && (b >2 || (b == 2 && a >= 6))){
            return 1;
        }

        if (a + b*10 >= 26 || a+b*10<10) {
            return digui(length-1, chars);
        }else {
            return digui(length-1, chars) + digui(length-2, chars);
        }
    }
}
