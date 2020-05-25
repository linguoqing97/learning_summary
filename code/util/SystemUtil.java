package com.jdh.fuhsi.portal.util;

import com.jdh.common.model.UserForJwt;
import com.jdh.crypto.JwtUtils;
import com.jdh.fuhsi.api.model.core.CifEnterpriseRes;
import com.jdh.fuhsi.portal.constant.common.CacheCons;
import com.jdh.fuhsi.portal.constant.common.ExpiresEnum;
import com.jdh.fuhsi.portal.constant.user.UserPromptEnums;
import com.jdh.fuhsi.portal.model.common.entity.res.LoginRes;
import com.jdh.log.LogTools;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 系统信息相关工具类
 *
 * @author chenxuan
 * @date 2020-03-10 16:03
 **/
@Component
@RefreshScope
public class SystemUtil {

    @Autowired
    private PropertiesTools propertiesTools;
    @Autowired
    RedisTools redisTools;
    /**
     * token续期时间间隔--默认30分钟
     */
    @Value("${tokenRenewInterval}")
    private String tokenRenewInterval;


    /**
     * 获取当前登录用户信息
     *
     * @return
     */
    public UserForJwt getCurrentUser() {
        String token = SpringContextUtils.getToken();
        ExceptionUtils.tokenException(StringUtils.isBlank(token), propertiesTools.getProperties(UserPromptEnums.PLEASE_LOGIN.getCode()));
        LogTools.info("请求token:",token);
        return JwtUtils.getCurrentUser(token);
    }

    /**
     * 获取当前登录企业信息
     */
    public CifEnterpriseRes getCurrentEnt() {
        String key = CacheCons.LOGIN_USER_KEY + getCurrentUser().getUserid();
        long expire = redisTools.getExpire(key, ExpiresEnum.ACCOUNT_ERRINPUT.getTimeUnit());
        // 如果剩余时间少于30分钟,再给用户会话加30分钟
        long renewInterval = Long.parseLong(tokenRenewInterval);
        if (expire < renewInterval) {
            expire = expire + 30L;
            redisTools.set(key, redisTools.get(key), expire, ExpiresEnum.ACCOUNT_ERRINPUT.getTimeUnit());
        }
        LoginRes loginRes = (LoginRes) redisTools.get(key);
        ExceptionUtils.tokenException(ObjectUtils.isEmpty(loginRes)
                || ObjectUtils.isEmpty(loginRes.getEntUser())
                || ObjectUtils.isEmpty(loginRes.getEntUser().getCurrentUserEnt()), propertiesTools.getProperties(UserPromptEnums.PLEASE_LOGIN.getCode()));
        return loginRes.getEntUser().getCurrentUserEnt();
    }

    /**
     * 生成随机i位数
     *
     * @param i
     * @return
     */
    public static int getRandomInt(int i) {
        return (int) (Math.random() * Math.pow(10.0D, (double) i) + Math.pow(10.0D, (double) i));
    }

    private static final String REPL_TARGET = "T";
    private static final String REPLACEMENT = " ";

    /**
     * LocalDateTime转String
     * @param localDateTime
     * @return
     */
    public static String replaceLocalDateTime(LocalDateTime localDateTime) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String time = dateTimeFormatter.format(localDateTime);
        return time;
    }
}
