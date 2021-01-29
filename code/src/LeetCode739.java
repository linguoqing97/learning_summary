import java.util.Stack;

public class LeetCode739 {
    public int[] dailyTemperatures(int[] T) {
        int[] result = new int[T.length];

        /*for (int i = 0; i < T.length; i++){
            for (int j = i+1; j < T.length; j++) {
                if (T[i] < T[j]){
                    result[i] = j-i;
                    break;
                }
            }
        }*/
        Stack<Integer> stack = new Stack<>();
        for (int i = 0; i < T.length; i++) {
            while (!stack.empty() && T[i] > T[stack.peek()]){
                result[stack.peek()] = i-stack.peek();
                stack.pop();
            }
            stack.push(i);
        }
        return result;
    }
}
