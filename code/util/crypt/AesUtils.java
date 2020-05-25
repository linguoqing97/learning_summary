package com.jdh.fuhsi.portal.util.crypt;

import com.jdh.log.LogTools;
import org.springframework.util.StringUtils;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 * @description:
 * @author: qinzhishen
 * @create: 2020-02-07 11:53
 */
public class AesUtils {

    private static final String S_KEY = "jkl;POIU1234++==";
    private static final String INSTANCE = "AES/ECB/PKCS5Padding";
    private static final String CHAR_SET = "utf-8";
    private static final String AES_MODEL = "AES";


    /**
     * 加密
     *
     * @param sSrc
     * @return
     */
    public static String encrypt(String sSrc) {
        if (StringUtils.isEmpty(sSrc)) {
            return "";
        }
        try {
            byte[] raw = S_KEY.getBytes(CHAR_SET);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, AES_MODEL);
            //算法/模式/补码方式
            Cipher cipher = Cipher.getInstance(INSTANCE);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encrypted = cipher.doFinal(sSrc.getBytes(CHAR_SET));
            return new BASE64Encoder().encode(encrypted);
        } catch (Exception e) {
            LogTools.info("加密出现错误:{}", e.getMessage());
        }
        return "";
    }

    /**
     * 解密
     *
     * @param sSrc
     * @return
     */
    public static String decrypt(String sSrc) {
        if (StringUtils.isEmpty(sSrc)) {
            return "";
        }
        try {
            byte[] raw = S_KEY.getBytes(CHAR_SET);
            SecretKeySpec secretKeySpec = new SecretKeySpec(raw, AES_MODEL);
            Cipher cipher = Cipher.getInstance(INSTANCE);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            // base64 解码
            byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);
            byte[] original = cipher.doFinal(encrypted1);
            String originalString = new String(original, CHAR_SET);
            return originalString;
        } catch (Exception e) {
            LogTools.info("加密出现错误:{}", e.getMessage());
        }
        return sSrc;
    }
}
