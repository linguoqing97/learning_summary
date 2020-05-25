package com.jdh.fuhsi.portal.util;

import com.jdh.common.model.resp.Resp;
import com.jdh.fuhsi.portal.constant.ResCodeEnum;

/**
 * @ClassName ResponseUtils
 * @Descriptioin response处理工具类
 * @Author linyimin
 * @Date 2020/3/22 16:04
 * @Version 1.0
 */
public class ResponseUtils {

    private ResponseUtils() {
    }

    public static <T> String getMsg(Resp<T> response){
        return response == null? "": response.getHead().getResMsg();
    }

    /**
     * @Description 校验resp是否返回正常
     * @Auther: linyimin
     * @Date: 2020/4/11 19:49
     * @Param
     * @Return:
     */
    public static <T> boolean isRightResp(Resp<T> resp){
        if(resp == null){
            return false;
        }
        return ResCodeEnum.SUCCESS.getResCode().equals(resp.getHead().getResCode());
    }
}
