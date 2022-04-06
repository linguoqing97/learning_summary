import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        /*LeetCode990 leetCode990 = new LeetCode990();
        String[] a = {"a==b","a==c","e==f", "a==f","a!=f"};
        boolean result = leetCode990.equationsPossible(a);
        System.out.println(result);*/

        /*LeetCode46 leetCode46 = new LeetCode46();
        int a = 22;
        int result = leetCode46.translateNum(a);
        System.out.println(result);*/

        /*LeetCode9 leetCode9 = new LeetCode9();
        int a = 2211;
        boolean palindrome = leetCode9.isPalindrome(a);
        System.out.println(palindrome);*/

        /*LeetCode739 leetCode739 = new LeetCode739();
        int[] a = {73, 74, 75, 71, 69, 72, 76, 73};
        int[] ints = leetCode739.dailyTemperatures(a);
        for (int anInt : ints) {
            System.out.print(anInt);
        }*/

        /*LeetCode15 leetCode15 = new LeetCode15();
        int[] a = {-1,-1, 0, 1, 2, -1, -4};
        System.out.println(leetCode15.threeSum(a).toString());*/

        /*二叉树序列化与反列化 test = new 二叉树序列化与反列化();
        String data = "1,2,3,null,null,4,5";
        TreeNode deserialize = test.deserialize(data);
        String serialize = test.serialize(deserialize);
        System.out.println(serialize.substring(1, data.length()+1));*/

        /*LC1014最佳观光组合 lc1014 = new LC1014最佳观光组合();
        int[] A = {8,1,8,2,6,1,1,1,1,10,10};
        int i = lc1014.maxScoreSightseeingPair(A);
        System.out.println(i);*/

        /*List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        System.out.println(list.size());
        for (Iterator<String> it = list.iterator(); it.hasNext(); ){
            if (it.next().equals("b")){
                it.remove();
            }
        }
        list.removeIf(e -> e.equals("a"));
        System.out.println(list.toString());
        System.out.println(list.size());*/

        /*LC1128等价多米诺骨牌 lc1128 = new LC1128等价多米诺骨牌();
        int[][] A = {{1,2},{2,1},{1,2},{2,1}};
        int i = lc1128.numEquivDominoPairs(A);
        System.out.println(i);*/

        /*int a ;
        int b = 4 ;
        a = b++ ;
        System.out.println(a );
        b=4;
        a = ++b + b++;
        System.out.println(a );
        b=4;
        a = b*10 + ++b + b++;
        System.out.println(a );
        b=4;
        a = ++b + ++b + ++b;
        System.out.println(a );
        b=4;
        a = b++ + b++ + b++;
        System.out.println(a );*/

        /*LC34 lc34 = new LC34();
        int[] ints = new int[]{5,7,7,8,8,10};
        int target = 8;
        System.out.println(lc34.searchRange(ints, target).toString());*/

        /*输出书架 objects = new 输出书架();
        int n = 5;
        int[] a = new int[]{1,2,3,4,5};
        int m = 5;
        int[] b = new int[]{2,6,9,10,11};
        int[] ints = objects.outputNumber(n, a, m, b);
        for (int i = 0; i < m; i++) {
            System.out.println(ints[i]);
        }*/

        /** 处理文件 **/
        File file = new File("D:\\work\\learning_summary\\code\\src\\dealData");
        try {
            Map<String, Integer> si = FileUtil.readFile(file);
            int cout = 0;
            for (String s : si.keySet()) {
                cout++;
                System.out.print(s+",");
            }
            System.out.println(" ");
            System.out.println(cout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
