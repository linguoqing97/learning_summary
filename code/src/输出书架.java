public class 输出书架 {

    public int[] outputNumber(int n, int[] a, int m, int[] b) {
        int[] result = new int[m];
        int[] find = new int[n+1];
        find[0] = 0;
        for (int i = 1; i<(n+1); i++){
            find[i] = find[i-1] + a[i-1];
        }
        for (int i = 0; i<m; i++) {
            int left = 1;
            int right = n;
            while (right-left > 1){
                int middle = (left+right)/2;
                if (b[i] > find[middle]){
                    left = middle;
                }else{
                    right = middle;
                }
            }
            if (b[i] > find[left]){
                result[i] = left+1;
            }else {
                result[i] = left;
            }
        }
        return result;

    }

}
