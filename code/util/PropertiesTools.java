package com.jdh.fuhsi.portal.util;

import com.jdh.log.LogTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化配置获取工具类(脚手架)
 * @author zz
 * @date 2020/2/27 16:22
 **/
@Component
public class PropertiesTools {

    public String getProperties(String name) {
        try {
            Locale locale = LocaleContextHolder.getLocale();
            return messageSource.getMessage(name, null, locale);
        } catch (NoSuchMessageException e) {
            LogTools.error("获取配置异常!异常信息:{}", e);
        }
        return null;
    }

    @Autowired
    private MessageSource messageSource;

}
