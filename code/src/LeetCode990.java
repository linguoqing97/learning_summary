import java.util.*;

public class LeetCode990 {

    /*public boolean equationsPossible(String[] equations) {
        Map<Character, String> equalMap = new HashMap<>();
        Map<Integer, String> unEqualMap = new HashMap<>();
        Arrays.sort(equations);
        int j = 1;
        for (String equation : equations) {
            char a = equation.charAt(0);
            char b = equation.charAt(3);
            if (!equation.contains("!")){
                equalMap = createMap(a, b, equalMap);
            }else {
                unEqualMap.put(j++, new StringBuilder().append("").append(a).append(b).toString());
            }
        }

        for (Character character1 : equalMap.keySet()) {
            for (Character character2 : equalMap.keySet()) {
                String s1 = equalMap.get(character1);
                String s2 = equalMap.get(character2);
                if (s1.equals(s2)){
                    break;
                }
                char[] s2ToChar = s2.toCharArray();
                for (char c : s2ToChar) {
                    if (s1.contains(c+"")){
                        equalMap.put(character1, s1+s2);
                        break;
                    }
                }
            }
        }

        for (String str : unEqualMap.values()){
            char[] strToChar = str.toCharArray();
            if (strToChar[0] == strToChar[1]){
                return false;
            }
        }
        for(String str1 : equalMap.values()){
            for (String str2 : unEqualMap.values()){
                char[] str2ToChar = str2.toCharArray();
                int i = 0;
                for (char c : str2ToChar) {
                    if (str1.contains(c+"")){
                        i++;
                    }
                    if (i>1){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public Map<Character, String> createMap(char a, char b, Map<Character, String> map){
        if (map.containsKey(a)){
            if (!map.get(a).contains(b+"")) {
                String in = new StringBuilder().append("").append(map.get(a)).append(b).toString();
                map.put(a, in);
            }
        }else if(map.containsKey(b)){
            if (!map.get(b).contains(a+"")) {
                String in = new StringBuilder().append("").append(map.get(b)).append(a).toString();
                map.put(b, in);
            }
        }else {
            for (Character key : map.keySet()){
                if (map.get(key).contains(a+"")){
                    String in =  new StringBuilder().append(map.get(key)).append(b).toString();
                    System.out.println(in);
                    map.put(key, in);
                    return map;
                }
                else if (map.get(key).contains(b+"")) {
                    String in = new StringBuilder().append(map.get(key)).append(a).toString();
                    System.out.println(in);
                    map.put(key, in);
                    return map;
                }
            }
            String in =  new StringBuilder().append("").append(a).append(b).toString();
            map.put(a, in);
            return map;
        }
        return map;
    }*/
    public boolean equationsPossible(String[] equations) {
        int length = equations.length;
        int[] parent = new int[26];
        for (int i = 0; i < 26; i++) {
            parent[i] = i;
        }
        for (String str : equations) {
            if (str.charAt(1) == '=') {
                int index1 = str.charAt(0) - 'a';
                int index2 = str.charAt(3) - 'a';
                union(parent, index1, index2);
            }
        }
        for (String str : equations) {
            if (str.charAt(1) == '!') {
                int index1 = str.charAt(0) - 'a';
                int index2 = str.charAt(3) - 'a';
                if (find(parent, index1) == find(parent, index2)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void union(int[] parent, int index1, int index2) {
        parent[find(parent, index1)] = find(parent, index2);
    }

    public int find(int[] parent, int index) {
        while (parent[index] != index) {
            parent[index] = parent[parent[index]];
            index = parent[index];
        }
        return index;
    }
}
