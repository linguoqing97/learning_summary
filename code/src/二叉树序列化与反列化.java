import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 20200616 linguoqing
 */

public class 二叉树序列化与反列化 {

    public String serialize(TreeNode root) {
        StringBuffer sb = new StringBuffer();
        xianXu(root, sb);
        return sb.toString();
    }

    /*
     * 先序遍历,生成字符串，逗号分隔
     */
    public void xianXu(TreeNode node, StringBuffer sb){
        if (node == null || "null,".contains(String.valueOf(node.val))){
            sb.append(",null");
            return;
        }else{
            sb.append("," + String.valueOf(node.val));
        }
        xianXu(node.left, sb);
        xianXu(node.right, sb);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        String[] split = data.split(",");
        List<String> list = new LinkedList<>(Arrays.asList(split));
        TreeNode root = xianXu_deserialize(list);
        return root;

    }

    /*
     * 先序遍历，反序列化
     */
    public TreeNode xianXu_deserialize(List<String> list){
        if (list.size() == 0) {
            return null;
        }
        if ("null".contains(list.get(0))){
            list.remove(0);
            return null;
        }
        TreeNode node = new TreeNode(Integer.valueOf(list.get(0)));
        list.remove(0);
        node.left = xianXu_deserialize(list);
        node.right = xianXu_deserialize(list);
        return node;
    }
}
