package util;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileUtil {

    public static Map<String, Integer> readFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String line = null;
        HashMap<String, Integer> si = new HashMap<>();
        int cout = 0;
        int sum = 0;
        while ((line = br.readLine()) != null) {
            sum++;
            if (si.containsKey(line)) cout++;
            si.put(line, 1);
        }
        System.out.println(cout);
        System.out.println(sum);
        return si;
    }

}
