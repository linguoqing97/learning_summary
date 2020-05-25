package com.jdh.fuhsi.portal.util;

import com.jdh.common.model.resp.Resp;
import com.jdh.fuhsi.portal.constant.ResCodeEnum;
import com.jdh.fuhsi.portal.exception.*;
import com.jdh.log.LogTools;

/**
 * 异常处理工具类
 *
 * @author lym
 * @date 2019/02/26
 */
public class ExceptionUtils {

    /**
     * 业务异常
     * @param condition 条件，满足时抛指定异常
     * @param errMsg 异常信息
     */
    public static void businessException(boolean condition, String errMsg) {
        if (condition) {
            throw new BusinessException(errMsg);
        }
    }
    /**
     * 业务异常
     * @param errMsg 异常信息
     */
    public static void businessException(String errMsg) {
            throw new BusinessException(errMsg);
    }

    /**
     * 系统异常
     * @param condition 条件，满足时抛指定异常
     * @param errMsg 异常信息
     */
    public static void systemException(boolean condition, String errMsg) {
        if (condition) {
            throw new SystemException(errMsg);
        }
    }


    /**
     * 参数校验异常
     * @param condition 条件，满足时抛指定异常
     * @param errMsg 异常信息
     */
    public static void validationException(boolean condition, String errMsg) {
        if (condition) {
            throw new ValidationException(errMsg);
        }
    }


    /**
     * 客户中心cif异常
     * @param response 客户中心返回response
     */
    public static <T> void cifException(Resp<T> response) {
        if (response == null || !ResCodeEnum.SUCCESS.getResCode().equals(response.getHead().getResCode())) {
            LogTools.error(ResponseUtils.getMsg(response));
            throw new CifException("客户服务系统繁忙，请稍候");
        }
    }

    /**
     * 登录异常
     * @param condition 条件，满足时抛指定异常
     * @param errMsg 异常信息
     */
    public static void tokenException(boolean condition, String errMsg) {
        if (condition) {
            throw new TokenException(errMsg);
        }
    }


    /**
     * 短信验证码已发送 提示
     * @param condition 条件，满足时抛指定异常
     * @param errMsg 异常信息
     */
    public static void isSendException(boolean condition, String errMsg) {
        if (condition) {
            throw new IsSendException(errMsg);
        }
    }

}
